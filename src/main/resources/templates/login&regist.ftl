<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>登录与注册 · 医院服务平台</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
    <link rel="stylesheet" href="/static/css/hospital-public.css?v=20261031-1">
</head>
<body class="hospital-public hospital-auth">
<#include "public-header.ftl"/>
<main class="public-shell auth-layout">
    <section class="auth-intro">
        <div class="public-eyebrow">欢迎使用医院服务平台</div>
        <h1>就医服务，<br>从这里开始。</h1>
        <p class="public-lead">登录后，预约医生、查看就诊记录，<br class="public-desktop-break">也让诊疗协作更加有序。</p>
        <ul class="auth-benefits">
            <li><span aria-hidden="true">01</span><div><strong>患者就医服务</strong><p>预约挂号、病史与住院信息查询。</p></div></li>
            <li><span aria-hidden="true">02</span><div><strong>医护工作入口</strong><p>使用工作账号进入对应的诊疗工作区。</p></div></li>
            <li><span aria-hidden="true">03</span><div><strong>统一账号登录</strong><p>系统按账号已有身份进入相应页面。</p></div></li>
        </ul>
        <a class="public-text-link" href="/hospital/guide">查看就医指南 <span aria-hidden="true">→</span></a>
    </section>
    <section class="auth-card" aria-label="账号登录与注册">
        <div class="auth-tabs" role="tablist" aria-label="选择登录或注册">
            <button type="button" id="signIn" role="tab" aria-selected="true" aria-controls="login-panel">账号登录</button>
            <button type="button" id="signUp" role="tab" aria-selected="false" aria-controls="register-panel">注册账号</button>
        </div>
        <div id="login-panel" role="tabpanel" aria-labelledby="signIn">
            <h2>欢迎回来</h2>
            <p class="auth-caption">请输入您的账号信息。</p>
            <form id="login" onsubmit="return login()">
                <label for="login-username">账号</label>
                <input id="login-username" class="layui-input" type="text" name="username" autocomplete="username" placeholder="请输入账号">
                <label for="login-password">密码</label>
                <input id="login-password" class="layui-input" type="password" name="password" autocomplete="current-password" placeholder="请输入密码">
                <p id="message" class="auth-message" role="alert" aria-live="polite"></p>
                <button class="public-primary auth-submit" type="submit">登录</button>
                <button class="auth-reset" type="reset">清空输入</button>
            </form>
            <p class="auth-note">患者、医生与管理员使用各自账号登录，无需手动选择身份。</p>
        </div>
        <div id="register-panel" role="tabpanel" aria-labelledby="signUp" hidden>
            <h2>创建账号</h2>
            <p class="auth-caption">已有账号？请切换至账号登录。</p>
            <form action="/regist" id="regest" onsubmit="return regest()">
                <label for="register-cert">身份证号</label>
                <input id="register-cert" class="layui-input" type="text" name="certId" autocomplete="off" placeholder="请输入身份证号">
                <label for="register-username">账号</label>
                <input id="register-username" class="layui-input" type="text" name="username" autocomplete="username" placeholder="设置账号">
                <label for="register-password">密码</label>
                <input id="register-password" class="layui-input" type="password" name="password" autocomplete="new-password" placeholder="设置密码">
                <p id="message2" class="auth-message" role="alert" aria-live="polite"></p>
                <button class="public-primary auth-submit" type="submit">注册</button>
                <button class="auth-reset" type="reset">清空输入</button>
            </form>
            <p class="auth-note">注册信息将按现有系统流程校验；身份及资料关联以系统结果为准。</p>
        </div>
    </section>
</main>
<footer class="public-footer"><div class="public-shell"><span>医院服务平台</span><span>预约挂号 · 就诊记录 · 医疗信息查询</span><a href="/hospital">返回首页</a></div></footer>
<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<link rel="stylesheet" href="/static/css/hospital-feedback.css?v=20261031-3">
<script src="/static/js/hospital-feedback.js?v=20261031-1"></script>
<script src="/static/js/jquery-3.3.1.min.js"></script>
  <script type="text/javascript">
	  function regest(){
		  var d = {};
		  var t = $('#regest').serializeArray();
		  $.each(t, function() {
			  d[this.name] = this.value;
		  });
		  var json=JSON.stringify(d);
		  HospitalFeedback.request({
			  url:"/regest",
			  type:'post',
			  data:json,
			  contentType: "application/json;charset=utf-8",
			  success:function(data){
				  console.log(data.message);
				  if(data.message=="注册成功"){
					  HospitalFeedback.result('注册成功，请使用账号登录。', function () { location.href="/hospital/login"; });
				  }
				  else {
					  $("#message2").text(data.message);

				  }
			  },
			  error:function (data) {
				  HospitalFeedback.error(data);
			  }
		  });
		  return false
	  }
	  function login(){
		  var d = {};
		  var t = $('#login').serializeArray();
		  $.each(t, function() {
			  d[this.name] = this.value;
		  });
		  var json=JSON.stringify(d);
		  HospitalFeedback.request({
			  url:"/login",
			  type:'post',
			  data:json,
			  contentType: "application/json;charset=utf-8",
			  success:function(data){
				  if(data.message.indexOf("登录成功")!=-1){
				  	if(data.message.indexOf("3")!=-1){
						location.href="/hospital/patient/index"
					}
					else  if(data.message.indexOf("2")!=-1){
						location.href="/doctor/seekMedicalAdvice"
					  }
					else if(data.message.indexOf("1")!=-1){
						location.href="/hospital/admin/index"
					  }

				  }
				  else {
					  $("#message").text(data.message);

				  }
			  },
			  error:function (data) {
				  HospitalFeedback.error(data);
			  }
		  });
		  return false
	  }
  </script>
<script src="/static/js/hospital-public.js?v=20261031-1"></script>
</body>
</html>
