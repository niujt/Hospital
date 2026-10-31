const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const api = require('../src/main/resources/static/js/finance-receipt.js');
const valid = () => ({
 receiptType:'DEMO_PURCHASE_RECEIPT',title:'药品购买凭证',notice:'模拟票据，非报销凭证',
 saleId:42,issuedAt:'2026-10-31T12:00:00Z',nameSource:'CURRENT_RECORD',paymentMethod:'BALANCE',
 patient:{patientId:8,name:'演示患者'},
 items:[{drugId:1,name:'演示药品 <img onerror=bad()>',quantity:2,unitPrice:'12.30',amount:'24.60'}],
 totalAmount:'24.60',refundedAmount:'0.00',netAmount:'24.60',refundStatus:'NONE',
 tax:{rate:'0.0000',configured:false,inclusive:true,netAmount:'24.60',taxAmount:'0.00',refundedTaxAmount:'0.00'},
 commission:'MUST_NOT_RENDER',bankAccount:'MUST_NOT_RENDER'
});
assert.equal(api.saleId('?saleId=42'),'42');
for(const query of ['', '?saleId=../1','?saleId=1&saleId=2','?saleId=%3Cscript%3E']) assert.throws(()=>api.saleId(query));
assert.equal(api.money('0012.3'),'12.30');
assert.equal(api.money('12.3000'),'12.30');
assert.equal(api.money('9007199254740993.12'),'9007199254740993.12'); // No floating-point loss.
for(const amount of ['',null,true,-1,Infinity,'NaN','12.301']) assert.throws(()=>api.money(amount));
assert.equal(api.maskedName('演示患者'),'演***');
assert.equal(api.maskedName('张*'),'张*');
assert.equal(api.maskedName('李'),'*');
assert.equal(api.issuedAt('2026-10-31T12:00:00Z'),'2026-10-31 20:00:00 北京时间');
assert.equal(api.issuedAt('2026-10-31T23:59:59Z'),'2026-11-01 07:59:59 北京时间');
assert.equal(api.issuedAt('2026-10-31T12:00:00+08:00'),'2026-10-31 12:00:00 北京时间');
for (const time of ['2026-10-31T12:00:00','2026-02-30T12:00:00Z','not-a-date','2026-10-31T24:00:00Z']) assert.throws(()=>api.issuedAt(time));
assert.equal(api.normalize({...valid(),paymentMethod:'CASH'},'42').paymentMethod,'现金登记');
let receipt=api.normalize(valid(),'42');
assert.equal(receipt.nameNote,'名称按当前档案显示，成交金额以原订单记录为准。');
assert.equal(receipt.taxRate,'未配置（演示0%）');assert.equal(receipt.taxAmount,'0.00');
assert.equal(receipt.net,'24.60');assert.equal(receipt.status,'未退款');
for(const status of ['NONE','PARTIAL','FULL']) {
 const data=valid();data.refundStatus=status;assert(api.normalize(data,'42').status);
}
for(const change of [
 {receiptType:'OFFICIAL_INVOICE'}, {saleId:43}, {items:[]}, {refundStatus:'UNKNOWN'},
 {netAmount:null}, {tax:null}, {tax:{rate:'101',inclusive:true,configured:true}}, {patient:null}, {issuedAt:''}, {items:[{name:'bad',quantity:true,unitPrice:1,amount:1}]}
]) assert.throws(()=>api.normalize({...valid(),...change},'42'));
function node() {
 return {textContent:'',hidden:true,disabled:true,children:[],attrs:{},events:{},
  setAttribute(k,v){this.attrs[k]=v;},appendChild(n){this.children.push(n);},
  addEventListener(k,v){this.events[k]=v;}};
}
function mockDoc() {
 const nodes=new Map();
 const doc={getElementById:id=>{if(!nodes.has(id))nodes.set(id,node());return nodes.get(id);},createElement:()=>node()};
 return {doc,nodes};
}
let {doc,nodes}=mockDoc();api.render(doc,receipt);
assert.equal(nodes.get('receipt-patient').textContent,'演***');
assert.equal(nodes.get('receipt-name-note').hidden,false);
assert(nodes.get('receipt-name-note').textContent.includes('当前档案'));
assert.equal(nodes.get('receipt-items').children[0].children[1].textContent,'演示药品 <img onerror=bad()>');
assert(!JSON.stringify([...nodes.values()]).includes('MUST_NOT_RENDER'));
async function run(response,search='?saleId=42') {
 const {doc,nodes}=mockDoc();let prints=0,fetches=[];
 const win={document:doc,location:{search},AbortController,
  setTimeout:()=>1,clearTimeout:()=>{},print:()=>prints++,
  fetch:async(url,options)=>{fetches.push({url,options});if(response instanceof Error)throw response;return response;}};
 await api.start(win);
 return {nodes,fetches,get prints(){return prints;}};
}
(async()=>{
 const success=await run({ok:true,status:200,json:async()=>valid()});
 assert.equal(success.fetches.length,1);
 assert.equal(success.fetches[0].url,'/api/finance/sales/42/receipt');
 assert.equal(success.fetches[0].options.method,'GET');
 assert.equal(success.fetches[0].options.credentials,'same-origin');
 assert.equal(success.fetches[0].options.redirect,'error');
 assert.equal(success.nodes.get('purchase-receipt').hidden,false);
 assert.equal(success.nodes.get('print-receipt').disabled,false);
 assert.equal(success.nodes.get('download-receipt').href,'/api/finance/sales/42/receipt.pdf');
 assert.equal(success.nodes.get('download-receipt').hidden,false);
 assert.equal(success.prints,0); // Never automatically opens a print dialog.
 success.nodes.get('print-receipt').events.click();assert.equal(success.prints,1);
 for(const response of [
  {ok:false,status:401},{ok:false,status:403},{ok:false,status:404},{ok:false,status:500},
  {ok:true,status:200,json:async()=>({...valid(),saleId:99})},
  {ok:true,status:200,json:async()=>{throw new Error('not JSON');}},
  new TypeError('network failure')
 ]) {
  const failed=await run(response);
  assert.equal(failed.nodes.get('purchase-receipt').hidden,true);
  assert.equal(failed.nodes.get('print-receipt').disabled,true);
  assert.equal(failed.nodes.get('download-receipt').hidden,true);
  assert.equal(failed.nodes.get('receipt-state').attrs.role,'alert');
  failed.nodes.get('print-receipt').events.click();assert.equal(failed.prints,0);
 }
 const bad=await run({ok:true,status:200,json:async()=>valid()},'?saleId=../x');
 assert.equal(bad.fetches.length,0);
 const long=valid();long.items=Array.from({length:150},(_,i)=>({...long.items[0],name:'演示项目'+i}));
 const longDoc=mockDoc();api.render(longDoc.doc,api.normalize(long,'42'));
 assert.equal(longDoc.nodes.get('receipt-items').children.length,150);
 const base=path.join(__dirname,'../src/main/resources/static');
 const html=fs.readFileSync(path.join(base,'finance-receipt.html'),'utf8');
 const css=fs.readFileSync(path.join(base,'css/finance-receipt.css'),'utf8');
 const js=fs.readFileSync(path.join(base,'js/finance-receipt.js'),'utf8');
 assert((html.match(/模拟票据，非报销凭证/g)||[]).length>=3);
 assert(css.includes('@page { size: A4 portrait;'));
 assert(css.includes('display: table-header-group'));
 assert(css.includes('page-break-inside: avoid'));
 assert(!js.includes('innerHTML'));
 assert(!/method:\s*['"](?:POST|PUT|DELETE)/.test(js));
 console.log('Receipt tests passed: exact money/masking/schema, 150 rows, text-only rendering, authenticated GET, permissions/network failures, disabled print on error, click-only print, A4 table-header pagination guards.');
})().catch(error=>{console.error(error);process.exitCode=1;});
