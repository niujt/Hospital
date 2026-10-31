<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>就医服务与信息查询 · 就医服务</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">

    <link rel="stylesheet" href="/static/css/patient-portal.css?v=20261031-1">
</head>
<body class="patient-portal">
<#include "header.ftl"/>
<main class="layui-container">
<hr>
<blockquote class="layui-elem-quote">您好，欢迎使用就医服务。您可以预约医生，也可以查看自己的就诊记录。</blockquote>
<div class="layui-row layui-col-space15">
    <div class="layui-col-xs12 layui-col-sm4"><div class="layui-card"><div class="layui-card-header">预约挂号</div><div class="layui-card-body"><p>选择科室、医生与就诊日期。</p><hr><a class="layui-btn layui-btn-normal" href="/patient/appointment">去预约</a></div></div></div>
    <div class="layui-col-xs12 layui-col-sm4"><div class="layui-card"><div class="layui-card-header">我的病史</div><div class="layui-card-body"><p>查看已有病史与就诊医生。</p><hr><a class="layui-btn layui-btn-primary" href="/patient/medicalhistory">查看记录</a></div></div></div>
    <div class="layui-col-xs12 layui-col-sm4"><div class="layui-card"><div class="layui-card-header">住院信息</div><div class="layui-card-body"><p>查看当前及历史住院记录。</p><hr><a class="layui-btn layui-btn-primary" href="/patient/hospitalization">查看住院信息</a></div></div></div>
</div>
<hr>
    <!-- 内容主体区域 -->
    <div class="layui-card">
        <div class="layui-card-header">就医服务与信息查询</div>
        <div class="layui-card-body">

        <form class="layui-form" action="/patient/searchinfo" onsubmit="return search()" method="get">
            <div class="layui-row layui-col-space15">
                <div class="layui-col-xs12 layui-col-sm4">
                    <div class="layui-form-item">
                        <label class="layui-form-label" for="type">查询类型</label>
                        <div class="layui-input-block">
                            <select id="type" name="type">
                                <option value="">请选择查询类型</option>
                                <option value="illness">疾病</option>
                                <option value="doctor">医生</option>
                                <option value="drugs">药品</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="layui-col-xs12 layui-col-sm6">
                    <div class="layui-form-item">
                        <label class="layui-form-label" for="name">查询内容</label>
                        <div class="layui-input-block">
                            <input type="text" id="name" name="name" autocomplete="off"
                                   placeholder="请输入你要查询的内容" class="layui-input">
                        </div>
                    </div>
                </div>
                <div class="layui-col-xs12 layui-col-sm2">
                    <button type="submit" class="layui-btn layui-btn-normal layui-btn-fluid">查询</button>
                </div>
            </div>
        </form>
        <ul id="list" class="layui-row layui-col-space15">

        </ul>
    </div>
    </div>
</main>
<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<script src="/static/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
    function escapeSearch(value) {
        return String(value == null ? '' : value).replace(/[&<>"']/g, function(c) {
            return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];
        });
    }
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
                            str = "<li>姓名：" + escapeSearch(value.name) + "</li>" +
                                "<li>科室" + escapeSearch(value.department) + "</li>" +
                                "<li>" + escapeSearch(value.expert) + "</li>" +
                                "<li>简介: " + escapeSearch(value.text) + "</li>";
                            $("#list").append("<li class='layui-col-xs12 layui-col-md6'><div class='layui-card'><div class='layui-card-body'><ul>" + str + "</ul></div></div></li>");
                        });

                    } else if (type == 'illness') {
                        $("#list").empty();
                        $.each(list, function (n, value) {
                            str = "疾病名称：<li>" + escapeSearch(value.name) + "</li><li>简介: " + escapeSearch(value.text) + "</li>";
                            $("#list").append("<li class='layui-col-xs12 layui-col-md6'><div class='layui-card'><div class='layui-card-body'><ul>" + str + "</ul></div></div></li>")
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
                            str = "<li>药品名称：" + escapeSearch(value.name) + "</li><li>数量：" + escapeSearch(value.number) + "</li>" +
                                "<li>市场价：" + escapeSearch(value.price) + " (元)</li><li>简介: " + escapeSearch(value.text) + "</li>" +
                                "<li>药品类型" + escapeSearch(value.type) + "</li>";
                            $("#list").append("<li class='layui-col-xs12 layui-col-md6'><div class='layui-card'><div class='layui-card-body'><ul>" + str + "</ul></div></div></li>");
                        });

                    }
                }

            },
            error: function (data) {
                layui.layer.msg(data.map, {
                    time: 2000 //2s后自动关闭
                });
            }
        });
        return false;
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
