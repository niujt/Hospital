<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>查看病史</title>
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
        <#list medicalhistorys as medicalhistory>
            <table class="layui-table" lay-even lay-skin="nob" style="width: 60%;margin: 0 auto">
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
</body>
</html>
