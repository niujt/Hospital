<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>首页 · 医院服务平台</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
    <link rel="stylesheet" href="/static/css/hospital-public.css?v=20261031-1">
</head>
<body class="hospital-public">
<#include "public-header.ftl"/>
<main>
    <section class="public-hero">
        <div class="public-shell public-hero-grid">
            <div class="public-hero-copy">
                <div class="public-eyebrow">以就医需求为中心</div>
                <h1>把就医的每一步，<br>安排得更清楚。</h1>
                <p class="public-lead">预约医生、查阅记录、了解就医信息。<br>常用服务集中在这里，方便您按需选择。</p>
                <div class="public-actions"><a class="public-primary" href="/hospital/login">登录并使用服务 <span aria-hidden="true">→</span></a><a class="public-secondary" href="/hospital/guide">查看就医指南</a></div>
                <p class="public-hero-note">个人预约与记录需登录后查看</p>
            </div>
            <aside class="public-service-overview" aria-label="服务使用步骤">
                <div class="public-overview-heading"><span class="public-brand-icon" aria-hidden="true">＋</span><div><strong>您的就医服务入口</strong><p>从预约到记录，找到需要的服务</p></div></div>
                <ol class="public-steps">
                    <li><span>01</span><div><strong>登录个人账号</strong><p>进入与账号身份对应的服务页面。</p></div></li>
                    <li><span>02</span><div><strong>选择所需服务</strong><p>预约挂号，或查询医生与医疗信息。</p></div></li>
                    <li><span>03</span><div><strong>查阅就诊记录</strong><p>查看系统已有病史与住院信息。</p></div></li>
                </ol>
            </aside>
        </div>
    </section>
    <section class="public-shell public-services" aria-labelledby="services-title">
        <div class="public-section-heading"><div><span class="public-eyebrow">常用服务</span><h2 id="services-title">您想办理什么？</h2></div><p>登录后进入个人服务页面</p></div>
        <div class="public-service-grid">
            <a class="public-service-card" href="/hospital/login"><span class="public-service-symbol" aria-hidden="true">＋</span><h3>预约挂号</h3><p>选择科室、医生与预约日期。</p><span class="public-card-link">登录后预约 →</span></a>
            <a class="public-service-card" href="/hospital/login"><span class="public-service-symbol" aria-hidden="true">≡</span><h3>我的就诊记录</h3><p>查看已有病史与住院信息。</p><span class="public-card-link">登录后查看 →</span></a>
            <a class="public-service-card" href="/hospital/login"><span class="public-service-symbol" aria-hidden="true">⌕</span><h3>医疗信息查询</h3><p>查询医生、疾病与药品资料。</p><span class="public-card-link">登录后查询 →</span></a>
            <a class="public-service-card" href="/hospital/guide"><span class="public-service-symbol" aria-hidden="true">↗</span><h3>就医指南</h3><p>查看已有的就医指引与信息。</p><span class="public-card-link">查看指南 →</span></a>
        </div>
    </section>
    <section class="public-shell public-bottom-grid">
        <div class="public-information"><h2>了解医院与服务</h2><p>继续浏览现有公开信息栏目。</p><div class="public-info-links"><a href="/hospital/introduction">医院介绍 →</a><a href="/hospital/service">患者服务 →</a><a href="/hospital/expert">医生介绍 →</a><a href="/hospital/news">医院资讯 →</a></div></div>
        <div class="public-staff"><span class="public-eyebrow">医护与管理人员</span><h2>进入工作区</h2><p>使用已有工作账号登录，系统将进入对应的诊疗或管理页面。</p><a class="public-text-link" href="/hospital/login">工作账号登录 →</a></div>
    </section>
</main>
<footer class="public-footer"><div class="public-shell"><span>医院服务平台</span><span>预约挂号 · 就诊记录 · 医疗信息查询</span><a href="/hospital">返回首页</a></div></footer>
</body>
</html>
