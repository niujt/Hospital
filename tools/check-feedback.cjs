const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const path = require('node:path');
const root = path.resolve(__dirname, '..');
let calls = [], finish, classes = new Set(), attrs = {};
const window = {document:{body:{classList:{contains:c=>classes.has(c)}}},layui:{use:(name, cb)=>cb(),layer:{msg:(text, options, done)=>{
  calls.push({text, options}); finish=done;
  options.success({attr:(key,value)=>attrs[key]=value});
}}}};
vm.runInNewContext(fs.readFileSync(path.join(root,'src/main/resources/static/js/hospital-feedback.js'),'utf8'),{window});
let refresh=0;
window.HospitalFeedback.result('添加成功',()=>refresh++);
assert.equal(refresh,0); finish(); assert.equal(refresh,1);
assert.equal(calls.at(-1).options.icon,1);
assert.equal(calls.at(-1).options.skin,'');
for (const text of ['更新失败','基本信息更新成功，但是账号无法更新','数量不足',null]) {
  window.HospitalFeedback.result(text,()=>refresh++); finish(); assert.equal(refresh,1);
}
window.HospitalFeedback.result('<img src=x onerror=alert(1)>');
assert(!calls.at(-1).text.includes('<img'));
window.HospitalFeedback.error({responseJSON:{message:'服务失败'}});
assert.equal(calls.at(-1).text,'服务失败'); assert.equal(attrs.role,'alert');
window.HospitalFeedback.error({}); assert(calls.at(-1).text.includes('请求未完成'));
classes.add('patient-portal'); window.HospitalFeedback.result('已生成');
assert.equal(calls.at(-1).options.skin,'patient-dialog patient-feedback');
assert(calls.at(-1).text.includes('is-success'));
window.HospitalFeedback.result('更新失败');assert(calls.at(-1).text.includes('is-error'));assert.equal(calls.at(-1).options.closeBtn,1);
window.HospitalFeedback.result('已生成');
assert.equal(calls.at(-1).options.icon,-1); assert.equal(attrs.role,'status');
function walk(dir) { return fs.readdirSync(dir,{withFileTypes:true}).flatMap(e=>e.isDirectory()?walk(path.join(dir,e.name)):[path.join(dir,e.name)]); }
for(const file of walk(path.join(root,'src/main/resources/templates'))) {
 const source=fs.readFileSync(file,'utf8');
 assert(!/(^|[^.\w])(?:alert|confirm|prompt)\s*\(/m.test(source),file+' uses native dialog');
 if(source.includes('HospitalFeedback.')) assert(source.includes('/static/js/hospital-feedback.js'),file+' missing helper');
}
console.log('Feedback assertions passed: deferred success, failures, escaping, fallback, roles, all template native-dialog guard.');

let dispatched=0, xhrOptions; const control={disabled:false};
window.document.querySelectorAll=()=>[control]; window.setTimeout=fn=>fn();
window.$={ajax:options=>{dispatched++;xhrOptions=options;return 'xhr';}};
assert.equal(window.HospitalFeedback.request({url:'/write',type:'POST'}),'xhr');
assert.equal(control.disabled,true);assert.equal(window.HospitalFeedback.request({url:'/write',type:'POST'}),false);assert.equal(dispatched,1);
xhrOptions.complete();assert.equal(control.disabled,false);
window.HospitalFeedback.request({url:'/write',type:'POST'});assert.equal(dispatched,2);xhrOptions.success({message:'添加成功'});xhrOptions.complete();assert.equal(control.disabled,false);
window.$.ajax=()=>{throw new Error('network setup');};assert.throws(()=>window.HospitalFeedback.request({url:'/write',type:'POST'}));assert.equal(control.disabled,false);
console.log('Pending guard passed: duplicate dispatch blocked, failure/success/throw release, existing disabled state preserved.');
