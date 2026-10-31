<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>接诊工作区 · 医生工作台</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
<#include "../menu.ftl"/>
<main class="layui-body layui-bg-gray"><div class="layui-fluid">
<fieldset class="layui-elem-field layui-field-title"><legend>接诊工作区</legend></fieldset>
<blockquote class="layui-elem-quote">按患者或预约时间筛选记录，进入诊疗、查阅病史或打印就诊单。</blockquote>
    <div class="layui-card">
        <div class="layui-card-header">接诊工作区</div>
        <div class="layui-card-body">
        <form action="/doctor/seekMedicalAdvice" method="get">
            <table>
                <tr>
                    <td>患者:</td>
                    <td><input name="patientname" autocomplete="off" class="layui-input lay-size=sm"></td>
                    <td>&nbsp;&nbsp;预约时间:</td>
                    <td><input name="time" type="date" autocomplete="off" class="layui-input lay-size=sm"></td>
                    <td>
                        <button class="layui-btn" aria-label="查询患者" type="submit"><i class="layui-icon">&#xe615;</i></button>
                    </td>
                    <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <button class="layui-btn" style="width: 120px;margin: 0 auto;" type="button" onclick="location.href='/loginout'">退出登录
                        </button>
                    </td>
                </tr>
            </table>
        </form>
        <table class="layui-table">
            <thead>
            <tr>
                <th>序号</th>
                <th>患者名称</th>
                <th>预约时间</th>
                <th colspan="2" width="320" scope="col">操作</th>
            </tr>
            </thead>
            <tbody>
            <#list appointments as a>
                <tr>
                    <td>${a_index+1}</td>
                    <td>${a.patientname!''}</td>
                    <td>${(a.time?string("yyyy-MM-dd"))!}</td>
                    <td colspan="2">
                        <div class="layui-btn-container" role="group" aria-label="就诊操作">
                            <button type="button" class="layui-btn layui-btn-sm" onclick="seek(${a.patientid}, ${a.id})"><i class="layui-icon" aria-hidden="true">&#xe642;</i> 就医</button>
                            <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" onclick="check(${a.patientid})"><i class="layui-icon layui-icon-read" aria-hidden="true"></i> 查看病史</button>
                            <button class="layui-btn layui-btn-primary layui-btn-sm" type="button" onclick="printseek(${a.patientid})"><i class="layui-icon" aria-hidden="true">&#xe66d;</i> 打印就诊单</button>
                        </div>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
    </div>
</div></main>
</div>
<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<link rel="stylesheet" href="/static/css/hospital-feedback.css?v=20261031-1">
<script src="/static/js/hospital-feedback.js?v=20261031-1"></script>
<script src="/static/js/jquery-3.3.1.min.js"></script>
<script src="/static/js/hospital-modal.js?v=20261031-2"></script>
<script type="text/javascript">
    function seek(id, appointmentId) {
        HospitalModal.open({url: '/doctor/seek/' + id + '?appointmentId=' + encodeURIComponent(appointmentId), title: '就医'});
    }
    function check(id) {
        HospitalModal.open({url: '/doctor/medicalhistory/' + id, title: '查看病史', editable: false});
    }
    function printseek(id){
        HospitalFeedback.request({
            url: "/doctor/printseek/"+id,
            type: 'post',
            contentType: "application/json;charset=utf-8",
            success: function (data) {
                console.log(data.message);
                HospitalFeedback.result(data.message);
            },
            error: function (data) {
                HospitalFeedback.error(data);
            }
        });
        return false
    }
</script>
<script>
    layui.use(['element', 'form'], function () {
        layui.element.render('nav');
        layui.form.render();
    });
</script>
</body>
</html>