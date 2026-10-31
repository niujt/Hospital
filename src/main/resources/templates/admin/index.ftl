<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>管理工作台 · 医疗系统</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <#include "../menu.ftl"/>
    <main class="layui-body layui-bg-gray">
        <div class="layui-fluid">
            <div class="layui-btn-container"><button id="workspace-refresh" type="button" class="layui-btn layui-btn-primary layui-btn-sm">刷新当前标签</button><span class="layui-font-gray">模块切换保留当前页面；关闭未保存内容前将提示确认。</span></div>
            <div class="layui-tabs layui-tabs-card" id="admin-workspace">
                <ul class="layui-tabs-header"><li lay-id="overview" lay-closable="false">工作台概览</li></ul>
                <div class="layui-tabs-body"><div class="layui-tabs-item">
        <div class="layui-fluid">
            <fieldset class="layui-elem-field layui-field-title">
                <legend>管理工作台</legend>
            </fieldset>
            <blockquote class="layui-elem-quote">欢迎进入医疗系统。请从左侧导航或下方入口选择工作模块。</blockquote>
            <div class="layui-row layui-col-space15">
            <div class="layui-col-xs12 layui-col-sm6 layui-col-md4">
                <div class="layui-card">
                    <div class="layui-card-header">医生管理</div>
                    <div class="layui-card-body">
                        <p>维护医生资料、科室与专家信息。</p>
                        <hr>
                        <a class="layui-btn layui-btn-sm" href="/admin/doctorManage">进入医生管理</a>
                    </div>
                </div>
            </div>
            <div class="layui-col-xs12 layui-col-sm6 layui-col-md4">
                <div class="layui-card">
                    <div class="layui-card-header">患者管理</div>
                    <div class="layui-card-body">
                        <p>查看患者档案，维护基础信息。</p>
                        <hr>
                        <a class="layui-btn layui-btn-sm" href="/admin/patientManage">进入患者管理</a>
                    </div>
                </div>
            </div>
            <div class="layui-col-xs12 layui-col-sm6 layui-col-md4">
                <div class="layui-card">
                    <div class="layui-card-header">预约管理</div>
                    <div class="layui-card-body">
                        <p>集中查看与管理预约记录。</p>
                        <hr>
                        <a class="layui-btn layui-btn-sm" href="/admin/appointmentManage">进入预约管理</a>
                    </div>
                </div>
            </div>
            <div class="layui-col-xs12 layui-col-sm6 layui-col-md4">
                <div class="layui-card">
                    <div class="layui-card-header">药品管理</div>
                    <div class="layui-card-body">
                        <p>维护药品资料与库存信息。</p>
                        <hr>
                        <a class="layui-btn layui-btn-sm" href="/admin/drugsManage">进入药品管理</a>
                    </div>
                </div>
            </div>
            <div class="layui-col-xs12 layui-col-sm6 layui-col-md4">
                <div class="layui-card">
                    <div class="layui-card-header">病史管理</div>
                    <div class="layui-card-body">
                        <p>查阅历史就诊与诊疗记录。</p>
                        <hr>
                        <a class="layui-btn layui-btn-sm" href="/admin/medicalhistoryManage">进入病史管理</a>
                    </div>
                </div>
            </div>
            <div class="layui-col-xs12 layui-col-sm6 layui-col-md4">
                <div class="layui-card">
                    <div class="layui-card-header">住院信息</div>
                    <div class="layui-card-body">
                        <p>查看与维护患者住院记录。</p>
                        <hr>
                        <a class="layui-btn layui-btn-sm" href="/admin/hospitalizationManage">进入住院信息</a>
                    </div>
                </div>
            </div>
            </div>
            <fieldset class="layui-elem-field">
                <legend>工作提示</legend>
                <div class="layui-field-box">修改或删除记录前，请核对患者与业务信息，确认无误后再提交。</div>
            </fieldset>
        </div>
                </div></div>
            </div>
        </div>
    </main>
</div>
<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<script>
    layui.use('element', function () {
        layui.element.init();
    });
</script>
<script src="/static/js/hospital-feedback.js?v=20261031-1"></script>
<script src="/static/js/hospital-drafts.js?v=1"></script>
<script src="/static/js/hospital-frame.js?v=1"></script>
<script src="/static/js/admin-workspace.js?v=2"></script>
</body>
</html>
