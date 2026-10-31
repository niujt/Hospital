'use strict';
const assert=require('node:assert/strict'),fs=require('node:fs');const {JSDOM}=require('jsdom');
(async()=>{
 const d=new JSDOM('<td data-policy-drug="1"></td><form data-finance-drug-form="edit"><input name="id" value="1"><input name="name" value="演示药"><select name="type"><option value="2" selected>散剂</option></select><input name="price" value="12.00"><input name="number" value="999"><input name="commissionRate" value="0"><textarea name="text">简介</textarea></form><p id="drug-policy-status"></p>',{runScripts:'outside-only',url:'http://localhost/admin/drug/1'});const w=d.window;let requests=[];
 w.fetch=async url=>({ok:true,json:async()=>url.endsWith('/session')?{csrfToken:'csrf'}:[{drugId:1,commissionRate:'12.5000'}]});
 w.HospitalFeedback={request:o=>requests.push(o),result:()=>{},error:()=>{}};w.eval(fs.readFileSync('src/main/resources/static/js/finance-drugs.js','utf8'));await new Promise(r=>setTimeout(r,20));
 const f=w.document.querySelector('form');assert.equal(f.elements.commissionRate.value,'12.5000');w.HospitalDrugForm.submit(f);assert.equal(requests[0].url,'/api/finance/drugs/1');assert.equal(requests[0].type,'PUT');const body=JSON.parse(requests[0].data);assert.equal(body.commissionRate,'12.5000');assert(!('number'in body));assert(!('id'in body));assert.equal(body.type,2);assert.equal(requests[0].headers['X-CSRF-Token'],'csrf');
 const key=requests[0].headers['Idempotency-Key'];requests[0].error({status:500});w.HospitalDrugForm.submit(f);assert.equal(requests[1].headers['Idempotency-Key'],key);f.elements.commissionRate.value='10';w.HospitalDrugForm.submit(f);assert.notEqual(requests[2].headers['Idempotency-Key'],key);
 d.window.close();console.log('Existing drug form checks passed: policy prefill, atomic endpoint, no stock write, CSRF, stable retry/new intent.');
})().catch(e=>{console.error(e);process.exit(1);});
