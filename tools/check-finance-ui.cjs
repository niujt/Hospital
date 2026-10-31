'use strict';
const assert=require('node:assert/strict');const fs=require('node:fs');
// Test-only dependency supplied by NODE_PATH; not bundled into production.
const {JSDOM}=require('jsdom');
const code=fs.readFileSync('src/main/resources/static/js/finance.js','utf8');
const pause=()=>new Promise(r=>setTimeout(r,100));
async function test(role){
 const dom=new JSDOM(`<p id="finance-status"></p><button id="finance-refresh"></button><div id="finance-app" data-role="${role}"></div>`,{url:'http://localhost/finance/'+role,runScripts:'outside-only'});const w=dom.window;const calls=[],plots=[];
 const routes={session:{csrfToken:'test-csrf'},catalog:[{drugId:1,name:'<script>unsafe</script>',stock:10,price:'10.00'}],recharges:[],sales:[{id:'sale-uuid',drugId:1,amount:'20.00',refundedAmount:'5.00',createdAt:'2026-10-31T12:00:00Z'}],orders:[],me:{patientId:1,balance:'100.00'},cards:[{id:'card-uuid',issuer:'演示银行',maskedNumber:'**** **** **** 1234'}],'payment-password':{configured:true},'patient-transactions':[{id:'t1',book:'PATIENT_BALANCE',direction:'IN',amount:'100.00',createdAt:'2026-10-30T18:00:00Z'}],'drug-policies':[{drugId:1,name:'药',commissionRate:'20',stock:10,price:'10'}],patients:[{patientId:1,name:'演示患者'}],doctors:[{doctorId:2,name:'演示医生'}],'cash-transactions':[],'encounters/appointments':[],encounters:[{id:'encounter-uuid',patientId:1}],'tax-policy':{rate:'0.0000',configured:false,notice:'演示税率，不代表实际适用税率'},'reports/cash':{points:[]},'reports/doctor':{points:[]}};
 w.fetch=async(url,options)=>{calls.push({url,options});const path=url.replace('/api/finance/','').split('?')[0];return {ok:true,status:200,json:async()=>options.method==='GET'?routes[path]||[]:{id:'new',status:'PENDING'}};};
 w.echarts={init:()=>({setOption:o=>plots.push(o),dispose(){},isDisposed:()=>false,resize(){}}),getInstanceByDom:()=>null};
 let opened; w.layui={$:node=>node,element:{render(){}},use(_mods,cb){cb();},layer:{open(options){opened=options;assert(options.content.isConnected,'Layer content must be attached before open');return 1;},close(){if(opened&&opened.end)opened.end();}}};
 w.eval(code);await pause();assert.match(w.document.getElementById('finance-status').textContent,/已更新/);assert.equal(w.document.querySelector('script'),null);assert.match(w.document.querySelector('[name=drugId]').textContent,/<script>unsafe/);
 if(role==='patient'){
  assert.equal(w.document.querySelector('a[href*=finance-receipt]').getAttribute('href'),'/static/finance-receipt.html?saleId=sale-uuid');
  assert(!calls.some(c=>c.url.includes('drug-policies')));assert(!w.document.getElementById('finance-app').textContent.includes('20%'));
  assert.match(w.document.querySelector('[name=cardId]').textContent,/1234/);assert.equal(plots[0].series[0].data[0],100);
  const f=w.document.getElementById('recharge');f.elements.amount.value='25.00';f.elements.channel.value='BANK';f.elements.cardId.value='card-uuid';f.elements.paymentPassword.value='test-only-password';f.dispatchEvent(new w.Event('submit',{cancelable:true}));await pause();
  const post=calls.find(c=>c.options.method==='POST'&&c.url.endsWith('/recharges'));assert(post);const body=JSON.parse(post.options.body);assert.equal(body.cardId,'card-uuid');assert.equal(post.options.headers['X-CSRF-Token'],'test-csrf');assert(post.options.headers['Idempotency-Key']);assert.equal(f.elements.paymentPassword.value,'');
 }
 if(role==='doctor'){
  const f=w.document.getElementById('order');f.elements.drugId.value='1';f.elements.quantity.value='2';f.elements.encounterId.value='encounter-uuid';f.dispatchEvent(new w.Event('submit',{cancelable:true}));await pause();const post=calls.find(c=>c.options.method==='POST');const body=JSON.parse(post.options.body);assert.equal(body.encounterId,'encounter-uuid');assert.equal(body.patientId,1);assert.equal(body.paymentMethod,'WALLET');assert(!('paymentPassword'in body));
 }
 if(role!=='patient') {
  const cells=[...w.document.querySelectorAll('thead th')].filter(n=>n.textContent==='操作');
  cells.forEach(n=>assert.equal(n.getAttribute('width'),'240'));
  assert.equal(w.document.querySelector('.layui-btn-group'),null);
 }
 if(role==='patient') assert.equal(w.document.querySelector('[aria-label="记录操作"]'),null,'Patient layout unchanged');
 if(role==='admin') {
  const refund=w.document.querySelector('[data-action=refund]');
  assert(refund.closest('.layui-btn-container'));
  assert(refund.classList.contains('layui-btn-primary'));
  assert.equal(refund.dataset.id,'sale-uuid');
  assert.equal(refund.previousElementSibling.textContent,'购买凭证');
  assert.equal(refund.previousElementSibling.title,'查看 / 打印购买凭证');
  refund.disabled=true;refund.click();assert.equal(opened,undefined,'Disabled action must not open a dialog');refund.disabled=false;
 }
 if(role==='admin'){assert(w.document.getElementById('tax-policy'));assert(w.document.getElementById('procurement'));assert(calls.some(c=>c.url.includes('reports/cash'))); const rateButton=w.document.querySelector('[data-action=rate]');rateButton.click();assert(opened);assert(w.document.getElementById('action-form'));w.document.getElementById('action-form').dispatchEvent(new w.KeyboardEvent('keydown',{key:'Escape',bubbles:true}));assert.equal(w.document.getElementById('action-form'),null);}
 if(role==='patient') {
  // Non-ledger card/password endpoints have no idempotency contract: ambiguous error locks submit.
  const before=w.fetch;w.fetch=async(url,options)=>{if(options.method==='POST'&&url.endsWith('/cards'))throw new Error('lost response');return before(url,options);};
  const cf=w.document.getElementById('card');cf.elements.issuer.value='测试银行';cf.elements.lastFour.value='5678';cf.dispatchEvent(new w.Event('submit',{cancelable:true}));await pause();assert.match(cf.querySelector('.form-status').textContent,/不要直接重复提交/);assert.equal(cf.querySelector('button').disabled,true);w.fetch=before;
  // Network ambiguity: retain the same intent across refresh, never retain the password.
  const realFetch=w.fetch;let firstKey, retryKey, rejectOnce=true;
  w.fetch=async(url,options)=>{if(options.method==='POST'&&url.endsWith('/recharges')){
    if(rejectOnce){rejectOnce=false;firstKey=options.headers['Idempotency-Key'];throw new Error('lost response');}
    retryKey=options.headers['Idempotency-Key'];
  }return realFetch(url,options);};
  function fill(){const f=w.document.getElementById('recharge');f.elements.amount.value='31.00';f.elements.channel.value='WECHAT';f.elements.paymentPassword.value='test-only-password';return f;}
  let f=fill();f.dispatchEvent(new w.Event('submit',{cancelable:true}));await pause();assert.match(f.querySelector('.form-status').textContent,/结果待核实/);assert.equal(f.elements.paymentPassword.value,'');
  w.document.getElementById('finance-refresh').click();await pause();f=fill();f.dispatchEvent(new w.Event('submit',{cancelable:true}));await pause();assert.equal(firstKey,retryKey);
 }
 dom.window.close();
}
(async()=>{for(const role of ['patient','doctor','admin'])await test(role);console.log('Finance UI jsdom checks passed: three roles, safe text, patient privacy, UUID IDs, CSRF/idempotency, password clearing, real ledger chart data.');})().catch(e=>{console.error(e);process.exit(1);});
