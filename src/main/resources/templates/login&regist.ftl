<!DOCTYPE html>
<html lang="en" >

<head>
<meta charset="UTF-8">
<title>登录&注册</title>

<!--图标样式-->
<link rel='stylesheet' href='../static/css/all.min.css'>

<link rel="stylesheet" href="../static/css/style.css">

</head>

<body>

  <h2>医疗管理系统</h2>
<div class="container" id="container">
	<div class="form-container sign-up-container">
		<form action="/regist" id="regest" onsubmit="return regest()">
			<h1>注册</h1>
			<input type="certId" name="certId" placeholder="身份证号(若注册管理员则不需要)" />
			<input type="username" name="username" placeholder="账号" />
			<input type="password" name="password" placeholder="密码" />
			<div>
			<button type="submit">注册</button><button type="reset">重置</button>
			</div>
			<span id="message2" style="color: red"></span>
		</form>
	</div>
	<div class="form-container sign-in-container">
		<form  id="login" onsubmit="return login()">
			<h1>登录</h1>
			<input type="username" name="username" placeholder="账号" value="admin1"/>
			<input type="password"  name="password" placeholder="密码" value="123456"/>
			<div>
				<button type="submit">登录</button><button type="reset">重置</button>
			</div>
			<span id="message" style="color: red"></span>
		</form>
	</div>
	<div class="overlay-container">
		<div class="overlay">
			<div class="overlay-panel overlay-left">
				<h1>欢迎回来！</h1>
				<p>请您先登录的个人信息，进行操作。</p>
				<button class="ghost" id="signIn">登录</button>
			</div>
			<div class="overlay-panel overlay-right">
				<h1>你好朋友！</h1>
				<p>输入您的个人信息注册。</p>
				<button  class="ghost" id="signUp">注册</button>
			</div>
		</div>
	</div>
</div>
  <script src="/static/js/layui.all.js"></script>
  <script src="/static/js/jquery-3.3.1.min.js"></script>
  <script type="text/javascript">
	  function regest(){
		  var d = {};
		  var t = $('#regest').serializeArray();
		  $.each(t, function() {
			  d[this.name] = this.value;
		  });
		  var json=JSON.stringify(d);
		  $.ajax({
			  url:"/regest",
			  type:'post',
			  data:json,
			  contentType: "application/json;charset=utf-8",
			  success:function(data){
				  console.log(data.message);
				  if(data.message=="注册成功"){
					  alert('注册成功！！！\n请返回登录页面进行登录或继续注册')
					  location.href="/hospital/login";
				  }
				  else {
					  $("#message2").text(data.message);

				  }
			  },
			  error:function (data) {
				  layer.msg(data.message, {
					  time: 2000, //2s后自动关闭
				  });
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
		  $.ajax({
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
						location.href="/admin/doctorManage"
					  }

				  }
				  else {
					  $("#message").text(data.message);

				  }
			  },
			  error:function (data) {
				  layer.msg(data.message, {
					  time: 2000, //2s后自动关闭
				  });
			  }
		  });
		  return false
	  }
  </script>
  

<script  src="../static/js/index.js"></script>




</body>

</html>
