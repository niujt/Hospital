/* Local simulated finance UI. No passwords/cards persisted in browser storage. */
(function () {
 'use strict';
 const app=document.getElementById('finance-app'), role=app.dataset.role, status=document.getElementById('finance-status');
 let csrf='', charts=[], loading=false;
 const formIntents=new Map();
 const esc=v=>String(v==null?'':v).replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
 const labels={PENDING:'待确认',CONFIRMED:'已确认',PAID:'已支付',COMPLETED:'已完成',WALLET:'余额',CASH:'现金登记',WECHAT:'模拟微信',ALIPAY:'模拟支付宝',BANK:'模拟银行卡',IN:'收入',OUT:'支出',PATIENT_BALANCE:'患者余额',RECHARGE:'充值',DRUG_SALE:'购药',REFUND:'退款',PROCUREMENT:'进货'};
 const display=v=>labels[v]||(typeof v==='string'&&/^\d{4}-\d\d-\d\dT/.test(v)?new Date(v).toLocaleString('zh-CN',{timeZone:'Asia/Shanghai',hour12:false}):v);
 function key(){return crypto.randomUUID ? crypto.randomUUID() : Date.now()+'-'+Array.from(crypto.getRandomValues(new Uint32Array(4))).join('-');}
 async function api(path,method='GET',body,k){
  const headers={'Accept':'application/json'}; if(method!=='GET'){headers['Content-Type']='application/json';headers['X-CSRF-Token']=csrf;if(k)headers['Idempotency-Key']=k;}
  const r=await fetch('/api/finance'+path,{method,headers,credentials:'same-origin',body:body==null?undefined:JSON.stringify(body)});
  let data;try{data=await r.json();}catch(_){data={};}
  if(!r.ok){const e=new Error(data.message||('请求失败（'+r.status+'），请重新登录或联系管理员'));e.definite=r.status<500;throw e;}return data;
 }
 function card(title,html){return '<section class="layui-card"><div class="layui-card-header">'+esc(title)+'</div><div class="layui-card-body">'+html+'</div></section><hr>';}
 function field(name,label,type='text',extra=''){return '<div class="layui-form-item"><label class="layui-form-label" for="f-'+name+'">'+esc(label)+'</label><div class="layui-input-block"><input id="f-'+name+'" name="'+name+'" type="'+type+'" class="layui-input" '+extra+'></div></div>';}
 function select(name,label,items){return '<div class="layui-form-item"><label class="layui-form-label" for="f-'+name+'">'+esc(label)+'</label><div class="layui-input-block"><select class="layui-input" id="f-'+name+'" name="'+name+'">'+items.map(i=>'<option value="'+esc(i[0])+'">'+esc(i[1])+'</option>').join('')+'</select></div></div>';}
 function form(id,html,button){html=html.replace(/(id|for)="f-([^"]+)"/g,(_,attr,name)=>attr+'="'+id+'-'+name+'"');return '<form id="'+id+'" autocomplete="off">'+html+'<div class="layui-form-item"><div class="layui-input-block"><button class="layui-btn" type="submit">'+esc(button)+'</button></div></div><p role="status" class="form-status"></p></form>';}
 function table(rows,cols,action){if(!rows.length)return '<p>暂无记录</p>';return '<div class="layui-table-box"><table class="layui-table"><thead><tr>'+cols.map(c=>'<th>'+esc(c[1])+'</th>').join('')+(action?(role==='patient'?'<th>操作</th>':'<th width="240">操作</th>'):'')+'</tr></thead><tbody>'+rows.map(r=>'<tr>'+cols.map(c=>'<td>'+esc(display(r[c[0]]))+'</td>').join('')+(action?'<td>'+(role==='patient'?action(r):'<div class="layui-btn-container" role="group" aria-label="记录操作">'+action(r)+'</div>')+'</td>':'')+'</tr>').join('')+'</tbody></table></div>';}
 const btn=(a,id,text)=>'<button type="button" class="layui-btn layui-btn-sm'+(role!=='patient'&&['rate','refund'].includes(a)?' layui-btn-primary':'')+'" data-action="'+a+'" data-id="'+id+'">'+esc(text)+'</button>';
 function bindForm(id,path,method,convert){const f=document.getElementById(id);if(!f)return;f.onsubmit=async e=>{e.preventDefault();const b=f.querySelector('button[type=submit]'), out=f.querySelector('.form-status');if(b.disabled)return;let data=Object.fromEntries(new FormData(f)), body=convert?convert(data):data;const safe=Object.assign({},body);['paymentPassword','loginPassword','currentPaymentPassword','newPaymentPassword','password'].forEach(n=>delete safe[n]);const sig=JSON.stringify(safe);const identity=id+':'+sig;if(!formIntents.has(identity))formIntents.set(identity,key());b.disabled=true;try{await api(typeof path==='function'?path(body):path,method,body,formIntents.get(identity));formIntents.delete(identity);out.textContent='操作成功';f.reset();await load();}catch(err){const nonLedger=id==='card'||id==='password';out.textContent=err.message+(err.definite?'':nonLedger?'；结果不确定，请刷新核实卡片或密码状态，不要直接重复提交。':'；结果待核实，请刷新记录后再重试，同一次请求将复用编号。');if(nonLedger&&!err.definite){f.dataset.ambiguous='true';}if(err.definite)formIntents.delete(identity);}finally{f.querySelectorAll('input[type=password]').forEach(i=>i.value='');b.disabled=f.dataset.ambiguous==='true';}};}
 function plot(id,rows,series){const node=document.getElementById(id);if(!node)return;node.style.height='320px';if(!rows.length){node.textContent='暂无可汇总数据，不展示模拟数字。';return;}const chart=echarts.init(node);charts.push(chart);chart.setOption({aria:{enabled:true},tooltip:{trigger:'axis',renderMode:'richText'},legend:{},grid:{left:65,right:35,bottom:55},xAxis:{type:'category',data:rows.map(r=>r.period)},yAxis:series.length>1&&series[0][0]==='encounters'?[{type:'value',name:'接诊次数',minInterval:1},{type:'value',name:'提成（元）'}]:{type:'value',name:'元'},series:series.map((s,i)=>({name:s[1],type:s[2]||'bar',yAxisIndex:series[0][0]==='encounters'?i:0,data:rows.map(r=>Number(r[s[0]]))}))});}
 function moneyFields(){return field('amount','金额（元）','number','min="0.01" step="0.01" required');}
 const pass=()=>field('paymentPassword','系统支付密码','password','required minlength="6" maxlength="32" autocomplete="off"');
 const ledgerCols=[['id','编号'],['createdAt','时间'],['category','类型'],['direction','方向'],['amount','金额（元）'],['sourceId','关联单号']];
 let sales=[],orders=[];
 async function load(){if(loading)return;loading=true;status.textContent='正在读取真实模拟账本…';try{
  charts.forEach(c=>c.dispose());charts=[];csrf=(await api('/session')).csrfToken;
  const [catalog,rs,ss,os]=await Promise.all([api('/catalog'),role==='doctor'?Promise.resolve([]):api('/recharges'),api('/sales'),api('/orders')]);sales=ss;orders=os;let html='';
  const drugs=catalog.map(d=>[d.drugId,d.name+' / ¥'+d.price+' / 库存 '+d.stock]);
  if(role==='patient'){
   const [me,cards,security,ledger]=await Promise.all([api('/me'),api('/cards'),api('/payment-password'),api('/patient-transactions')]);
   html+=card('可用余额','<h2>¥ '+esc(me.balance)+'</h2><p>充值仅在管理员确认收款后到账；退款只退回本系统余额，不提现。</p>');
   html+=card('模拟充值',form('recharge',moneyFields()+select('channel','模拟渠道',[['WECHAT','模拟微信'],['ALIPAY','模拟支付宝'],['BANK','模拟银行卡']])+select('cardId','模拟银行卡',[['','非银行卡渠道可不选']].concat(cards.map(c=>[c.id,(c.issuer||'演示卡')+' '+c.maskedNumber])))+pass(),'提交，等待管理员确认'));
   html+=card('充值记录',table(rs,[['id','编号'],['amount','金额'],['channel','模拟渠道'],['status','状态'],['requestedAt','申请时间']]));
   html+=card('系统支付密码 · '+(security.configured?'已设置':'未设置'),'<p>这是本演示系统独立密码，勿使用真实银行或支付平台密码。6–32 字符。</p>'+form('password',field('loginPassword','登录密码','password','autocomplete="current-password"')+field('currentPaymentPassword','原支付密码','password','autocomplete="off"')+field('newPaymentPassword','新支付密码','password','required minlength="6" maxlength="32" autocomplete="new-password"'),'保存系统支付密码'));
   html+=card('模拟银行卡','<p>仅保存演示名称和四位尾号，不绑定银行，不输入完整卡号或 CVV。</p>'+table(cards,[['issuer','演示名称'],['maskedNumber','脱敏尾号']],r=>btn('delete-card',r.id,'删除'))+form('card',field('issuer','演示名称','text','required maxlength="40" placeholder="例如：测试银行"')+field('lastFour','虚构四位尾号','text','required pattern="[0-9]{4}" maxlength="4" inputmode="numeric"'),'添加模拟卡'));
   html+=card('购买药品',form('order',select('drugId','药品',drugs)+field('quantity','数量','number','required min="1" step="1"'),'创建余额待支付订单'));
   html+=card('我的账单',table(ledger,ledgerCols)+'<div id="wallet-chart"></div>');
   setTimeout(()=>{const totals={};ledger.filter(r=>r.book==='PATIENT_BALANCE').forEach(r=>{const d=new Intl.DateTimeFormat('en-CA',{timeZone:'Asia/Shanghai',year:'numeric',month:'2-digit',day:'2-digit'}).format(new Date(r.createdAt));totals[d]=totals[d]||{period:d,income:0,expense:0};totals[d][r.direction==='IN'?'income':'expense']+=Math.round(Number(r.amount)*100);});plot('wallet-chart',Object.values(totals).map(r=>({...r,income:r.income/100,expense:r.expense/100})).sort((a,b)=>a.period.localeCompare(b.period)),[['income','余额入账'],['expense','余额支出']]);},0);
  }else{
   const policies=await api('/drug-policies');html+=card('药品提成比例（仅医护/管理可见）',table(policies,[['drugId','药品编号'],['name','药品'],['stock','库存'],['price','售价'],['commissionRate','提成 %']],role==='admin'?r=>btn('rate',r.drugId,'设置比例'):null));
   if(role==='admin'){
    const [patients,cash,doctors,patientLedger,procurements]=await Promise.all([api('/patients'),api('/cash-transactions'),api('/doctors'),api('/patient-transactions'),api('/procurements')]);
    html=card('待确认模拟充值',table(rs,[['id','编号'],['patientId','患者'],['amount','金额'],['channel','渠道'],['status','状态']],r=>r.status==='PENDING'?btn('confirm',r.id,'确认模拟收款'):'已处理'))+html;
    const taxPolicy=await api('/tax-policy');html+=card('购买票据演示税率','<p>现有售价为含税总价：只拆分未税金额和税额，不增加付款金额。仅作用于设置后新建的订单。</p><p>'+esc(taxPolicy.notice)+'；当前：'+esc(taxPolicy.configured?taxPolicy.rate+'%':'未配置（演示0%）')+'</p>'+form('tax-policy',field('rate','演示税率 %','number','required min="0" max="100" step="0.0001"'),'保存新订单演示税率'));
    html+=card('管理员分配医生账号','<p>医生不能通过公众注册认领档案；这里只为尚未分配账号的医生建立独立登录。</p>'+form('doctor-account',select('doctorId','医生',doctors.filter(d=>!d.accountAssigned).map(d=>[d.doctorId,d.name]))+field('username','登录账号','text','required maxlength="64" autocomplete="off"')+field('password','初始登录密码','password','required minlength="8" maxlength="72" autocomplete="new-password"'),'分配医生登录账号'));
    html+=card('患者账务流水（与现金账分开）','<p>这里保留充值、购药与退回余额记录，不将余额支付再次计入现金收入。</p>'+table(patientLedger,[['patientId','患者'],['book','账本']].concat(ledgerCols)));
    html+=card('现金购药登记',form('order',select('patientId','患者',patients.map(p=>[p.patientId,p.name]))+select('drugId','药品',drugs)+field('quantity','数量','number','required min="1" step="1"'),'建立现金待确认订单'));
    html+=card('药品进货',form('procurement',select('drugId','药品',drugs)+field('quantity','数量','number','required min="1" step="1"')+field('unitCost','单价（元）','number','required min="0.01" step="0.01"')+field('reference','进货凭据','text','required maxlength="100"'),'登记模拟支出并入库'));
    html+=card('进货明细',table(procurements,[['createdAt','时间'],['drugName','药品'],['quantity','数量'],['unitCost','单价'],['amount','支出'],['reference','凭据']]));
    html+=card('现金收支','<p>余额购药不重复记现金收入；退款到余额不产生现金支出。</p><select id="cash-period" class="layui-input"><option value="DAY">每日</option><option value="WEEK">每周（周一开始）</option><option value="MONTH">每月</option></select><div id="cash-chart"></div>'+table(cash,ledgerCols));
    html+=card('医生接诊与提成',select('reportDoctor','医生',doctors.map(d=>[d.doctorId,d.name]))+'<select id="doctor-period" class="layui-input"><option value="DAY">每日</option><option value="WEEK">每周</option><option value="MONTH">每月</option></select><div id="doctor-chart"></div>');
   }else{
    const appts=await api('/encounters/appointments');html=card('实际接诊完成登记','<p>完成真实诊疗后再登记；未来预约不能提前完成。不是页面访问次数。</p>'+table(appts,[['appointmentId','预约'],['patientName','患者'],['date','预约日期'],['status','状态']],r=>r.encounterId?'已登记':btn('encounter',r.appointmentId,'登记接诊完成')))+html;
    const encounters=await api('/encounters');html+=card('为已完成接诊开购药订单','<p>这里只创建订单，由患者在自己的钱包页面输入系统支付密码完成支付。</p>'+form('order',select('encounterId','已完成接诊',encounters.map(e=>[e.id,'接诊 '+e.id+' / 患者 '+e.patientId]))+select('drugId','药品',drugs)+field('quantity','数量','number','required min="1" step="1"'),'创建患者待支付订单'));
    window.financeEncounters=encounters;
    html+=card('我的接诊与提成','<p>上海时区；每周从周一开始。退款按原成交提成冲回，可能出现负值。</p><select id="doctor-period" class="layui-input"><option value="DAY">每日</option><option value="WEEK">每周</option><option value="MONTH">每月</option></select><div id="doctor-chart"></div>');
   }
  }
  html+=card('待支付及历史订单',table(os,[['id','订单'],['drugId','药品'],['quantity','数量'],['amount','金额'],['paymentMethod','方式'],['status','状态']],r=>r.status==='PENDING'&&(role==='patient'||role==='admin'&&r.paymentMethod==='CASH')?btn('pay',r.id,r.paymentMethod==='CASH'?'确认模拟现金收款':'余额支付'):''));
  html+=card('购药与退款记录','<p>退款仅由管理员按原购药记录办理，退回余额；如需退款请联系管理员。退款不代表药品已退回库存。</p>'+table(ss,[['id','销售单'],['drugId','药品'],['amount','金额'],['refundedAmount','已退款'],['createdAt','时间']].concat(role==='patient'?[]:[['commission','提成'],['reversedCommission','已冲回提成']]),role==='patient'||role==='admin'?r=>'<a class="layui-btn layui-btn-primary layui-btn-sm" href="/static/finance-receipt.html?saleId='+encodeURIComponent(String(r.id))+'" target="_blank" rel="noopener" title="查看 / 打印购买凭证">'+(role==='patient'?'查看 / 打印购买凭证':'购买凭证')+'</a>'+(role==='admin'?btn('refund',r.id,'退回余额'):''):null));
  app.innerHTML=html;layui.element.render();
  bindForm('recharge','/recharges','POST',d=>{if(d.channel!=='BANK')delete d.cardId;return d;});bindForm('password','/payment-password','PUT');bindForm('card','/cards','POST');
  bindForm('order','/orders','POST',d=>{d.drugId=Number(d.drugId);d.quantity=Number(d.quantity);d.paymentMethod=role==='admin'?'CASH':'WALLET';if(d.patientId)d.patientId=Number(d.patientId);if(d.encounterId){const e=(window.financeEncounters||[]).find(e=>String(e.id)===d.encounterId);if(e)d.patientId=e.patientId;}return d;});
  bindForm('tax-policy','/tax-policy','PUT');
  bindForm('doctor-account','/doctor-accounts','POST',d=>({...d,doctorId:Number(d.doctorId)}));
  bindForm('procurement','/procurements','POST',d=>({...d,drugId:Number(d.drugId),quantity:Number(d.quantity)}));
  const cashPeriod=document.getElementById('cash-period');if(cashPeriod){cashPeriod.onchange=()=>report('cash');await report('cash');}
  const doctorPeriod=document.getElementById('doctor-period');if(doctorPeriod){doctorPeriod.onchange=()=>report('doctor');const ds=document.getElementById('f-reportDoctor');if(ds)ds.onchange=()=>report('doctor');await report('doctor');}
  status.textContent='已更新。所有金额来自本地模拟账本，未连接真实支付。';
 }catch(e){status.textContent=e.message;}finally{loading=false;}}
 async function report(type){try{const period=document.getElementById(type+'-period').value;const doctor=document.getElementById('f-reportDoctor');if(type==='doctor'&&doctor&&!doctor.value)return;const data=await api('/reports/'+type+'?period='+period+(type==='doctor'&&doctor?'&doctorId='+encodeURIComponent(doctor.value):''));const node=document.getElementById(type+'-chart');const old=echarts.getInstanceByDom(node);if(old)old.dispose();plot(type+'-chart',data.points,type==='cash'?[['income','现金收入'],['expense','现金支出'],['net','净额','line']]:[['encounters','接诊次数'],['commission','药物提成','line']]);}catch(e){status.textContent=e.message;}}
 const actionKeys=new Map();
 app.addEventListener('click',async e=>{const b=e.target.closest('[data-action]');if(!b||b.disabled)return;const action=b.dataset.action,id=b.dataset.id;let path,method='POST',body={},title,fields='';
  if(action==='confirm'){path='/recharges/'+id+'/confirm';title='确认已收到这笔模拟充值？';}
  if(action==='delete-card'){path='/cards/'+id;method='DELETE';title='删除这张模拟银行卡？';}
  if(action==='encounter'){path='/encounters/complete';body={appointmentId:Number(id)};title='确认这次接诊已实际完成？';}
  if(action==='pay'){const order=orders.find(r=>String(r.id)===id);path='/orders/'+id+'/pay';title='支付订单 '+id+'，金额 ¥'+order.amount;fields=order.paymentMethod==='WALLET'?pass():'';}
  if(action==='rate'){path='/drug-policies/'+id;method='PUT';title='设置药品提成百分比';fields=field('commissionRate','百分比','number','required min="0" max="100" step="0.0001"');}
  if(action==='refund'){path='/sales/'+id+'/refund';title='退款到患者余额（非现金退款）';fields=moneyFields()+field('reason','退款原因','text','required maxlength="200"');}
  if(!path)return;
  const dialog=document.createElement('div');dialog.className='layui-fluid';dialog.innerHTML='<blockquote class="layui-elem-quote">'+esc(title)+'</blockquote>'+form('action-form',fields,'确认');document.body.appendChild(dialog);const idx=layui.layer.open({type:1,title:'模拟业务确认',area:[Math.min(520,window.innerWidth-24)+'px','auto'],skin:role==='patient'?'patient-dialog':'',content:layui.$(dialog),shadeClose:false,success:function(layero){layero.attr('role','dialog');layero.attr('aria-modal','true');layero.attr('aria-label',title);const first=dialog.querySelector('input,button');if(first)first.focus();},end:function(){dialog.remove();b.focus();}});
  dialog.addEventListener('keydown',function(ev){if(ev.key==='Escape'){ev.preventDefault();layui.layer.close(idx);}if(ev.key==='Tab'){const nodes=Array.from(dialog.querySelectorAll('input,button')).filter(n=>!n.disabled);const first=nodes[0],last=nodes[nodes.length-1];if(ev.shiftKey&&document.activeElement===first){ev.preventDefault();last.focus();}else if(!ev.shiftKey&&document.activeElement===last){ev.preventDefault();first.focus();}}});
  const f=dialog.querySelector('form');f.onsubmit=async ev=>{ev.preventDefault();const submit=f.querySelector('button');if(submit.disabled)return;const data=Object.assign({},body,Object.fromEntries(new FormData(f)));const identity=action+':'+id+':'+JSON.stringify(Object.fromEntries(Object.entries(data).filter(([k])=>k!=='paymentPassword')));if(!actionKeys.has(identity))actionKeys.set(identity,key());submit.disabled=true;b.disabled=true;try{await api(path,method,data,actionKeys.get(identity));actionKeys.delete(identity);layui.layer.close(idx);await load();}catch(err){f.querySelector('.form-status').textContent=err.message;if(err.definite)actionKeys.delete(identity);}finally{f.querySelectorAll('input[type=password]').forEach(i=>i.value='');submit.disabled=false;b.disabled=false;}};
 });
 document.getElementById('finance-refresh').onclick=load;window.addEventListener('resize',()=>charts.forEach(c=>{if(!c.isDisposed())c.resize();}));layui.use(['element','layer'],load);
})();
