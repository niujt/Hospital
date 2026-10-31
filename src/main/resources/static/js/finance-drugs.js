/* Existing admin drug forms use the same atomic catalog+commission transaction. */
(function(root){
 'use strict';
 const form=document.querySelector('[data-finance-drug-form]'), cells=document.querySelectorAll('[data-policy-drug]');
 let csrf='',ready=false,key=null,lastPayload=null;
 function uuid(){return crypto.randomUUID?crypto.randomUUID():Date.now()+'-'+Array.from(crypto.getRandomValues(new Uint32Array(4))).join('-');}
 async function get(path){const r=await fetch('/api/finance'+path,{credentials:'same-origin'});if(!r.ok)throw new Error('无权读取提成，或会话已过期。');return r.json();}
 async function init(){try{
  const session=await get('/session');csrf=session.csrfToken;
  const policies=await get('/drug-policies');
  cells.forEach(cell=>{const row=policies.find(d=>String(d.drugId)===cell.dataset.policyDrug);cell.textContent=row?row.commissionRate+'%':'未找到政策';});
  if(form){if(form.dataset.financeDrugForm==='edit'){const id=form.elements.id.value;const row=policies.find(d=>String(d.drugId)===id);if(!row)throw new Error('无法读取该药品提成，请刷新后重试。');form.elements.commissionRate.value=row.commissionRate;}
   ready=true;document.getElementById('drug-policy-status').textContent='提成仅医护可见，保存与药品资料同时提交。';}
 }catch(e){cells.forEach(c=>c.textContent='未读取（请重新登录）');if(form)document.getElementById('drug-policy-status').textContent=e.message;}}
 root.HospitalDrugForm={submit:function(f){
  if(!ready){HospitalFeedback.result('数据尚未加载完成，请稍后重试。');return false;}
  if(!f.reportValidity())return false;
  const d=Object.fromEntries(new FormData(f));const id=d.id;delete d.id;delete d.number;if(d.type===''){HospitalFeedback.result('请选择药品类型。');return false;}d.type=Number(d.type);
  if(!Number.isInteger(d.type)||d.type<0||d.type>5){HospitalFeedback.result('请选择药品类型。');return false;}
  const payload=JSON.stringify(d);if(lastPayload!==payload){key=uuid();lastPayload=payload;}
  HospitalFeedback.request({url:'/api/finance/drugs'+(id?'/'+encodeURIComponent(id):''),type:id?'PUT':'POST',headers:{'X-CSRF-Token':csrf,'Idempotency-Key':key},data:payload,contentType:'application/json;charset=utf-8',success:function(data){key=null;lastPayload=null;HospitalFeedback.result(data.message||'保存成功',function(){parent.location.reload();});},error:function(xhr){if(xhr.status&&xhr.status<500){key=null;lastPayload=null;}HospitalFeedback.error(xhr);}});
  return false;
 }};
 init();
}(window));
