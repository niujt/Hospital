<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>预约挂号 · 就医服务</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
    <link rel="stylesheet" href="/static/css/patient-portal.css?v=20261031-1">
</head>
<body class="patient-portal">
<#include "header.ftl"/>
<main class="layui-container">
<hr>
    <!-- 内容主体区域 -->
    <div class="layui-card">
        <div class="layui-card-header">预约挂号</div>
        <div class="layui-card-body">
        <form class="layui-form" onsubmit="return appointment()">
            <table class="layui-table" lay-even lay-skin="nob">
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
    </div>
</main>
<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<link rel="stylesheet" href="/static/css/hospital-feedback.css?v=20261031-3">
<script src="/static/js/hospital-feedback.js?v=20261031-1"></script>
<script src="/static/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
    function downloadpdf(){
        HospitalFeedback.request({
            url: "/patient/downloadpdf",
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
    function appointment() {
        var d = {};
        var t = $('form').serializeArray();
        $.each(t, function () {
            d[this.name] = this.value;
        });
        var json = JSON.stringify(d);
        HospitalFeedback.request({
            url: "/patient/appointment",
            type: 'post',
            data: json,
            contentType: "application/json;charset=utf-8",
            success: function (data) {
                console.log(data.message);
                HospitalFeedback.result(data.message, function () { parent.location.reload(); });

            },
            error: function (data) {
                HospitalFeedback.error(data);
            }
        });
        return false
    }

    layui.use(['layer', 'form', 'element'], function () {
        layui.element.render('nav');
        layui.form.render();
        var layer = layui.layer
            , form = layui.form;
        form.on('select(department)', function (data) {
            var department = (data.value);
            HospitalFeedback.request({
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
