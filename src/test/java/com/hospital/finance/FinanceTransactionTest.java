package com.hospital.finance;

import com.hospital.finance.auth.PaymentSecurityService;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinanceTransactionTest {
    AnnotationConfigApplicationContext context;
    JdbcTemplate db; FinanceService service; PaymentSecurityService security;
    @Configuration @EnableTransactionManagement
    static class Config {
        @Bean DataSource dataSource(){return new DriverManagerDataSource("jdbc:h2:mem:finance"+UUID.randomUUID()+";MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000","sa","");}
        @Bean JdbcTemplate jdbc(DataSource ds){return new JdbcTemplate(ds);}
        @Bean PlatformTransactionManager transactionManager(DataSource ds){return new DataSourceTransactionManager(ds);}
        @Bean PaymentSecurityService paymentSecurity(){return mock(PaymentSecurityService.class);}
        @Bean FinanceService finance(JdbcTemplate db,PaymentSecurityService security){return new FinanceService(db,security);}
    }
    @BeforeEach void setup(){
        context=new AnnotationConfigApplicationContext(Config.class);db=context.getBean(JdbcTemplate.class);service=context.getBean(FinanceService.class);security=context.getBean(PaymentSecurityService.class);
        db.execute("create table login(id int auto_increment primary key,username varchar(64),password varchar(255),role int)");
        db.execute("create table patient(id int primary key,loginid int,name varchar(100))");
        db.execute("create table doctor(id int primary key,loginid int,name varchar(100))");
        db.execute("create table drugs(id int auto_increment primary key,name varchar(100),number int,price decimal(10,2),type int default 0,text varchar(2000))");
        db.execute("create table appointment(id int primary key,patientid int,doctorid int,time date)");
        db.update("insert into patient values(1,31,'Synthetic patient'),(2,32,'Synthetic patient two')");
        db.update("insert into doctor values(1,21,'Synthetic doctor'),(2,22,'Synthetic doctor two'),(3,null,'Unassigned synthetic doctor')");
        db.update("insert into drugs(id,name,number,price) values(1,'Synthetic drug',100,10.00)");
        db.update("insert into appointment values(1,1,1,'2020-01-01'),(2,2,2,'2020-01-01'),(3,1,1,'2099-01-01')");
        new ResourceDatabasePopulator(new ClassPathResource("db/finance/V1__finance.sql"),new ClassPathResource("db/finance/V2__demo_tax.sql")).execute(context.getBean(DataSource.class));
    }
    @AfterEach void close(){db.execute("DROP ALL OBJECTS");context.close();}
    String recharge(String amount){return service.recharge(31,1,UUID.randomUUID().toString(),amount,"WECHAT","system-demo",null).get("id").toString();}
    void fund(String amount){service.confirmRecharge(11,UUID.randomUUID().toString(),recharge(amount));}
    String order(int quantity){return service.createOrder(31,3,UUID.randomUUID().toString(),1,1,quantity,"WALLET",null).get("id").toString();}
    String pay(String order){return service.payOrder(31,3,UUID.randomUUID().toString(),order,"system-demo").get("saleId").toString();}
    int count(String table){return db.queryForObject("select count(*) from "+table,Integer.class);}
    BigDecimal balance(){return db.queryForObject("select balance from finance_wallet where patient_id=1",BigDecimal.class);}
    void money(String expected,BigDecimal actual){assertEquals(0,new BigDecimal(expected).compareTo(actual));}
    List<Throwable> concurrent(int n,Runnable action)throws Exception{
        ExecutorService pool=Executors.newFixedThreadPool(n);CountDownLatch ready=new CountDownLatch(n),go=new CountDownLatch(1);List<Future<Throwable>> jobs=new ArrayList<>();
        try{for(int i=0;i<n;i++)jobs.add(pool.submit(()->{ready.countDown();go.await();try{action.run();return null;}catch(Throwable t){return t;}}));assertTrue(ready.await(10,TimeUnit.SECONDS));go.countDown();List<Throwable> failures=new ArrayList<>();for(Future<Throwable> f:jobs){Throwable t=f.get(20,TimeUnit.SECONDS);if(t!=null)failures.add(t);}return failures;}finally{pool.shutdownNow();}
    }
    @Test void pendingRechargeDoesNotCreditUntilConfirmed(){String id=recharge("100.00");assertEquals(0,count("finance_ledger"));service.confirmRecharge(11,"confirm",id);money("100",balance());assertEquals(2,count("finance_ledger"));}
    @Test void concurrentDifferentKeyConfirmationCreditsOnce()throws Exception{String id=recharge("100");assertTrue(concurrent(6,()->service.confirmRecharge(11,UUID.randomUUID().toString(),id)).isEmpty());money("100",balance());assertEquals(2,count("finance_ledger"));}
    @Test void rechargeRetrySameKeyCreatesOnlyOneRequest(){Map<String,Object> a=service.recharge(31,1,"same","10","WECHAT","pw",null);Map<String,Object>b=service.recharge(31,1,"same","10.00","WECHAT","pw",null);assertEquals(a.get("id"),b.get("id"));assertEquals(1,count("finance_recharge"));}
    @Test void sameKeyDifferentPayloadRejected(){service.recharge(31,1,"same","10","WECHAT","pw",null);assertEquals(409,assertThrows(FinanceException.class,()->service.recharge(31,1,"same","11","WECHAT","pw",null)).getStatus());assertEquals(1,count("finance_recharge"));}
    @Test void walletPurchaseDoesNotDuplicateCashReceipt(){fund("100");pay(order(2));money("80",balance());assertEquals(1,db.queryForObject("select count(*) from finance_ledger where book='CASH'",Integer.class));assertEquals(98,db.queryForObject("select number from drugs where id=1",Integer.class));}
    @Test void concurrentDifferentKeyOrderPaymentDebitsOnce()throws Exception{fund("100");String id=order(2);assertTrue(concurrent(6,()->service.payOrder(31,3,UUID.randomUUID().toString(),id,"pw")).isEmpty());money("80",balance());assertEquals(1,count("finance_sale"));assertEquals(98,db.queryForObject("select number from drugs where id=1",Integer.class));}
    @Test void concurrentSameKeyOrderCreationOnlyOneOrder()throws Exception{assertTrue(concurrent(5,()->service.createOrder(31,3,"same-order",1,1,1,"WALLET",null)).isEmpty());assertEquals(1,count("finance_order"));}
    @Test void lostResponseRetryReturnsPaidSaleWithoutAnotherDebit(){fund("100");String id=order(1);Map<String,Object> first=service.payOrder(31,3,"pay-id",id,"pw");Map<String,Object> retry=service.payOrder(31,3,"pay-id",id,"pw");assertEquals(first.get("saleId"),retry.get("saleId"));money("90",balance());assertEquals(1,count("finance_sale"));}
    @Test void insufficientBalanceRollsBackOperationAndStock(){fund("5");String id=order(1);int operations=count("finance_operation");assertThrows(FinanceException.class,()->pay(id));money("5",balance());assertEquals(100,db.queryForObject("select number from drugs where id=1",Integer.class));assertEquals(0,count("finance_sale"));assertEquals(operations,count("finance_operation"));}
    @Test void competingOrdersCannotOverdraw()throws Exception{fund("10");List<String> orders=Arrays.asList(order(1),order(1));java.util.concurrent.atomic.AtomicInteger next=new java.util.concurrent.atomic.AtomicInteger();List<Throwable> failures=concurrent(2,()->pay(orders.get(next.getAndIncrement())));assertEquals(1,failures.size());money("0",balance());assertEquals(1,count("finance_sale"));}
    @Test void competingOrdersCannotOversellStock()throws Exception{fund("100");db.update("update drugs set number=1");List<String> orders=Arrays.asList(order(1),order(1));java.util.concurrent.atomic.AtomicInteger next=new java.util.concurrent.atomic.AtomicInteger();assertEquals(1,concurrent(2,()->pay(orders.get(next.getAndIncrement()))).size());money("90",balance());assertEquals(0,db.queryForObject("select number from drugs where id=1",Integer.class));}
    @Test void secondLedgerFailureRollsBackRechargeAndWallet(){String id=recharge("100");db.execute("alter table finance_ledger add constraint injected_failure check(book<>'PATIENT_BALANCE')");assertThrows(RuntimeException.class,()->service.confirmRecharge(11,"broken",id));assertEquals(0,count("finance_wallet"));assertEquals(0,count("finance_ledger"));assertEquals("PENDING",db.queryForObject("select status from finance_recharge where id=?",String.class,id));}
    @Test void ledgerFailureAfterStockAndSaleRollsEverythingBack(){fund("100");String id=order(1);db.execute("alter table finance_ledger add constraint injected_failure check(category<>'DRUG_SALE')");assertThrows(RuntimeException.class,()->pay(id));money("100",balance());assertEquals(100,db.queryForObject("select number from drugs where id=1",Integer.class));assertEquals(0,count("finance_sale"));assertEquals("PENDING",db.queryForObject("select status from finance_order where id=?",String.class,id));}
    @Test void refundOnlyReturnsBalanceAndNeverCashOut(){fund("100");String sale=pay(order(1));service.refund(11,"refund",sale,"10","Synthetic refund");money("100",balance());assertEquals(0,db.queryForObject("select count(*) from finance_ledger where book='CASH' and direction='OUT'",Integer.class));assertEquals(99,db.queryForObject("select number from drugs where id=1",Integer.class));}
    @Test void concurrentRefundCannotExceedOriginal()throws Exception{fund("100");String sale=pay(order(1));assertEquals(1,concurrent(2,()->service.refund(11,UUID.randomUUID().toString(),sale,"7","Synthetic refund")).size());money("97",balance());assertEquals(1,count("finance_refund"));}
    @Test void refundSameKeyRetryDoesNotDoubleCredit(){fund("100");String sale=pay(order(1));service.refund(11,"refund",sale,"3","reason");service.refund(11,"refund",sale,"3.00","reason");money("93",balance());assertEquals(1,count("finance_refund"));}
    @Test void commissionSnapshotSurvivesPolicyChangeAndRefundsFully(){fund("100");String encounter=service.completeEncounter(21,"complete",1).get("id").toString();service.setPolicy(11,"policy",1,"12.3456");String id=service.createOrder(31,3,"o",1,1,1,"WALLET",encounter).get("id").toString();String sale=pay(id);service.setPolicy(11,"policy2",1,"99");service.refund(11,"r1",sale,"3","reason");service.refund(11,"r2",sale,"7","reason");money("1.23",db.queryForObject("select commission from finance_sale",BigDecimal.class));money("1.23",db.queryForObject("select sum(reversed_commission) from finance_refund",BigDecimal.class));}
    @Test void standaloneSaleHasNoCommission(){fund("100");service.setPolicy(11,"policy",1,"25");pay(order(1));money("0",db.queryForObject("select commission from finance_sale",BigDecimal.class));}
    @Test void completedEncounterCountIsNotPageViewsAndUnique(){service.appointments(21,2,null);assertEquals(0,count("finance_encounter"));service.completeEncounter(21,"complete",1);service.completeEncounter(21,"complete-again",1);assertEquals(1,count("finance_encounter"));assertThrows(FinanceException.class,()->service.completeEncounter(21,"other",2));assertThrows(FinanceException.class,()->service.completeEncounter(21,"future",3));}
    @Test void catalogAndPatientSaleDtosNeverContainCommission(){assertFalse(service.catalog(false).get(0).containsKey("commissionRate"));fund("100");pay(order(1));assertFalse(service.sales(1,null,false).get(0).containsKey("commission"));assertFalse(service.sales(1,null,false).get(0).containsKey("commissionRate"));}
    @Test void procurementCashAndStockAtomic(){service.procure(11,"stock",1,5,"2.50","Synthetic invoice");assertEquals(105,db.queryForObject("select number from drugs where id=1",Integer.class));money("12.50",db.queryForObject("select amount from finance_ledger where category='PROCUREMENT'",BigDecimal.class));}
    @Test void procurementFailureRollsBackStock(){db.execute("alter table finance_ledger add constraint injected_failure check(category<>'PROCUREMENT')");assertThrows(RuntimeException.class,()->service.procure(11,"stock",1,5,"2.50","Synthetic invoice"));assertEquals(100,db.queryForObject("select number from drugs where id=1",Integer.class));assertEquals(0,count("finance_procurement"));}
    @Test void cashSaleRefundCreditsBalanceWithoutCashOut(){String id=service.createOrder(11,1,"cash",1,1,1,"CASH",null).get("id").toString();String sale=service.payOrder(11,1,"paycash",id,null).get("saleId").toString();service.refund(11,"refundcash",sale,"10","reason");money("10",balance());assertEquals(1,db.queryForObject("select count(*) from finance_ledger where book='CASH' and direction='IN'",Integer.class));assertEquals(0,db.queryForObject("select count(*) from finance_ledger where book='CASH' and direction='OUT'",Integer.class));}
    @Test void paymentPasswordFailureCannotWriteMoneyOrOrderState(){fund("100");String id=order(1);doThrow(new IllegalArgumentException("denied")).when(security).verify(1,"bad");assertThrows(IllegalArgumentException.class,()->service.payOrder(31,3,"badpay",id,"bad"));money("100",balance());assertEquals(0,count("finance_sale"));}
    @Test void wrongPatientCannotPayOrder(){fund("100");String id=order(1);assertEquals(403,assertThrows(FinanceException.class,()->service.payOrder(32,3,"attack",id,"pw")).getStatus());money("100",balance());}
    @Test void doctorCreatesPendingOrderButPatientAlonePaysWallet(){fund("100");String encounter=service.completeEncounter(21,"complete",1).get("id").toString();String id=service.createOrder(21,2,"doctor-order",1,1,1,"WALLET",encounter).get("id").toString();assertEquals(403,assertThrows(FinanceException.class,()->service.payOrder(21,2,"doctor-pay",id,"pw")).getStatus());service.payOrder(31,3,"patient-pay",id,"pw");money("90",balance());}
    @Test void orderPriceIsStableUntilPayment(){fund("100");String id=order(1);db.update("update drugs set price=99 where id=1");pay(id);money("90",balance());money("10",db.queryForObject("select unit_price from finance_sale",BigDecimal.class));}
    @Test void staffCannotForgeAnotherDoctorsEncounter(){String other=service.completeEncounter(22,"other-complete",2).get("id").toString();assertThrows(FinanceException.class,()->service.createOrder(21,2,"forged",2,1,1,"WALLET",other));}
    @Test void walletHistoryDoesNotDoubleShowRecharge(){fund("100");assertEquals(1,service.transactions(1,false).size());assertEquals(1,service.transactions(null,true).size());}
    @Test void cashReportUsesActualCashBookOnly(){fund("100");pay(order(1));service.procure(11,"procure",1,2,"3","reference");Map<String,Object> report=service.cashReport("MONTH");List<?> points=(List<?>)report.get("points");Map<?,?> point=(Map<?,?>)points.get(0);assertEquals("100.00",point.get("income"));assertEquals("6.00",point.get("expense"));assertEquals("94.00",point.get("net"));}
    @Test void emptyReportsDoNotInventSeries(){assertTrue(((List<?>)service.cashReport("DAY").get("points")).isEmpty());assertTrue(((List<?>)service.doctorReport(1,"WEEK").get("points")).isEmpty());}
    @Test void shanghaiMondayWeekBucketUsesCompletionNotAppointmentDate(){service.completeEncounter(21,"complete",1);db.update("update finance_encounter set completed_at=?",java.sql.Timestamp.from(java.time.Instant.parse("2026-11-01T16:30:00Z")));List<?> points=(List<?>)service.doctorReport(1,"WEEK").get("points");Map<?,?> point=(Map<?,?>)points.get(0);assertEquals("2026-11-02",point.get("period"));assertEquals(1,point.get("encounters"));}
    @Test void financialReferencesPreventDeletingDrugOrPatient(){fund("100");pay(order(1));assertThrows(RuntimeException.class,()->db.update("delete from drugs where id=1"));assertThrows(RuntimeException.class,()->db.update("delete from patient where id=1"));}
    @Test void refundLedgerFailureRollsBackWalletAndCommissionReversal(){fund("100");String sale=pay(order(1));db.execute("alter table finance_ledger add constraint injected_failure check(category<>'REFUND')");assertThrows(RuntimeException.class,()->service.refund(11,"broken",sale,"5","reason"));money("90",balance());money("0",db.queryForObject("select refunded_amount from finance_sale",BigDecimal.class));assertEquals(0,count("finance_refund"));}

    @Test void drugMetadataAndPolicyCreateAtomicallyWithZeroStock(){Map<String,Object> r=service.saveDrug(11,"newdrug",null,"Synthetic catalog drug",2,"12.50","demo text","15.25");int drug=((Number)r.get("drugId")).intValue();assertEquals(0,db.queryForObject("select number from drugs where id=?",Integer.class,drug));money("15.25",db.queryForObject("select commission_rate from finance_drug_policy where drug_id=?",BigDecimal.class,drug));assertEquals(r.get("drugId"),service.saveDrug(11,"newdrug",null,"Synthetic catalog drug",2,"12.50","demo text","15.25").get("drugId"));assertEquals(2,count("drugs"));}
    @Test void drugMetadataEditDoesNotChangeStock(){service.saveDrug(11,"edit",1,"Renamed synthetic",4,"9.25","updated","20");assertEquals(100,db.queryForObject("select number from drugs where id=1",Integer.class));assertEquals("Renamed synthetic",db.queryForObject("select name from drugs where id=1",String.class));money("20",db.queryForObject("select commission_rate from finance_drug_policy where drug_id=1",BigDecimal.class));}
    @Test void policyInsertFailureRollsBackNewDrugAndIdempotency(){db.execute("alter table finance_drug_policy add constraint injected_policy_failure check(commission_rate=0)");int operations=count("finance_operation");assertThrows(RuntimeException.class,()->service.saveDrug(11,"newdrug",null,"Should rollback",1,"12.50","demo","10"));assertEquals(1,count("drugs"));assertEquals(0,count("finance_drug_policy"));assertEquals(operations,count("finance_operation"));}
    @Test void policyFailureRollsBackMetadataUpdate(){service.setPolicy(11,"policy",1,"0");db.execute("alter table finance_drug_policy add constraint injected_policy_failure check(commission_rate=0)");assertThrows(RuntimeException.class,()->service.saveDrug(11,"edit",1,"Should rollback",1,"12.50","demo","10"));assertEquals("Synthetic drug",db.queryForObject("select name from drugs where id=1",String.class));money("10",db.queryForObject("select price from drugs where id=1",BigDecimal.class));}
    @Test void invalidPolicyOrMetadataRejectedBeforeWrite(){for(String rate:Arrays.asList("-1","101","0.12345","100.0001"))assertThrows(FinanceException.class,()->service.saveDrug(11,"invalid"+rate,null,"Bad",0,"1","",rate));assertThrows(FinanceException.class,()->service.saveDrug(11,"badtype",null,"Bad",6,"1","","0"));assertThrows(FinanceException.class,()->service.saveDrug(11,"badprice",null,"Bad",0,"100000000.00","","0"));assertEquals(1,count("drugs"));}
    @Test void concurrentSameNameCreatesOnlyOneCatalogDrug()throws Exception{assertEquals(1,concurrent(2,()->service.saveDrug(11,UUID.randomUUID().toString(),null,"Same synthetic name",0,"1","","0")).size());assertEquals(2,count("drugs"));}
    @Test void staffPolicyMetadataStillDoesNotLeakToPatientCatalog(){service.saveDrug(11,"edit",1,"Synthetic drug",4,"10","private policy not here","30");Map<String,Object> staff=service.catalog(true).get(0);assertEquals(4,staff.get("type"));assertEquals("30.0000",staff.get("commissionRate"));assertFalse(service.catalog(false).get(0).containsKey("commissionRate"));}

    @Test void administratorCanAssignUnboundDoctorAccountWithHash(){Map<String,Object> r=service.assignDoctorAccount(11,"assign",3,"synthetic-doctor","StrongDemoPassword");int login=((Number)r.get("loginId")).intValue();String stored=db.queryForObject("select password from login where id=?",String.class,login);assertNotEquals("StrongDemoPassword",stored);assertTrue(com.hospital.finance.auth.LoginPasswordCodec.matches(stored,"StrongDemoPassword"));assertEquals(Integer.valueOf(login),db.queryForObject("select loginid from doctor where id=3",Integer.class));assertEquals(Integer.valueOf(2),db.queryForObject("select role from login where id=?",Integer.class,login));assertFalse(r.containsKey("password"));assertEquals(login,((Number)service.assignDoctorAccount(11,"assign",3,"synthetic-doctor","StrongDemoPassword").get("loginId")).intValue());}
    @Test void assignedDoctorCannotBeReboundAndChangedPasswordRetryRejected(){service.assignDoctorAccount(11,"assign",3,"synthetic-doctor","StrongDemoPassword");assertThrows(FinanceException.class,()->service.assignDoctorAccount(11,"different",3,"other-doctor","StrongDemoPassword"));assertThrows(FinanceException.class,()->service.assignDoctorAccount(11,"assign",3,"synthetic-doctor","DifferentPassword"));assertEquals(1,count("login"));}
    @Test void doctorAccountBindFailureRollsBackLogin(){db.execute("alter table doctor add constraint injected_doctor_bind_failure check(id<>3 or loginid is null)");assertThrows(RuntimeException.class,()->service.assignDoctorAccount(11,"assign",3,"synthetic-doctor","StrongDemoPassword"));assertEquals(0,count("login"));assertNull(db.queryForObject("select loginid from doctor where id=3",Integer.class));}

    @Test void paidReceiptUsesSaleAmountsAndMaskedNameWithoutCommission(){fund("100");String sale=pay(order(2));db.update("update drugs set price=99 where id=1");Map<String,Object> receipt=service.receipt(31,3,sale);assertEquals("模拟票据，非报销凭证",receipt.get("notice"));assertEquals("DEMO_PURCHASE_RECEIPT",receipt.get("receiptType"));assertEquals("20.00",receipt.get("totalAmount"));Map<?,?> item=(Map<?,?>)((List<?>)receipt.get("items")).get(0);assertEquals("10.00",item.get("unitPrice"));assertEquals(2,item.get("quantity"));assertEquals("S***",((Map<?,?>)receipt.get("patient")).get("name"));assertFalse(com.alibaba.fastjson2.JSON.toJSONString(receipt).contains("commission"));assertFalse(com.alibaba.fastjson2.JSON.toJSONString(receipt).contains("Synthetic patient"));}
    @Test void receiptIsReadOnlyAndRepeatedReadsDoNotCreateLedgerEntries(){fund("100");String sale=pay(order(1));int entries=count("finance_ledger"),operations=count("finance_operation");BigDecimal before=balance();Map<String,Object> first=service.receipt(31,3,sale);assertEquals(first,service.receipt(31,3,sale));assertEquals(first,service.receipt(11,1,sale));assertEquals(entries,count("finance_ledger"));assertEquals(operations,count("finance_operation"));money(before.toPlainString(),balance());}
    @Test void foreignPatientAndDoctorCannotReadReceipt(){fund("100");String sale=pay(order(1));assertEquals(404,assertThrows(FinanceException.class,()->service.receipt(32,3,sale)).getStatus());assertEquals(403,assertThrows(FinanceException.class,()->service.receipt(21,2,sale)).getStatus());}
    @Test void pendingOrderCannotProducePaidReceipt(){String pending=order(1);assertEquals(404,assertThrows(FinanceException.class,()->service.receipt(31,3,pending)).getStatus());assertEquals(0,count("finance_ledger"));}
    @Test void receiptReportsPartialAndFullRefundWithoutPretendingOriginalSaleDisappeared(){fund("100");String sale=pay(order(1));assertEquals("NONE",service.receipt(31,3,sale).get("refundStatus"));service.refund(11,"receipt-partial",sale,"3","demo");Map<String,Object> partial=service.receipt(31,3,sale);assertEquals("PARTIAL",partial.get("refundStatus"));assertEquals("3.00",partial.get("refundedAmount"));assertEquals("7.00",partial.get("netAmount"));service.refund(11,"receipt-full",sale,"7","demo");Map<String,Object> full=service.receipt(31,3,sale);assertEquals("FULL",full.get("refundStatus"));assertEquals("0.00",full.get("netAmount"));assertEquals("10.00",full.get("totalAmount"));}
    @Test void receiptRequiresPaidOrderLinkEvenIfSaleRowExists(){fund("100");String order=order(1);String sale=pay(order);db.update("update finance_order set status='PENDING' where id=?",order);assertThrows(FinanceException.class,()->service.receipt(11,1,sale));}


    @Test void repeatedPdfGenerationKeepsActualAmountsAndDoesNotWriteLedgers()throws Exception{
        fund("100");String sale=pay(order(2));service.refund(11,"pdf-refund",sale,"5","demo");
        int entries=count("finance_ledger"),operations=count("finance_operation");BigDecimal before=balance();
        for(int i=0;i<2;i++){
            byte[] pdf=ReceiptPdf.render(service.receipt(31,3,sale));
            com.itextpdf.text.pdf.PdfReader reader=new com.itextpdf.text.pdf.PdfReader(pdf);
            String text=com.itextpdf.text.pdf.parser.PdfTextExtractor.getTextFromPage(reader,1);reader.close();
            assertTrue(text.contains("购买金额：20.00"));assertTrue(text.contains("已退回余额：5.00"));assertTrue(text.contains("退款后净额：15.00"));
        }
        assertEquals(entries,count("finance_ledger"));assertEquals(operations,count("finance_operation"));money(before.toPlainString(),balance());
    }

    @SuppressWarnings("unchecked") Map<String,Object> tax(String sale){return (Map<String,Object>)service.receipt(31,3,sale).get("tax");}
    @Test void inclusiveTaxIsSnapshottedAtOrderAndDoesNotIncreaseCharge(){
        fund("100");service.setTaxPolicy(11,"tax13","13");String pending=order(2);
        service.setTaxPolicy(11,"tax20","20");String sale=pay(pending);Map<String,Object> tax=tax(sale);
        assertEquals("13.0000",tax.get("rate"));assertEquals(true,tax.get("configured"));assertEquals("17.70",tax.get("netAmount"));assertEquals("2.30",tax.get("taxAmount"));assertEquals("20.00",tax.get("grossAmount"));money("80",balance());
        service.setTaxPolicy(11,"tax50","50");assertEquals(tax,tax(sale));
    }
    @Test void cumulativeTaxRefundReconcilesPartialAndFullAmounts(){
        fund("100");service.setTaxPolicy(11,"tax13","13");String sale=pay(order(2));
        service.refund(11,"r5",sale,"5","demo");Map<String,Object> partial=tax(sale);
        assertEquals("0.58",partial.get("refundedTaxAmount"));assertEquals("4.42",partial.get("refundedNetAmount"));assertEquals("1.72",partial.get("remainingTaxAmount"));assertEquals("13.28",partial.get("remainingNetAmount"));
        service.refund(11,"r5b",sale,"5","demo");assertEquals("1.15",tax(sale).get("refundedTaxAmount"));
        service.refund(11,"r10",sale,"10","demo");Map<String,Object> full=tax(sale);
        assertEquals("2.30",full.get("refundedTaxAmount"));assertEquals("17.70",full.get("refundedNetAmount"));assertEquals("0.00",full.get("remainingTaxAmount"));assertEquals("0.00",full.get("remainingNetAmount"));
    }
    @Test void defaultAndLegacyTaxRemainExplicitlyUnconfigured(){
        assertEquals(true,service.taxPolicy().get("configured"));assertEquals("3.0000",service.taxPolicy().get("rate"));fund("100");String oldPending=order(2);
        db.update("delete from finance_order_tax where order_id=?",oldPending);service.setTaxPolicy(11,"tax13","13");
        String sale=pay(oldPending);assertEquals(false,tax(sale).get("configured"));assertEquals("0.00",tax(sale).get("taxAmount"));
        db.update("delete from finance_order_tax where sale_id=?",sale);
        assertEquals(false,tax(sale).get("configured"));assertEquals("20.00",tax(sale).get("netAmount"));
    }
    @Test void taxPolicyValidationAndIdempotency(){
        for(String invalid:new String[]{"-1","100.0001","101","1.00001","NaN","",null})assertThrows(FinanceException.class,()->service.setTaxPolicy(11,UUID.randomUUID().toString(),invalid));
        Map<String,Object> first=service.setTaxPolicy(11,"tax0","0");assertEquals(true,first.get("configured"));assertEquals(first,service.setTaxPolicy(11,"tax0","0.0000"));assertThrows(FinanceException.class,()->service.setTaxPolicy(11,"tax0","1"));
    }
    @Test void taxSnapshotFailureRollsBackNewOrderAndOperation(){
        db.execute("alter table finance_order_tax add constraint reject_demo_tax check(gross_amount < 1)");int before=count("finance_operation");
        assertThrows(RuntimeException.class,()->order(2));assertEquals(0,count("finance_order"));assertEquals(before,count("finance_operation"));
    }
    @Test void migrationCanBeReappliedWithoutChangingPolicyOrSnapshots(){
        fund("100");service.setTaxPolicy(11,"tax13","13");String sale=pay(order(2));Map<String,Object> before=tax(sale);
        new ResourceDatabasePopulator(new ClassPathResource("db/finance/V2__demo_tax.sql")).execute(context.getBean(DataSource.class));
        assertEquals(before,tax(sale));assertEquals("13.0000",service.taxPolicy().get("rate"));
    }
    @Test void pdfIncludesSnapshotTaxAndUnambiguousDemoNotice()throws Exception{
        fund("100");service.setTaxPolicy(11,"tax13","13");String sale=pay(order(2));service.refund(11,"pdf-tax-refund",sale,"5","demo");
        com.itextpdf.text.pdf.PdfReader reader=new com.itextpdf.text.pdf.PdfReader(ReceiptPdf.render(service.receipt(31,3,sale)));
        String text=com.itextpdf.text.pdf.parser.PdfTextExtractor.getTextFromPage(reader,1);reader.close();
        for(String expected:new String[]{"13.0000%","17.70","2.30","4.42","0.58","不代表实际适用税率","未配置不表示免税"})assertTrue(text.contains(expected),expected);
    }

    @Test void newlyConfiguredThreePercentSplitsTenWithoutExtraCharge(){
        fund("100");String sale=pay(order(1));Map<String,Object> split=tax(sale);
        assertEquals("3.0000",split.get("rate"));assertEquals(true,split.get("configured"));
        assertEquals("10.00",split.get("grossAmount"));assertEquals("9.71",split.get("netAmount"));assertEquals("0.29",split.get("taxAmount"));money("90",balance());
    }
    @Test void threePercentTwentyAndFiveRefundReconcile(){
        fund("100");String sale=pay(order(2));service.refund(11,"three-refund",sale,"5","demo");Map<String,Object> split=tax(sale);
        assertEquals("19.42",split.get("netAmount"));assertEquals("0.58",split.get("taxAmount"));assertEquals("0.15",split.get("refundedTaxAmount"));assertEquals("4.85",split.get("refundedNetAmount"));
    }
    @Test void reapplyingMigrationDoesNotTurnExistingUnconfiguredPolicyIntoThreePercent(){
        db.update("update finance_tax_policy set rate=0,configured=FALSE where id=1");
        new ResourceDatabasePopulator(new ClassPathResource("db/finance/V2__demo_tax.sql")).execute(context.getBean(DataSource.class));
        assertEquals(false,service.taxPolicy().get("configured"));assertEquals("0.0000",service.taxPolicy().get("rate"));
    }
}
