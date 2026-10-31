<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>我的住院信息 · 就医服务</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
    <link rel="stylesheet" href="/static/css/patient-portal.css?v=20261031-1">
</head>
<body class="patient-portal">
<#include "header.ftl"/>
<main class="layui-container">
<hr>
    <!-- 内容主体区域 -->
    <div class="layui-card">
        <div class="layui-card-header">我的住院信息</div>
        <div class="layui-card-body">
        <table class="layui-table" lay-even lay-skin="nob">
            <tr>
                <td style="width: 100px">楼层：</td>
                <td>${theLast.floor}</td>
            </tr>
            <tr>
                <td>门牌号：</td>
                <td>
                    ${theLast.door}
                </td>
            </tr>
            <tr>
                <td>床号：</td>
                <td>
                    ${theLast.bed}
                </td>
            </tr>
            <tr>
                <td>患病：</td>
                <td>
                    ${theLast.medicalname}
                </td>
            </tr>
            <tr>
                <td>住院时间：</td>
                <td>
                    ${(theLast.intime?string('yyyy-MM-dd'))!}
                </td>
            </tr>
            <tr>
                <td>出院时间：</td>
                <td>
                    ${(theLast.outtime?string('yyyy-MM-dd'))!}
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <button class="layui-btn layui-btn-normal" onclick="showHistroy()">住院历史信息</button>
                </td>
            </tr>
        </table>
    </div>
    <hr>
    <hr>
    <table id="table" class="layui-table layui-hide" lay-even lay-skin="nob">
        <thead>
        <tr>
            <th>序号</th>
            <th>楼层号</th>
            <th>门牌号</th>
            <th>床号</th>
            <th>患者</th>
            <th>患病</th>
            <th>住院时间</th>
            <th>出院时间</th>
        </tr>
        </thead>
        <tbody>
        <#list others as h>
            <tr>
                <td>${h_index+1}</td>
                <td>${h.floor}</td>
                <td>${h.door}</td>
                <td>${h.bed}</td>
                <td>${h.patientname!''}</td>
                <td>${h.medicalname}</td>
                <td>${(h.intime?string("yyyy-MM-dd"))!}</td>
                <td>${(h.outtime?string("yyyy-MM-dd"))!}</td>
            </tr>
        </#list>
        </tbody>
    </table>
    </div>
</main>
<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<script src="/static/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
    function showHistroy() {
        $("#table").removeClass("layui-hide");
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
