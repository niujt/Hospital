<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>就医</title>
    <link rel="stylesheet" href="/static/css/layui.min.css">
    <link rel="stylesheet" href="/static/css/normalize.css" type="text/css"/>
    <link rel="stylesheet" href="/static/css/secondary.css" type="text/css"/>
    <script language="javascript" type="text/javascript" src="/static/js/KunyiHTML5.min.js"></script>
    <script language="javascript" type="text/javascript" src="/static/js/Kunyilibrary.js"></script>
</head>
<body>
<header>
    <a class="logo" href="/hospital"><img src="/static/images/logo.png" width="350" height="51"/></a>
</header>
<div class="layui-layout layui-layout-admin">

    <#--<#include "../menu.ftl"/>-->
    <!-- 内容主体区域 -->
    <div style="padding: 15px;">
        <form action="/doctor/seekMedicalAdvice" method="get">
            <table>
                <tr>
                    <td>患者:</td>
                    <td><input name="patientname" autocomplete="off" class="layui-input lay-size=sm"></td>
                    <td>&nbsp;&nbsp;预约时间:</td>
                    <td><input name="time" type="date" autocomplete="off" class="layui-input lay-size=sm"></td>
                    <td>
                        <button class="layui-btn"style="width: 60px;margin: 0 auto;" type="submit"><i class="layui-icon">&#xe615;</i></button>
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
                <th colspan="2">操作</th>
            </tr>
            </thead>
            <tbody>
            <#list appointments as a>
                <tr>
                    <td>${a_index+1}</td>
                    <td>${a.patientname!''}</td>
                    <td>${(a.time?string("yyyy-MM-dd"))!}</td>
                    <td colspan="2">
                        <button style="width: 120px;" class="layui-btn" onclick="seek(${a.patientid})">就医</button>
                        <button style="width: 120px;" class="layui-btn" onclick="check(${a.patientid})">查看病史</button>
                        <button style="width: 120px;" class="layui-btn" type="button" onclick="printseek(${a.patientid})">打印就诊单</button>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
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
<script type="text/javascript">
    function seek(id) {
        layer.open({
            type: 2,
            title: '就医',
            closeBtn: 0,
            area: ['80%', '60%'],
            content: ['/doctor/seek/' + id, 'yes'],
            btn: ['取消'],
            yes: function (index) {
                //事件
                layer.close(index);
            }
        });
    }

    function check(id) {
        layer.open({
            type: 2,
            title: '查看病史',
            closeBtn: 0,
            area: ['80%', '60%'],
            content: ['/doctor/medicalhistory/' + id, 'yes'],
            btn: ['取消'],
            yes: function (index) {
                //事件
                layer.close(index);
            }
        });
    }
    function printseek(id){
        $.ajax({
            url: "/doctor/printseek/"+id,
            type: 'post',
            contentType: "application/json;charset=utf-8",
            success: function (data) {
                console.log(data.message);
                alert(data.message);
            },
            error: function (data) {
                layer.msg(data.message, {
                    time: 2000, //2s后自动关闭
                });
            }
        });
        return false
    }
</script>
</body>
</html>