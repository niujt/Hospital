<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>预约挂号页面</title>
    <link rel="stylesheet" href="/static/css/layui.min.css">
    <link rel="stylesheet" href="/static/css/normalize.css" type="text/css"/>
    <link rel="stylesheet" href="/static/css/secondary.css" type="text/css"/>
    <script language="javascript" type="text/javascript" src="/static/js/KunyiHTML5.min.js"></script>
    <script language="javascript" type="text/javascript" src="/static/js/Kunyilibrary.js"></script>
</head>
<body style="background-image: url('/static/images/background.jpg') no-repeat">
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
        <form class="layui-form" onsubmit="return appointment()">
            <table class="layui-table" lay-even lay-skin="nob" style="width: 60%;margin: 0 auto">
                <tr>
                    <td style="white-space:nowrap">科室：</td>
                    <td colspan="2">
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select id="department" lay-filter="department">
                                    <option value="">请选择科室</option>
                                    <option value="精神科">精神科</option>
                                    <option value="妇产科">妇产科</option>
                                    <option value="口腔科">口腔科</option>
                                    <option value="骨科">骨科</option>
                                    <option value="外科">外科</option>
                                    <option value="内科">内科</option>
                                </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="white-space:nowrap">预约医生：</td>
                    <td colspan="2">
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="doctorid" id="doctorid">
                                </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="white-space:nowrap">预约时间：</td>
                    <td colspan="2">
                        <input type="date" name="time" required lay-verify="required" autocomplete="off"
                               class="layui-input">
                        <input type="hidden" name="patientid" va required lay-verify="required" value="${patientid}"
                               autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td style="white-space:nowrap">门诊收费(元)：</td>
                    <td colspan="2">
                        <input type="text" name="expenses" required lay-verify="required" autocomplete="off"
                               class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>操作：</td>
                    <td>
                        <button type="submit" class="layui-btn layui-btn-normal">预约</button>
                    </td>
                    <td>
                        <button type="button" class="layui-btn layui-btn-normal" onclick="downloadpdf()">生成预约挂号单</button>
                    </td>
                </tr>
            </table>
        </form>
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
    function downloadpdf(){
        $.ajax({
            url: "/patient/downloadpdf",
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
    function appointment() {
        var d = {};
        var t = $('form').serializeArray();
        $.each(t, function () {
            d[this.name] = this.value;
        });
        var json = JSON.stringify(d);
        $.ajax({
            url: "/patient/appointment",
            type: 'post',
            data: json,
            contentType: "application/json;charset=utf-8",
            success: function (data) {
                console.log(data.message);
                alert(data.message);
                // if(data.message=="已存在"){
                //     $("#message").text(data.message);
                // }
                // else {
                parent.location.reload();
                // }
            },
            error: function (data) {
                layer.msg(data.message, {
                    time: 2000, //2s后自动关闭
                });
            }
        });
        return false
    }

    layui.use(['layer', 'form'], function () {
        var layer = layui.layer
            , form = layui.form;
        form.on('select(department)', function (data) {
            var department = (data.value);
            $.ajax({
                type: 'GET',
                url: '/doctor/' + department,
                dataType: 'json',
                success: function (data) {
                    $("#doctorid").empty();
                    data = data.doctors;
                    data.forEach(function (item) {
                        var name = item.name;
                        var expert = item.expert;
                        if(expert==0){
                            expert='非专家';
                        }
                        else {
                            expert='专家';
                        }
                        console.log(name);
                        var id = item.id;
                        console.log(id);
                        $("#doctorid").append("<option value=" + id + ">" + name +"----"+expert+"</option>");
                    });
                    form.render('select');
                }
            });
        });

    });
</script>
</body>
</html>