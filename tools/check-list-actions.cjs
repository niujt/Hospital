/* Structural UI contracts only; no browser use and no runtime resource copying. */
const fs=require('node:fs'),assert=require('node:assert/strict'),path=require('node:path');
const dir='src/main/resources/templates/admin';
const files=fs.readdirSync(dir).filter(n=>n.endsWith('Manage.ftl'));
assert.equal(files.length,9);
for(const file of files){
 const s=fs.readFileSync(path.join(dir,file),'utf8');
 const table=s.indexOf('<table class="layui-table">');
 const toolbar=s.indexOf('列表操作">');
 assert.ok(toolbar>=0&&toolbar<table,file+' toolbar is outside/above records table');
 assert.equal((s.match(/onclick="showAdd\(/g)||[]).length,1,file+' unique add action');
 assert.ok(s.indexOf('onclick="showAdd(')<table,file+' add outside header');
 assert.doesNotMatch(s.match(/<thead>[\s\S]*?<\/thead>/)[0],/<button/,file+' header only describes columns');
 assert.match(s,/<th width="180" scope="col">操作<\/th>/);
 assert.match(s,/<button type="button" class="layui-btn layui-btn-primary layui-btn-sm"[^>]+onclick="edit\([^>]+>.*? 编辑<\/button>/);
 assert.match(s,/<button type="button" class="layui-btn layui-btn-danger layui-btn-sm"[^>]+onclick="del\([^>]+>.*? 删除<\/button>/);
 assert.match(s,/role="group" aria-label="记录操作"/);
 if(file==='hospitalizationManage.ftl'){
  assert.ok(s.indexOf("location.href='/admin/getHospitalization'")<table);
  assert.equal((s.match(/getHospitalization/g)||[]).length,1);
 }
}
const d=fs.readFileSync('src/main/resources/templates/doctor/seekMedicalAdvice.ftl','utf8');
assert.match(d,/<th colspan="2" width="320" scope="col">操作/);
assert.match(d,/onclick="seek\(\$\{a.patientid\}, \$\{a.id\}\)"/);
assert.match(d,/onclick="check\(\$\{a.patientid\}\)"/);
assert.match(d,/onclick="printseek\(\$\{a.patientid\}\)"/);
const group=d.match(/aria-label="就诊操作">([\s\S]*?)<\/div>/)[1];
assert.equal((group.match(/<button/g)||[]).length,3);
assert.doesNotMatch(group,/style=|width:/);
assert.equal((group.match(/layui-btn-sm/g)||[]).length,3);
assert.equal((group.match(/layui-btn-primary/g)||[]).length,2);
console.log('List action contracts passed: 9 independent native toolbars; 18 compact semantic row actions; export preserved; 3 doctor actions retain exact handlers without fixed widths.');
