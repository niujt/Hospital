<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>住院信息</title>
    <link rel="stylesheet" href="/static/css/layui.min.css">
    <link rel="stylesheet" href="/static/css/normalize.css" type="text/css"/>
    <link rel="stylesheet" href="/static/css/secondary.css" type="text/css"/>
    <script language="javascript" type="text/javascript" src="/static/js/KunyiHTML5.min.js"></script>
    <script language="javascript" type="text/javascript" src="/static/js/Kunyilibrary.js"></script>
</head>
<body >
<header>
    <a class="logo" href="/hospital"><img src="/static/images/logo.png" width="350" height="51"/></a>
</header>
<div class="layui-layout layui-layout-admin">
    <#--<#include "../menu.ftl"/>-->
    <div>
        <table style="margin: 0 auto;">
            <tr>
                <td><a href="/patient/appointment">挂号</a>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td><a href="/patient/search">查询</a>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td><a href="/patient/medicalhistory">病史</a>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td><a href="/patient/hospitalization">住院</a>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td><a href="/loginout">退出登录</a></td>
            </tr>
        </table>
    </div>
    <!-- 内容主体区域 -->
    <div style="padding: 15px;">
        <table class="layui-table" lay-even lay-skin="nob" style="width: 60%;margin: 0 auto">
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
    <table id="table" class="layui-table" lay-even lay-skin="nob" style="width: 60%;margin: 0 auto;display: none">
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
    <div style="height: 400px;"></div>
</div>
<footer>
    <div class="footBase">
        <h3>咨询电话：门诊导医电话 ：1111111 （上午8：00至12:00）（下午14:00至17:00）
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span>医院办公室电话：111111（全天）</span></h3>
        <p>主办单位：人民医院 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;东区地址：省市城关解放路64号 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;西区地址：省市商都大道2996号<br/>版权所有：省人民医院
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;备案号：000000 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </p>
        <div class="QRcode"><img src="/static/images/QRcode.jpg" width="104" height="104"><br/>手机扫一扫关注人民医院官方微信</div>
    </div>
</footer>
<script src="/static/js/layui.all.js"></script>
<script src="/static/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
    function showHistroy() {
        $("#table").show();
    }
</script>
</body>
</html>