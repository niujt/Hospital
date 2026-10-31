/* Source + VM contracts. Browser integration is validated separately in the isolated app. */
const fs=require('node:fs'),vm=require('node:vm'),assert=require('node:assert/strict');
const read=n=>fs.readFileSync('src/main/resources/static/js/'+n,'utf8');
const field={name:'diagnosis',value:'',type:'text'}, hidden={name:'id',value:'1',type:'hidden'};
const form={method:'post',hasAttribute:()=>false,querySelectorAll:s=>s.startsWith('input,')?[field,hidden]:[]};
const doc={querySelectorAll:s=>s==='form'?[form]:[],querySelector:()=>null,documentElement:{},addEventListener(){}};
const win={document:doc,MutationObserver:class{observe(){}},setTimeout:fn=>fn()};
vm.runInNewContext(read('hospital-drafts.js'),{window:win});
const tracker=win.HospitalDrafts.track(win);assert.equal(tracker.dirty(),false);hidden.value='2';assert.equal(tracker.dirty(),false);field.value='draft';assert.equal(tracker.dirty(),true);field.value='';assert.equal(tracker.dirty(),false);assert.equal(tracker.pending(),false);
function context(search, rows, selectedValue){
 let capture, blocked=false, note;
 const attrs={},button={disabled:false,setAttribute(k,v){attrs[k]=v},hasAttribute:k=>k in attrs,removeAttribute:k=>delete attrs[k]};
 const select={value:selectedValue,getAttribute:k=>attrs[k],setAttribute(k,v){attrs[k]=v},replaceChildren(o){this.value=o.value}};
 const order={id:'order',querySelector:s=>s.includes('encounterId')?select:button};
 const app={getAttribute:()=> 'doctor',parentNode:{insertBefore(n){note=n}},querySelectorAll:()=>[],addEventListener:(n,fn)=>capture=fn};
 const document={getElementById:id=>id==='finance-app'?app:order,createElement:()=>({setAttribute(){}})};
 const w={location:{search},document,financeEncounters:rows,MutationObserver:class{observe(){}}};
 vm.runInNewContext(read('doctor-order-context.js'),{window:w,URLSearchParams,Option:function(text,value){this.value=value}});
 if(capture)capture({target:order,preventDefault(){blocked=true},stopImmediatePropagation(){}});
 return {blocked,button,select,note};
}
assert.equal(context('?patientId=7&appointmentId=9',[{id:11,patientId:7,appointmentId:9}],'other').blocked,false);
assert.equal(context('?patientId=7&appointmentId=9',[{id:11,patientId:8,appointmentId:9}],'11').blocked,true);
assert.equal(context('?patientId=7&appointmentId=9',[{id:11,patientId:7,appointmentId:10}],'11').button.disabled,true);
assert.equal(context('?patientId=7&appointmentId=',[],'').blocked,true);
assert.equal(context('',[],'').note,undefined);
const workspace=read('admin-workspace.js');assert.match(workspace,/beforeClose\(admin-workspace\)/);assert.match(workspace,/tabs.close\('admin-workspace', id, true\)/);assert.match(workspace,/entries.values\(\)\)\.find/);assert.match(workspace,/HospitalDrafts.track/);assert.doesNotMatch(workspace,/localStorage|sessionStorage|fetch\(/);
const modal=read('hospital-modal.js');assert.match(modal,/bridge.hasChanges/);assert.match(modal,/save.hidden = !ready && !pending/);
const page=fs.readFileSync('src/main/resources/templates/doctor/seek.ftl','utf8');assert.match(page,/encounter-history/);assert.match(page,/encounter-orders/);assert.doesNotMatch(page,/id="form2"|function drug\(/);
console.log('Workspace contracts passed: in-memory dirty guard, ignored hidden values, context patient+appointment match/mismatch/missing, standalone unchanged, native tab close/dedup, readonly footer.');

assert.match(workspace, /location.href === 'about:blank'/);
assert.match(workspace, /location.pathname !== entry.url/);
assert.ok(workspace.indexOf('frame.src = url;') < workspace.indexOf('pane.appendChild(frame);'));
