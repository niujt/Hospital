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
    <style>
        .item{
          float: left;
        }
        .item li{
            margin-right: 40px;
            text-align: left;
            width: 200px;
            padding-right: 100px;
            /*white-space: nowrap;*/
            /*overflow: hidden;*/
            word-wrap: break-word;
            word-break: break-all;
            height:auto;
            /*text-overflow: ellipsis;*/
            border: 1px dotted #000;
            border-radius: 5px;
            box-shadow: inset 1px 1px #ccc;
            background: #ccc;
            font-size: 20px;
            color: black;
            font-family: fantasy;
            margin-top: 10px;
        }
        /*#list {*/
        /*display: block;*/
        /*overflow: hidden;*/
        /*white-space: nowrap;*/
        /*}*/

        /*#list li {*/
        /*float: left;*/
        /*list-style: none;*/
        /*display: inline-block;*/
        /*margin-left: 15px;*/
        /*width: 130px;*/
        /*}*/
    </style>
</head>
<body>
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

        <form class="layui-form" action="/patient/searchinfo" onsubmit="return search()" method="get">
            <table>
                <tr>
                    <td>查询类型：</td>
                    <td>
                        <select id="type" name="type">
                            <option value="">--请选择查询类型--</option>
                            <option value="illness">疾病</option>
                            <option value="doctor">医生</option>
                            <option value="drugs">药品</option>
                        </select>
                    </td>
                    <td><input type="text" id="name" name="name" autocomplete="off" placeholder="请输入你要查询的内容"
                               class="layui-input"></td>
                    <td>
                        <button style="width: 60px;margin-top: 0px" type="submit" class="btn layui-btn"><i
                                    class="layui-icon">&#xe615;</i></button>
                    </td>
                </tr>
            </table>
        </form>
        <ul id="list">

        </ul>
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
<script src="/static/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
    function search() {
        var name = $("#name").val();
        var type = $("#type").val();
        $.ajax({
            url: "/patient/searchinfo?name=" + name + "&type=" + type,
            type: 'get',
            contentType: "application/json;charset=utf-8",
            success: function (data) {
                var json = data.map;
                var type, list;
                var str = '';
                for (var key in json) {
                    type = key;
                    list = json[key];
                    if (type == 'doctor') {
                        $("#list").empty();
                        $.each(list, function (n, value) {
                            if (value.expert == '1') {
                                value.expert = '专业';
                            } else {
                                value.expert = '非专业';
                            }
                            if (value.text == null) {
                                value.text = "无";
                            }
                            if (value.sex == 0) {
                                value.sex = "男";
                            } else {
                                value.sex = "女";
                            }
                            str = "<li>姓名：" + value.name + "</li>" +
                                "<li>性别：" + value.sex + "</li>" +
                                "<li>家庭住址：" + value.address + "</li>" +
                                "<li>年龄：" + value.age + "(周岁)</li>" +
                                "<li>身份证号码：" + value.certId + "</li>" +
                                "<li>科室" + value.department + "</li>" +
                                "<li>" + value.expert + "</li>" +
                                "<li>简介: " + value.text + "</li>";
                            $("#list").append("<li><ul class='item'>" + str + "</ul></li>");
                        });

                    } else if (type == 'illness') {
                        $("#list").empty();
                        $.each(list, function (n, value) {
                            str = "疾病名称：<li>" + value.name + "</li><li>简介: " + value.text + "</li>";
                            $("#list").append("<li><ul class='item'>" + str + "</ul></li>")
                        });

                    } else {
                        $("#list").empty();
                        $.each(list, function (n, value) {
                            if (value.text == null) {
                                value.text = "无";
                            }
                            if (value.type == 0) {
                                value.type = "颗粒剂";
                            } else if (value.type == 1) {
                                value.type = "丸剂";
                            } else if (value.type == 2) {
                                value.type = "散剂";
                            } else if (value.type == 3) {
                                value.type = "酊剂";
                            } else if (value.type == 4) {
                                value.type = "片剂";
                            } else {
                                value.type = "胶囊剂";
                            }
                            str = "<li>药品名称：" + value.name + "</li><li>数量：" + value.number + "</li>" +
                                "<li>市场价：" + value.price + " (元)</li><li>简介: " + value.text + "</li>" +
                                "<li>药品类型" + value.type + "</li>";
                            $("#list").append("<li><ul class='item'>" + str + "</ul></li>");
                        });

                    }
                }

            },
            error: function (data) {
                layer.msg(data.map, {
                    time: 2000 //2s后自动关闭
                });
            }
        });
        return false;
    }
</script>
</body>
</html>
