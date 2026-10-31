/* Source/VM contract checks only. Does not control a browser or touch the running app. */
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const assert = require('node:assert/strict');
const root = path.resolve(__dirname, '..');
const js = name => fs.readFileSync(path.join(root, 'src/main/resources/static/js', name), 'utf8');
let active = 0, valid = true, layuiValid = true, submits = [], requests = [], states = [], timers = [];
const fields = [{type:'text'}, {type:'hidden'}, {type:'text',readOnly:true}, {type:'text',disabled:true}];
const forms = ['保存诊断','保存配药','保存入院'].map((label, i) => ({
    closest: () => ({classList:{contains:()=>active === i}}),
    getAttribute: () => label,
    reportValidity: () => valid,
    querySelectorAll: () => fields,
    requestSubmit: () => submits.push(i)
}));
const rows = [{hidden:false},{hidden:false},{hidden:false}];
let observed;
const original = options => { requests.push(options); if (options.url === '/throws') throw Error('transport failed'); return 'xhr'; };
const child = {
    document:{querySelectorAll:selector=>selector === 'form[data-modal-form]' ? forms
        : selector === '[data-modal-actions]' ? rows : [{}]},
    HospitalFeedback:{request:original},
    layui:{form:{validate:input=>{assert.equal(input.length,1);return layuiValid;}}},
    MutationObserver:class { constructor(callback){observed=callback;} observe(){} disconnect(){} },
    setTimeout:fn=>{timers.push(fn);return timers.length;}, clearTimeout:()=>{}
};
vm.runInNewContext(js('hospital-modal-form.js'), {window:child});
const bridge=child.HospitalModalForm.attach({update:s=>states.push(s)});
assert(rows.every(row=>row.hidden));
assert.equal(child.HospitalModalForm.attach({update:()=>{}}),null);
assert.equal(states.at(-1).label,'保存诊断');
valid=false;assert.equal(bridge.submit(),false);assert.equal(submits.length,0);
valid=true;layuiValid=false;assert.equal(bridge.submit(),false);assert.equal(submits.length,0);
layuiValid=true;assert.equal(bridge.submit(),true);assert.deepEqual(submits,[0]);
active=1;observed();assert.equal(states.at(-1).label,'保存配药');bridge.submit();assert.deepEqual(submits,[0,1]);
const get={url:'/lookup',type:'GET'};child.HospitalFeedback.request(get);assert.equal(requests.at(-1),get);
const payload={url:'/existing',type:'PUT',data:'unchanged',contentType:'application/json'};
child.HospitalFeedback.request(payload);assert.equal(states.at(-1).pending,true);
assert.equal(requests.at(-1).url,payload.url);assert.equal(requests.at(-1).data,payload.data);
assert.equal(child.HospitalFeedback.request(payload),false);assert.equal(bridge.submit(),false);
requests.at(-1).success({message:'更新失败'});requests.at(-1).complete();assert.equal(states.at(-1).pending,false);
child.HospitalFeedback.request(payload);requests.at(-1).success({message:'更新成功'});requests.at(-1).complete();
assert.equal(states.at(-1).pending,true);timers.shift()();assert.equal(states.at(-1).pending,false);
assert.throws(()=>child.HospitalFeedback.request({url:'/throws',type:'POST'}),/transport failed/);assert.equal(states.at(-1).pending,false);
bridge.dispose();assert(rows.every(row=>!row.hidden));assert.equal(child.HospitalFeedback.request,original);

// Parent contract: persistent native footer, no accidental close on save, read-only has no Save.
let options, closed=[], focused=0, lastFocus, clicks=0, listened={}, childKeys={}, attached=0, submitted=0, host;
function anchor(){return {tagName:'A',tabIndex:0,textContent:'',attrs:{},setAttribute(k,v){this.attrs[k]=v;},
 getAttribute(k){return this.attrs[k];},getClientRects(){return [{}];},focus(){focused++;lastFocus=this;},click(){clicks++;}};}
let anchors=[anchor(),anchor()];
const content={style:{}};
const frame={setAttribute(){},addEventListener(){},removeEventListener(){},contentWindow:{
 document:{addEventListener:(n,f)=>childKeys[n]=f,removeEventListener(){},querySelectorAll:()=>[]},
 HospitalModalForm:{attach:h=>{host=h;attached++;h.update({ready:true,pending:false,label:'保存'});return{submit:()=>submitted++,dispose(){}};}}
}};
const footer={querySelectorAll:()=>anchors};
const dialog={setAttribute(){},contains:()=>true,querySelector:s=>s==='iframe'?frame:s==='.layui-layer-content'?content:footer,
 querySelectorAll:()=>anchors};
const parent={innerWidth:900,innerHeight:700,document:{activeElement:{focus(){}},addEventListener:(n,f)=>listened[n]=f,
 removeEventListener(){},querySelectorAll:()=>[]},
 layui:{layer:{open:o=>{options=o;o.success([dialog],7);return 7;},close:i=>closed.push(i)}},
 HospitalFeedback:{error(){throw Error('unexpected bridge error');}}};
vm.runInNewContext(js('hospital-modal.js'),{window:parent});
parent.HospitalModal.open({url:'/existing/form',title:'编辑'});
assert.deepEqual(Array.from(options.btn),['保存','取消']);
assert.equal(content.style.overflow,'hidden');assert.equal(attached,1);
listened.keydown({key:'Tab',shiftKey:true,target:anchors[0],preventDefault(){}});
assert.equal(lastFocus,anchors[1]);
listened.keydown({key:'Tab',shiftKey:false,target:anchors[1],preventDefault(){}});
assert.equal(lastFocus,anchors[0]);
listened.keydown({key:'Enter',target:anchors[0],preventDefault(){}});
assert.equal(clicks,1);
assert.equal(options.yes(),false);assert.equal(submitted,1);assert.equal(closed.length,0);
host.update({ready:true,pending:true,label:'保存'});options.yes();assert.equal(submitted,1);
assert.equal(options.btn2(),false);assert.equal(closed.length,0);
listened.keydown({key:'Escape',preventDefault(){}});assert.equal(closed.length,0);
host.update({ready:true,pending:false,label:'保存'});listened.keydown({key:'Escape',preventDefault(){}});assert.deepEqual(closed,[7]);
options.end();
anchors=[anchor()];parent.HospitalModal.open({url:'/existing/readonly',editable:false});
assert.deepEqual(Array.from(options.btn),['关闭']);assert.equal(attached,1);
options.yes();assert.deepEqual(closed,[7,7]);options.end();

// All 18 administration forms use the same bridge; no unsupported direct form.submit().
let count=0;
for(const group of ['add','info']) {
 const dir=path.join(root,'src/main/resources/templates/admin',group);
 for(const name of fs.readdirSync(dir).filter(n=>n.endsWith('.ftl')&&n!=='foot.ftl')) {
  const text=fs.readFileSync(path.join(dir,name),'utf8');
  assert(text.includes('data-modal-form'),name);
  assert(text.includes('data-modal-actions'),name);count++;
 }
}
assert.equal(count,18);
assert(!/\bform\.submit\s*\(/.test(js('hospital-modal-form.js').replace(/\/\/[^\n]*/g,'')));
console.log('Modal contracts passed: 18 admin forms, active-tab bridge fixtures, validation, readonly exclusion, existing payloads, pending/error/success guards, footer close rules, Escape, standalone fallback.');
