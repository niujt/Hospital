<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>医生工作台 · 医疗系统</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <#include "../menu.ftl"/>
    <main class="layui-body layui-bg-gray">
        <div class="layui-fluid">
            <fieldset class="layui-elem-field layui-field-title">
                <legend>医生工作台</legend>
            </fieldset>
            <blockquote class="layui-elem-quote">医生工作台 · 先定位预约患者，再进入诊疗与病史工作。</blockquote>
            <div class="layui-row layui-col-space15">
            <div class="layui-col-xs12 layui-col-md8">
                <div class="layui-card">
                    <div class="layui-card-header">接诊工作区</div>
                    <div class="layui-card-body">
                        <p>按患者姓名或预约时间查看预约记录；核对患者后，完成诊疗、查阅病史或打印就诊单。</p>
                        <hr>
                        <a class="layui-btn layui-btn-sm" href="/doctor/seekMedicalAdvice">进入接诊工作区</a>
                    </div>
                </div>
            </div>
            </div>
            <fieldset class="layui-elem-field">
                <legend>工作提示</legend>
                <div class="layui-field-box">接诊顺序：筛选预约记录 → 核对患者 → 查看病史 → 进入就医。诊断、开药与入院操作均在患者对应的诊疗窗口中完成。</div>
            </fieldset>
        </div>
    </main>
</div>
<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<script>
    layui.use('element', function () {
        layui.element.init();
    });
</script>
</body>
</html>
