<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>我的病史 · 就医服务</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
    <link rel="stylesheet" href="/static/css/patient-portal.css?v=20261031-1">
</head>
<body class="patient-portal">
<#include "header.ftl"/>
<main class="layui-container">
<hr>
    <!-- 内容主体区域 -->
    <div class="layui-card">
        <div class="layui-card-header">我的病史</div>
        <div class="layui-card-body">
        <#list medicalhistorys as medicalhistory>
            <table class="layui-table" lay-even lay-skin="nob">
                <tr>
                    <td>既往病史：</td>
                    <td>
                        ${medicalhistory.name}
                    </td>
                </tr>
                <tr>
                    <td>患病时间：</td>
                    <td>
                        ${(medicalhistory.time?string('yyyy-MM-dd'))!}
                    </td>
                </tr>
                <tr>
                    <td>就诊医生：</td>
                    <td>
                        ${medicalhistory.doctorname}
                    </td>
                </tr>
            </table>
            <hr>
        </#list>
    </div>
    </div>
</main>
<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<script>
    layui.use(['element', 'form'], function () {
        layui.element.render('nav');
        layui.form.render();
    });
</script>
</body>
</html>
