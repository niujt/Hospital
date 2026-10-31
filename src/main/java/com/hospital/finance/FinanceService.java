package com.hospital.finance;

import com.alibaba.fastjson2.JSON;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;

@Service
@Transactional
public class FinanceService {
    private final JdbcTemplate db;
    private final com.hospital.finance.auth.PaymentSecurityService paymentSecurity;
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final BigDecimal ZERO = new BigDecimal("0.00");
    public FinanceService(JdbcTemplate db, com.hospital.finance.auth.PaymentSecurityService paymentSecurity) { this.db = db; this.paymentSecurity = paymentSecurity; }

    public int patientFor(int loginId) {
        List<Integer> ids = db.query("select id from patient where loginid=?", (rs,n)->rs.getInt(1), loginId);
        if (ids.size()!=1) throw fail(403,"当前账号未唯一关联患者档案");
        return ids.get(0);
    }
    public int doctorFor(int loginId) {
        List<Integer> ids = db.query("select id from doctor where loginid=?", (rs,n)->rs.getInt(1), loginId);
        if (ids.size()!=1) throw fail(403,"当前账号未唯一关联医生档案");
        return ids.get(0);
    }
    public Map<String,Object> wallet(int patientId) {
        ensurePatient(patientId); ensureWallet(patientId);
        return map("patientId",patientId,"balance",decimal(db.queryForObject(
                "select balance from finance_wallet where patient_id=?",BigDecimal.class,patientId)));
    }
    public Map<String,Object> recharge(int actor,int patient,String key,String amount,String channel,String paymentPassword,String cardId) {
        paymentSecurity.verify(patient,paymentPassword);
        if ("BANK".equals(channel)) { Map<String,Object> card=one("select patient_id from finance_demo_card where id=?",cardId); if(num(card,"patient_id")!=patient) throw fail(403,"模拟卡不属于本人"); }
        if (!Arrays.asList("WECHAT","ALIPAY","BANK").contains(channel)) throw fail(400,"仅支持模拟微信、支付宝或银行卡");
        BigDecimal value=positive(amount); ensurePatient(patient);
        return operation(actor,key,"RECHARGE|"+patient+"|"+value+"|"+channel+"|"+cardId, op->{
            String id=id(); db.update("insert into finance_recharge(id,patient_id,amount,channel,status,requested_at) values(?,?,?,?,?,?)",
                    id,patient,value,channel,"PENDING",now());
            return map("id",id,"status","PENDING","amount",decimal(value),"channel",channel);
        });
    }
    public Map<String,Object> confirmRecharge(int actor,String key,String rechargeId) {
        return operation(actor,key,"CONFIRM|"+rechargeId,op->{
            Map<String,Object> r=one("select * from finance_recharge where id=? for update",rechargeId);
            if ("CONFIRMED".equals(r.get("status"))) return map("id",rechargeId,"status","CONFIRMED");
            if(!"PENDING".equals(r.get("status")))throw fail(409,"充值状态不允许确认");
            int patient=num(r,"patient_id"); BigDecimal amount=money(r,"amount");
            credit(patient,amount);
            if(db.update("update finance_recharge set status='CONFIRMED',confirmed_by=?,confirmed_at=? where id=? and status='PENDING'",actor,now(),rechargeId)!=1)throw fail(409,"充值状态已变化");
            entry(op,"CASH","IN","RECHARGE",patient,amount,rechargeId,null,actor);
            entry(op,"PATIENT_BALANCE","IN","RECHARGE",patient,amount,rechargeId,null,actor);
            return map("id",rechargeId,"status","CONFIRMED","amount",decimal(amount));
        });
    }
    public Map<String,Object> setPolicy(int actor,String key,int drugId,String rate) {
        BigDecimal value;
        if(rate==null||!rate.matches("[0-9]{1,3}(\\.[0-9]{1,4})?"))throw fail(400,"提成百分比格式无效");
        try { value=new BigDecimal(rate).setScale(4,RoundingMode.UNNECESSARY); }
        catch(Exception ex) { throw fail(400,"提成百分比最多四位小数"); }
        if(value.signum()<0||value.compareTo(new BigDecimal("100"))>0) throw fail(400,"提成百分比必须在0到100之间");
        final BigDecimal policy=value;
        return operation(actor,key,"POLICY|"+drugId+"|"+policy,op->{
            one("select id from drugs where id=? for update",drugId);
            if(db.update("update finance_drug_policy set commission_rate=?,updated_by=?,updated_at=? where drug_id=?",policy,actor,now(),drugId)==0)
                db.update("insert into finance_drug_policy(drug_id,commission_rate,updated_by,updated_at) values(?,?,?,?)",drugId,policy,actor,now());
            return map("drugId",drugId,"commissionRate",policy.toPlainString());
        });
    }
    public Map<String,Object> completeEncounter(int actor,String key,int appointmentId) {
        int doctor=doctorFor(actor);
        return operation(actor,key,"COMPLETE|"+appointmentId,op->{
            Map<String,Object> appointment=one("select * from appointment where id=? for update",appointmentId);
            if(num(appointment,"doctorid")!=doctor) throw fail(403,"不能完成其他医生的接诊");
            Object scheduled=appointment.get("time");
            if(scheduled instanceof java.sql.Date && ((java.sql.Date)scheduled).toLocalDate().isAfter(LocalDate.now(ZONE)))
                throw fail(400,"未来预约不能提前标记接诊完成");
            List<Map<String,Object>> existing=rows("select id from finance_encounter where appointment_id=?",appointmentId);
            if(!existing.isEmpty()) return map("id",existing.get(0).get("id"),"status","COMPLETED");
            String encounter=id();
            db.update("insert into finance_encounter(id,appointment_id,patient_id,doctor_id,completed_at) values(?,?,?,?,?)",
                    encounter,appointmentId,num(appointment,"patientid"),doctor,now());
            return map("id",encounter,"status","COMPLETED");
        });
    }
    private Map<String,Object> sale(int actor,int role,String key,int patient,int drugId,int quantity,String method,String encounterId,BigDecimal quotedUnit) {
        if(quantity<=0) throw fail(400,"数量必须为正整数");
        if(!Arrays.asList("WALLET","CASH").contains(method)) throw fail(400,"支付方式无效");
        if("CASH".equals(method)&&role!=1) throw fail(403,"线下现金销售仅由管理员登记");
        if(role==3&&patientFor(actor)!=patient) throw fail(403,"不能购买到其他患者账户");
        ensurePatient(patient);
        return operation(actor,key,"SALE|"+patient+"|"+drugId+"|"+quantity+"|"+method+"|"+encounterId,op->{
            Integer doctor=null;
            if(encounterId!=null&&!encounterId.trim().isEmpty()) {
                Map<String,Object> encounter=one("select * from finance_encounter where id=?",encounterId);
                if(num(encounter,"patient_id")!=patient) throw fail(403,"接诊记录不属于该患者");
                doctor=num(encounter,"doctor_id");
                if(role==2&&doctorFor(actor)!=doctor) throw fail(403,"接诊记录不属于当前医生");
            } else if(role==2) throw fail(400,"医生销售需要自己的已完成接诊记录");
            Map<String,Object> drug=one("select * from drugs where id=? for update",drugId);
            BigDecimal unit=quotedUnit; if(unit==null||unit.signum()<=0) throw fail(400,"药品价格必须为正");
            BigDecimal amount=bounded(unit.multiply(BigDecimal.valueOf(quantity)).setScale(2,RoundingMode.UNNECESSARY));
            if(num(drug,"number")<quantity) throw fail(409,"药品库存不足");
            BigDecimal rate=ZERO;
            if(doctor!=null) { List<BigDecimal> rates=db.query("select commission_rate from finance_drug_policy where drug_id=? for update",(rs,n)->rs.getBigDecimal(1),drugId); if(!rates.isEmpty()) rate=rates.get(0); }
            BigDecimal commission=amount.multiply(rate).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP);
            if("WALLET".equals(method)) debit(patient,amount);
            if(db.update("update drugs set number=number-? where id=? and number>=?",quantity,drugId,quantity)!=1) throw fail(409,"药品库存不足");
            String sale=id();
            db.update("insert into finance_sale(id,patient_id,drug_id,quantity,unit_price,amount,payment_method,encounter_id,doctor_id,commission_rate,commission,created_at) values(?,?,?,?,?,?,?,?,?,?,?,?)",
                    sale,patient,drugId,quantity,unit,amount,method,encounterId,doctor,rate,commission,now());
            entry(op,"WALLET".equals(method)?"PATIENT_BALANCE":"CASH","WALLET".equals(method)?"OUT":"IN","DRUG_SALE",patient,amount,sale,null,actor);
            return map("id",sale,"amount",decimal(amount),"paymentMethod",method,"status","COMPLETED");
        });
    }
    public Map<String,Object> refund(int actor,String key,String saleId,String amount,String reason) {
        BigDecimal requested=positive(amount); text(reason,500,"退款原因");
        return operation(actor,key,"REFUND|"+saleId+"|"+requested+"|"+reason,op->{
            Map<String,Object> sale=one("select * from finance_sale where id=? for update",saleId);
            BigDecimal original=money(sale,"amount"),already=money(sale,"refunded_amount");
            if(already.add(requested).compareTo(original)>0) throw fail(409,"累计退款超过原销售金额");
            BigDecimal total=already.add(requested), originalCommission=money(sale,"commission");
            BigDecimal cumulative=total.compareTo(original)==0?originalCommission:
                    originalCommission.multiply(total).divide(original,2,RoundingMode.HALF_UP).min(originalCommission);
            BigDecimal reversal=cumulative.subtract(money(sale,"reversed_commission"));
            int patient=num(sale,"patient_id"); credit(patient,requested); String refund=id();
            db.update("insert into finance_refund(id,sale_id,amount,reversed_commission,reason,actor_id,created_at) values(?,?,?,?,?,?,?)",
                    refund,saleId,requested,reversal,reason,actor,now());
            db.update("update finance_sale set refunded_amount=?,reversed_commission=? where id=?",total,cumulative,saleId);
            entry(op,"PATIENT_BALANCE","IN","REFUND",patient,requested,refund,saleId,actor);
            return map("id",refund,"saleId",saleId,"amount",decimal(requested),"destination","BALANCE","status","COMPLETED");
        });
    }
    public Map<String,Object> procure(int actor,String key,int drugId,int quantity,String unitCost,String reference) {
        if(quantity<=0) throw fail(400,"进货数量必须为正整数");
        BigDecimal cost=positive(unitCost), amount=bounded(cost.multiply(BigDecimal.valueOf(quantity))); text(reference,200,"进货凭据");
        return operation(actor,key,"PROCURE|"+drugId+"|"+quantity+"|"+cost+"|"+reference,op->{
            Map<String,Object> drug=one("select * from drugs where id=? for update",drugId);
            if((long)num(drug,"number")+quantity>Integer.MAX_VALUE) throw fail(400,"库存数量超出范围");
            db.update("update drugs set number=number+? where id=?",quantity,drugId); String procurement=id();
            db.update("insert into finance_procurement(id,drug_id,quantity,unit_cost,amount,reference_text,actor_id,created_at) values(?,?,?,?,?,?,?,?)",
                    procurement,drugId,quantity,cost,amount,reference,actor,now());
            entry(op,"CASH","OUT","PROCUREMENT",null,amount,procurement,null,actor);
            return map("id",procurement,"amount",decimal(amount),"status","COMPLETED");
        });
    }
    private Map<String,Object> operation(int actor,String key,String payload,Function<String,Map<String,Object>> action) {
        text(key,128,"Idempotency-Key"); String fingerprint=sha(payload), operation=id();
        try { db.update("insert into finance_operation(id,actor_id,request_key,fingerprint,created_at) values(?,?,?,?,?)",operation,actor,key,fingerprint,now()); }
        catch(DuplicateKeyException duplicate) {
            Map<String,Object> previous=one("select * from finance_operation where actor_id=? and request_key=? for update",actor,key);
            if(!fingerprint.equals(previous.get("fingerprint"))) throw fail(409,"幂等键已用于不同请求");
            if(previous.get("result_json")==null) throw fail(409,"请求正在处理，请稍后重试");
            return JSON.parseObject((String)previous.get("result_json"));
        }
        Map<String,Object> result=action.apply(operation);
        db.update("update finance_operation set result_json=? where id=?",JSON.toJSONString(result),operation);
        return result;
    }
    private void ensurePatient(int patient) { one("select id from patient where id=?",patient); }
    private void ensureWallet(int patient) {
        if(db.queryForObject("select count(*) from finance_wallet where patient_id=?",Integer.class,patient)==0)
            try { db.update("insert into finance_wallet(patient_id,balance) values(?,0)",patient); } catch(DuplicateKeyException ignored) { }
    }
    private void credit(int patient,BigDecimal amount) {
        ensureWallet(patient); Map<String,Object> wallet=one("select balance from finance_wallet where patient_id=? for update",patient);
        BigDecimal balance=bounded(money(wallet,"balance").add(amount));
        db.update("update finance_wallet set balance=? where patient_id=?",balance,patient);
    }
    private void debit(int patient,BigDecimal amount) {
        ensureWallet(patient); one("select balance from finance_wallet where patient_id=? for update",patient);
        if(db.update("update finance_wallet set balance=balance-? where patient_id=? and balance>=?",amount,patient,amount)!=1) throw fail(409,"余额不足");
    }
    private void entry(String operation,String book,String direction,String category,Integer patient,BigDecimal amount,String source,String original,int actor) {
        db.update("insert into finance_ledger(id,operation_id,book,direction,category,patient_id,amount,source_id,original_source_id,actor_id,created_at) values(?,?,?,?,?,?,?,?,?,?,?)",
                id(),operation,book,direction,category,patient,amount,source,original,actor,now());
    }
    private List<Map<String,Object>> rows(String sql,Object...args) {
        return db.query(sql,(rs,n)->{Map<String,Object> row=new LinkedHashMap<>(); for(int i=1;i<=rs.getMetaData().getColumnCount();i++) row.put(rs.getMetaData().getColumnLabel(i).toLowerCase(Locale.ROOT),"result_json".equalsIgnoreCase(rs.getMetaData().getColumnLabel(i))?rs.getString(i):rs.getObject(i));return row;},args);
    }
    private Map<String,Object> one(String sql,Object...args) { List<Map<String,Object>> rows=rows(sql,args);if(rows.size()!=1) throw fail(404,"记录不存在或不唯一");return rows.get(0); }
    private static int num(Map<String,Object> row,String key) { Object value=row.get(key); if(value==null) throw fail(400,"数据缺少"+key); return ((Number)value).intValue(); }
    private static BigDecimal money(Map<String,Object> row,String key) { Object value=row.get(key); return value==null?null:new BigDecimal(value.toString()); }
    private static BigDecimal positive(String amount) { if(amount==null||!amount.matches("[0-9]{1,17}(\\.[0-9]{1,2})?"))throw fail(400,"金额须为最多两位小数的普通十进制正数"); try { BigDecimal value=Money.of(amount).asBigDecimal();if(value.signum()<=0) throw fail(400,"金额必须大于0");return bounded(value); } catch(FinanceException ex){throw ex;}catch(Exception ex){throw fail(400,"金额必须为最多两位小数的正数");} }
    private static BigDecimal bounded(BigDecimal value) { if(value.precision()-value.scale()>17) throw fail(400,"金额超出范围");return value; }
    private static void text(String value,int max,String label) { if(value==null||value.trim().isEmpty()||value.length()>max)throw fail(400,label+"不能为空或超长"); }
    private static FinanceException fail(int status,String message) { return new FinanceException(status,message); }
    private static String decimal(BigDecimal value) {return value.setScale(2,RoundingMode.UNNECESSARY).toPlainString();}
    private static String id(){return UUID.randomUUID().toString();}
    private static Timestamp now(){return Timestamp.from(Instant.now());}
    private static String sha(String value) {try {byte[] bytes=MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));StringBuilder out=new StringBuilder();for(byte b:bytes)out.append(String.format("%02x",b));return out.toString();}catch(Exception ex){throw new IllegalStateException(ex);}}
    public static Map<String,Object> map(Object...values){Map<String,Object> result=new LinkedHashMap<>();for(int i=0;i<values.length;i+=2)result.put(values[i].toString(),values[i+1]);return result;}

    public static final String TAX_NOTICE="演示税率，不代表实际适用税率；未配置不表示免税";
    @Transactional(readOnly=true)
    public Map<String,Object> taxPolicy() {
        Map<String,Object> policy=one("select * from finance_tax_policy where id=1");
        return map("rate",money(policy,"rate").toPlainString(),"configured",Boolean.TRUE.equals(policy.get("configured")),"notice",TAX_NOTICE);
    }
    public Map<String,Object> setTaxPolicy(int actor,String key,String rate) {
        if(rate==null||!rate.matches("[0-9]{1,3}(\\.[0-9]{1,4})?"))throw fail(400,"演示税率格式无效");
        BigDecimal value=new BigDecimal(rate).setScale(4,RoundingMode.UNNECESSARY);
        if(value.compareTo(new BigDecimal("100"))>0)throw fail(400,"演示税率必须在0到100之间");
        return operation(actor,key,"TAX_POLICY|"+value,op->{
            one("select id from finance_tax_policy where id=1 for update");
            db.update("update finance_tax_policy set rate=?,configured=TRUE,updated_by=?,updated_at=? where id=1",value,actor,now());
            return map("rate",value.toPlainString(),"configured",true,"notice",TAX_NOTICE);
        });
    }
    private void snapshotTax(String order,BigDecimal gross,BigDecimal rate,boolean configured) {
        BigDecimal net=gross.divide(BigDecimal.ONE.add(rate.movePointLeft(2)),2,RoundingMode.HALF_UP);
        db.update("insert into finance_order_tax(order_id,rate,configured,gross_amount,net_amount,tax_amount) values(?,?,?,?,?,?)",
                order,rate,configured,gross,net,gross.subtract(net));
    }
    private Map<String,Object> receiptTax(String sale,BigDecimal gross,BigDecimal refunded) {
        List<Map<String,Object>> snapshots=rows("select * from finance_order_tax where sale_id=?",sale);
        Map<String,Object> snapshot=snapshots.isEmpty()?null:snapshots.get(0);
        BigDecimal rate=snapshot==null?BigDecimal.ZERO:money(snapshot,"rate");
        BigDecimal tax=snapshot==null?BigDecimal.ZERO:money(snapshot,"tax_amount");
        BigDecimal net=gross.subtract(tax);
        // Cumulative reversal prevents rounding drift; a full refund reverses exactly the original tax.
        BigDecimal reversed=refunded.compareTo(gross)==0?tax:tax.multiply(refunded).divide(gross,2,RoundingMode.HALF_UP).min(tax);
        BigDecimal refundedNet=refunded.subtract(reversed);
        return map("rate",rate.setScale(4).toPlainString(),"configured",snapshot!=null&&Boolean.TRUE.equals(snapshot.get("configured")),
                "inclusive",true,"netAmount",decimal(net),"taxAmount",decimal(tax),"grossAmount",decimal(gross),
                "refundedNetAmount",decimal(refundedNet),"refundedTaxAmount",decimal(reversed),
                "remainingNetAmount",decimal(net.subtract(refundedNet)),"remainingTaxAmount",decimal(tax.subtract(reversed)),"notice",TAX_NOTICE);
    }

    public Map<String,Object> createOrder(int actor,int role,String key,int patient,int drugId,int quantity,String method,String encounterId) {
        if(quantity<=0) throw fail(400,"数量必须为正整数");
        if(!Arrays.asList("WALLET","CASH").contains(method)) throw fail(400,"支付方式无效");
        if("CASH".equals(method)&&role!=1) throw fail(403,"现金销售由管理员登记");
        if(role==3&&patientFor(actor)!=patient) throw fail(403,"不能使用其他患者账户");
        if(role==2) { Map<String,Object> e=one("select * from finance_encounter where id=?",encounterId); if(num(e,"doctor_id")!=doctorFor(actor)||num(e,"patient_id")!=patient)throw fail(403,"必须使用本人的患者接诊记录"); }
        ensurePatient(patient);
        return operation(actor,key,"ORDER|"+patient+"|"+drugId+"|"+quantity+"|"+method+"|"+encounterId,op->{
            Map<String,Object> drug=one("select * from drugs where id=?",drugId); BigDecimal price=money(drug,"price");
            if(price==null||price.signum()<=0)throw fail(400,"药品价格无效");
            BigDecimal amount=bounded(price.multiply(BigDecimal.valueOf(quantity)).setScale(2,RoundingMode.UNNECESSARY));
            String order=id();
            db.update("insert into finance_order(id,patient_id,drug_id,quantity,unit_price,amount,payment_method,encounter_id,created_by,status,created_at) values(?,?,?,?,?,?,?,?,?,?,?)",
                    order,patient,drugId,quantity,price,amount,method,encounterId,actor,"PENDING",now());
            Map<String,Object> policy=one("select * from finance_tax_policy where id=1 for update");
            snapshotTax(order,amount,money(policy,"rate"),Boolean.TRUE.equals(policy.get("configured")));
            return map("id",order,"status","PENDING","amount",decimal(amount),"paymentMethod",method);
        });
    }
    public Map<String,Object> payOrder(int actor,int role,String key,String orderId,String paymentPassword) {
        return operation(actor,key,"PAY|"+orderId,op->{
            Map<String,Object> order=one("select * from finance_order where id=? for update",orderId);
            int patient=num(order,"patient_id");
            if(role==3&&patientFor(actor)!=patient)throw fail(403,"不能支付他人的订单");
            if(role==2&&num(order,"created_by")!=actor)throw fail(403,"不能支付他人的订单");
            if("CASH".equals(order.get("payment_method"))&&role!=1)throw fail(403,"现金销售由管理员确认");
            if("PAID".equals(order.get("status")))return map("id",orderId,"saleId",order.get("sale_id"),"status","PAID","amount",decimal(money(order,"amount")));
            if(!"PENDING".equals(order.get("status")))throw fail(409,"订单状态不允许支付");
            if("WALLET".equals(order.get("payment_method"))) {
                if(role!=3)throw fail(403,"余额订单必须由患者本人输入系统支付密码完成，医生仅创建待支付单");
                paymentSecurity.verify(patient,paymentPassword);
            }
            Map<String,Object> sale=sale(actor,role,"order-payment:"+orderId,patient,num(order,"drug_id"),num(order,"quantity"),
                    order.get("payment_method").toString(),(String)order.get("encounter_id"),money(order,"unit_price"));
            if(rows("select order_id from finance_order_tax where order_id=?",orderId).isEmpty())
                snapshotTax(orderId,money(order,"amount"),BigDecimal.ZERO,false);
            db.update("update finance_order_tax set sale_id=? where order_id=?",sale.get("id"),orderId);
            if(db.update("update finance_order set status='PAID',sale_id=?,paid_at=? where id=? and status='PENDING'",sale.get("id"),now(),orderId)!=1)throw fail(409,"订单状态已变化");
            return map("id",orderId,"saleId",sale.get("id"),"status","PAID","amount",sale.get("amount"));
        });
    }
    @Transactional(readOnly=true)
    public List<Map<String,Object>> recharges(Integer patient) {
        List<Map<String,Object>> result=patient==null?rows("select * from finance_recharge order by requested_at desc"):rows("select * from finance_recharge where patient_id=? order by requested_at desc",patient);
        List<Map<String,Object>> out=new ArrayList<>();for(Map<String,Object> r:result)out.add(map("id",r.get("id"),"patientId",r.get("patient_id"),"amount",decimal(money(r,"amount")),"channel",r.get("channel"),"status",r.get("status"),"requestedAt",time(r.get("requested_at"))));return out;
    }
    @Transactional(readOnly=true)
    public List<Map<String,Object>> catalog(boolean staff) {
        List<Map<String,Object>> out=new ArrayList<>();
        for(Map<String,Object> r:rows("select d.id,d.name,d.type,d.text,d.number,d.price,coalesce(p.commission_rate,0) as rate from drugs d left join finance_drug_policy p on p.drug_id=d.id order by d.id")) {
            Map<String,Object> item=map("drugId",r.get("id"),"name",r.get("name"),"stock",r.get("number"),"price",decimal(money(r,"price")));
            if(staff){item.put("commissionRate",money(r,"rate").toPlainString());item.put("type",r.get("type"));item.put("text",r.get("text"));}out.add(item);
        }return out;
    }
    @Transactional(readOnly=true)
    public List<Map<String,Object>> transactions(Integer patient,boolean cash) {
        String sql=cash?"select * from finance_ledger where book='CASH'":"select * from finance_ledger where (book='PATIENT_BALANCE' or (book='CASH' and category='DRUG_SALE'))";
        List<Map<String,Object>> found=patient==null?rows(sql+" order by created_at desc"):rows(sql+" and patient_id=? order by created_at desc",patient);
        List<Map<String,Object>> out=new ArrayList<>();for(Map<String,Object> r:found)out.add(map("id",r.get("id"),"patientId",r.get("patient_id"),"book",r.get("book"),"direction",r.get("direction"),"category",r.get("category"),"amount",decimal(money(r,"amount")),"sourceId",r.get("source_id"),"originalSourceId",r.get("original_source_id"),"createdAt",time(r.get("created_at"))));return out;
    }
    @Transactional(readOnly=true)
    public List<Map<String,Object>> sales(Integer patient,Integer doctor,boolean staff) {
        String sql="select * from finance_sale where 1=1";List<Object> args=new ArrayList<>();
        if(patient!=null){sql+=" and patient_id=?";args.add(patient);}if(doctor!=null){sql+=" and doctor_id=?";args.add(doctor);}
        List<Map<String,Object>> out=new ArrayList<>();for(Map<String,Object> r:rows(sql+" order by created_at desc",args.toArray())) {
            Map<String,Object> item=map("id",r.get("id"),"patientId",r.get("patient_id"),"drugId",r.get("drug_id"),"quantity",r.get("quantity"),"amount",decimal(money(r,"amount")),"refundedAmount",decimal(money(r,"refunded_amount")),"paymentMethod",r.get("payment_method"),"createdAt",time(r.get("created_at")));
            if(staff){item.put("doctorId",r.get("doctor_id"));item.put("commissionRate",money(r,"commission_rate").toPlainString());item.put("commission",decimal(money(r,"commission")));item.put("reversedCommission",decimal(money(r,"reversed_commission")));}out.add(item);
        }return out;
    }
    @Transactional(readOnly=true)
    public List<Map<String,Object>> orders(int actor,int role) {
        List<Map<String,Object>> found=role==1?rows("select * from finance_order order by created_at desc"):
                role==3?rows("select * from finance_order where patient_id=? order by created_at desc",patientFor(actor)):
                rows("select * from finance_order where created_by=? order by created_at desc",actor);
        List<Map<String,Object>> out=new ArrayList<>();for(Map<String,Object> r:found)out.add(map("id",r.get("id"),"patientId",r.get("patient_id"),"drugId",r.get("drug_id"),"quantity",r.get("quantity"),"amount",decimal(money(r,"amount")),"status",r.get("status"),"saleId",r.get("sale_id"),"paymentMethod",r.get("payment_method")));return out;
    }
    @Transactional(readOnly=true)
    public List<Map<String,Object>> encounters(int actor,int role) {
        List<Map<String,Object>> found=role==1?rows("select * from finance_encounter order by completed_at desc"):
                role==3?rows("select * from finance_encounter where patient_id=? order by completed_at desc",patientFor(actor)):
                rows("select * from finance_encounter where doctor_id=? order by completed_at desc",doctorFor(actor));
        List<Map<String,Object>> out=new ArrayList<>();for(Map<String,Object> r:found)out.add(map("id",r.get("id"),"appointmentId",r.get("appointment_id"),"patientId",r.get("patient_id"),"doctorId",r.get("doctor_id"),"completedAt",time(r.get("completed_at"))));return out;
    }
    @Transactional(readOnly=true)
    public Map<String,Object> cashReport(String period) {
        period(period);TreeMap<String,BigDecimal[]> grouped=new TreeMap<>();
        for(Map<String,Object> r:rows("select direction,amount,created_at from finance_ledger where book='CASH'")){
            BigDecimal[] sums=grouped.computeIfAbsent(bucket(r.get("created_at"),period),k->new BigDecimal[]{ZERO,ZERO});
            int i="IN".equals(r.get("direction"))?0:1;sums[i]=sums[i].add(money(r,"amount"));
        }
        List<Map<String,Object>> points=new ArrayList<>();grouped.forEach((p,s)->points.add(map("period",p,"income",decimal(s[0]),"expense",decimal(s[1]),"net",decimal(s[0].subtract(s[1])))));
        return map("period",period,"timezone",ZONE.getId(),"points",points);
    }
    @Transactional(readOnly=true)
    public Map<String,Object> doctorReport(int doctor,String period) {
        period(period);TreeMap<String,BigDecimal[]> grouped=new TreeMap<>();
        for(Map<String,Object> r:rows("select completed_at from finance_encounter where doctor_id=?",doctor))
            grouped.computeIfAbsent(bucket(r.get("completed_at"),period),k->new BigDecimal[]{ZERO,ZERO})[0]=grouped.get(bucket(r.get("completed_at"),period))[0].add(BigDecimal.ONE);
        for(Map<String,Object> r:rows("select commission,created_at from finance_sale where doctor_id=?",doctor)){
            BigDecimal[] s=grouped.computeIfAbsent(bucket(r.get("created_at"),period),k->new BigDecimal[]{ZERO,ZERO});s[1]=s[1].add(money(r,"commission"));}
        for(Map<String,Object> r:rows("select r.reversed_commission,r.created_at from finance_refund r join finance_sale s on s.id=r.sale_id where s.doctor_id=?",doctor)){
            BigDecimal[] s=grouped.computeIfAbsent(bucket(r.get("created_at"),period),k->new BigDecimal[]{ZERO,ZERO});s[1]=s[1].subtract(money(r,"reversed_commission"));}
        List<Map<String,Object>> points=new ArrayList<>();grouped.forEach((p,s)->points.add(map("period",p,"encounters",s[0].intValue(),"commission",decimal(s[1]))));
        return map("doctorId",doctor,"period",period,"timezone",ZONE.getId(),"points",points);
    }
    private static String time(Object value){return value==null?null:((Timestamp)value).toInstant().toString();}
    private static void period(String period){if(!Arrays.asList("DAY","WEEK","MONTH").contains(period))throw fail(400,"period须为DAY/WEEK/MONTH");}
    private static String bucket(Object timestamp,String period){LocalDate date=((Timestamp)timestamp).toInstant().atZone(ZONE).toLocalDate();if("WEEK".equals(period))date=date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));if("MONTH".equals(period))return date.toString().substring(0,7);return date.toString();}
    @Transactional(readOnly=true)
    public List<Map<String,Object>> appointments(int actor,int role,Integer doctorFilter) {
        int doctor=role==2?doctorFor(actor):(doctorFilter==null?0:doctorFilter);
        String sql="select a.id,a.patientid,a.doctorid,a.time,p.name as patient_name,d.name as doctor_name,e.id as encounter_id from appointment a join patient p on p.id=a.patientid join doctor d on d.id=a.doctorid left join finance_encounter e on e.appointment_id=a.id";
        List<Map<String,Object>> found=doctor==0?rows(sql+" order by a.id desc"):rows(sql+" where a.doctorid=? order by a.id desc",doctor);
        List<Map<String,Object>> out=new ArrayList<>();for(Map<String,Object> r:found)out.add(map("appointmentId",r.get("id"),"patientId",r.get("patientid"),"patientName",r.get("patient_name"),"doctorId",r.get("doctorid"),"doctorName",r.get("doctor_name"),"date",String.valueOf(r.get("time")),"encounterId",r.get("encounter_id"),"status",r.get("encounter_id")==null?"PENDING":"COMPLETED"));return out;
    }
    @Transactional(readOnly=true)
    public List<Map<String,Object>> patients() {
        List<Map<String,Object>> out=new ArrayList<>();for(Map<String,Object> r:rows("select id,name from patient order by id"))out.add(map("patientId",r.get("id"),"name",r.get("name")));return out;
    }
    @Transactional(readOnly=true)
    public List<Map<String,Object>> doctors() {
        List<Map<String,Object>> out=new ArrayList<>();for(Map<String,Object> r:rows("select id,name,loginid from doctor order by id"))out.add(map("doctorId",r.get("id"),"name",r.get("name"),"accountAssigned",r.get("loginid")!=null));return out;
    }

    @Transactional(readOnly=true)
    public List<Map<String,Object>> procurements() {
        List<Map<String,Object>> out=new ArrayList<>();for(Map<String,Object> r:rows("select p.*,d.name as drug_name from finance_procurement p join drugs d on d.id=p.drug_id order by p.created_at desc"))
            out.add(map("id",r.get("id"),"drugId",r.get("drug_id"),"drugName",r.get("drug_name"),"quantity",r.get("quantity"),"unitCost",decimal(money(r,"unit_cost")),"amount",decimal(money(r,"amount")),"reference",r.get("reference_text"),"createdAt",time(r.get("created_at"))));return out;
    }

    /** Atomic catalog metadata + staff-only policy. No inventory write from this API. */
    public Map<String,Object> saveDrug(int actor,String key,Integer drugId,String name,Integer type,String price,String description,String commissionRate) {
        text(name,100,"药品名称");
        if(type==null||type<0||type>5)throw fail(400,"药品类型必须在0到5之间");
        if(description!=null&&description.length()>2000)throw fail(400,"药品说明过长");
        BigDecimal amount=positive(price);
        if(amount.compareTo(new BigDecimal("99999999.99"))>0)throw fail(400,"单价超出药品字段范围");
        if(commissionRate==null||!commissionRate.matches("[0-9]{1,3}(\\.[0-9]{1,4})?"))throw fail(400,"提成百分比格式无效");
        BigDecimal rate=new BigDecimal(commissionRate).setScale(4,RoundingMode.UNNECESSARY);
        if(rate.compareTo(new BigDecimal("100"))>0)throw fail(400,"提成百分比必须在0到100之间");
        String cleanName=name.trim(),details=description==null?"":description;
        // JSON canonical tuple avoids delimiter ambiguity in free text fields.
        String payload="DRUG_METADATA|"+JSON.toJSONString(Arrays.asList(drugId,cleanName,type,amount.toPlainString(),details,rate.toPlainString()));
        return operation(actor,key,payload,op->{
            one("select id from finance_catalog_guard where id=1 for update");
            List<Map<String,Object>> names=drugId==null?rows("select id from drugs where name=?",cleanName):rows("select id from drugs where name=? and id<>?",cleanName,drugId);
            if(!names.isEmpty())throw fail(409,"同名药品已存在");
            Integer savedId=drugId;
            if(savedId==null){
                org.springframework.jdbc.support.KeyHolder keys=new org.springframework.jdbc.support.GeneratedKeyHolder();
                db.update(connection->{java.sql.PreparedStatement statement=connection.prepareStatement("insert into drugs(name,type,price,number,text) values(?,?,?,0,?)",java.sql.Statement.RETURN_GENERATED_KEYS);statement.setString(1,cleanName);statement.setInt(2,type);statement.setBigDecimal(3,amount);statement.setString(4,details);return statement;},keys);
                if(keys.getKey()==null)throw fail(500,"未取得新增药品编号");
                savedId=keys.getKey().intValue();
            } else {
                one("select id from drugs where id=? for update",savedId);
                if(db.update("update drugs set name=?,type=?,price=?,text=? where id=?",cleanName,type,amount,details,savedId)!=1)throw fail(409,"药品记录已变化");
            }
            if(db.update("update finance_drug_policy set commission_rate=?,updated_by=?,updated_at=? where drug_id=?",rate,actor,now(),savedId)==0)
                db.update("insert into finance_drug_policy(drug_id,commission_rate,updated_by,updated_at) values(?,?,?,?)",savedId,rate,actor,now());
            int stock=db.queryForObject("select number from drugs where id=?",Integer.class,savedId);
            return map("drugId",savedId,"name",cleanName,"type",type,"price",decimal(amount),"text",details,"stock",stock,"commissionRate",rate.toPlainString(),"message","操作成功");
        });
    }

    public Map<String,Object> assignDoctorAccount(int actor,String key,int doctorId,String username,String password) {
        if(username==null||!username.matches("[\\p{L}\\p{N}_.-]{3,64}"))throw fail(400,"用户名须为3至64个字母、数字或_.-字符");
        if(password==null||password.length()<8||password.length()>72)throw fail(400,"新账号密码须为8至72个字符");
        // Never persist a fast password-derived idempotency fingerprint. Verify the supplied
        // secret against the salted account hash after replay resolution instead.
        Map<String,Object> result=operation(actor,key,"DOCTOR_ACCOUNT|"+doctorId+"|"+username,op->{
            one("select id from finance_catalog_guard where id=1 for update");
            Map<String,Object> doctor=one("select id,loginid from doctor where id=? for update",doctorId);
            if(doctor.get("loginid")!=null)throw fail(409,"医生已分配账号，不能重复绑定");
            if(!rows("select id from login where username=?",username).isEmpty())throw fail(409,"用户名已存在");
            String encoded=com.hospital.finance.auth.LoginPasswordCodec.encode(password);
            org.springframework.jdbc.support.KeyHolder keys=new org.springframework.jdbc.support.GeneratedKeyHolder();
            db.update(connection->{java.sql.PreparedStatement statement=connection.prepareStatement("insert into login(username,password,role) values(?,?,2)",java.sql.Statement.RETURN_GENERATED_KEYS);statement.setString(1,username);statement.setString(2,encoded);return statement;},keys);
            if(keys.getKey()==null)throw fail(500,"未取得新账号编号");
            int loginId=keys.getKey().intValue();
            if(db.update("update doctor set loginid=? where id=? and loginid is null",loginId,doctorId)!=1)throw fail(409,"医生账号关联已变化");
            return map("doctorId",doctorId,"loginId",loginId,"username",username,"accountAssigned",true,"message","医生账号已分配");
        });
        Map<String,Object> saved=one("select password from login where id=? for update",((Number)result.get("loginId")).intValue());
        if(!com.hospital.finance.auth.LoginPasswordCodec.matches((String)saved.get("password"),password))throw fail(409,"该幂等请求已使用不同账号密码，请勿重复分配");
        return result;
    }

    /** Read-only purchase receipt; never a tax invoice and never a financial mutation. */
    @Transactional(readOnly=true)
    public Map<String,Object> receipt(int actor,int role,String saleId) {
        if(role!=1&&role!=3)throw fail(403,"仅患者本人或管理员可查看购买凭证");
        String sql="select s.id,s.patient_id,s.drug_id,s.quantity,s.unit_price,s.amount,s.payment_method,s.refunded_amount,s.created_at,d.name as drug_name,p.name as patient_name from finance_sale s join finance_order o on o.sale_id=s.id and o.status='PAID' join drugs d on d.id=s.drug_id join patient p on p.id=s.patient_id where s.id=?";
        Map<String,Object> sale=role==3?one(sql+" and s.patient_id=?",saleId,patientFor(actor)):one(sql,saleId);
        BigDecimal total=money(sale,"amount"),refunded=money(sale,"refunded_amount");
        String refundStatus=refunded.signum()==0?"NONE":refunded.compareTo(total)==0?"FULL":"PARTIAL";
        String patientName=(String)sale.get("patient_name");
        String maskedName=patientName==null||patientName.trim().isEmpty()?"患者":new String(Character.toChars(patientName.trim().codePointAt(0)))+"***";
        Map<String,Object> item=map("drugId",sale.get("drug_id"),"name",sale.get("drug_name"),"quantity",sale.get("quantity"),"unitPrice",decimal(money(sale,"unit_price")),"amount",decimal(total));
        return map("receiptType","DEMO_PURCHASE_RECEIPT","title","药品购买凭证","notice","模拟票据，非报销凭证",
                "saleId",sale.get("id"),"issuedAt",time(sale.get("created_at")),"paymentMethod",sale.get("payment_method"),
                "patient",map("patientId",sale.get("patient_id"),"name",maskedName),"items",Collections.singletonList(item),
                "totalAmount",decimal(total),"refundedAmount",decimal(refunded),"netAmount",decimal(total.subtract(refunded)),
                "refundStatus",refundStatus,"nameSource","CURRENT_RECORD","tax",receiptTax(saleId,total,refunded));
    }

}
