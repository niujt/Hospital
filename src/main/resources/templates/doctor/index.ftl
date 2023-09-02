<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="/static/css/layui.min.css">
    <script type="text/javascript" src="/static/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="/static/js/jquery.indexSlidePattern.js"></script>
    <title>管理员首页</title>
    <style>
        * { padding: 0; margin: 0; list-style-type: none; }
        a, img { border: 0; }
        /* demo */
        /*五张图片在缩略图时所显示的位置调整*/
        #demo li:nth-of-type(1) img { transform: translate(-210px); }
        #demo li:nth-of-type(2) img { transform: translate(-180px); }
        #demo li:nth-of-type(3) img { transform: translate(-380px); }
        #demo li:nth-of-type(4) img { transform: translate(-450px); }
        #demo li:nth-of-type(5) img { transform: translate(-320px); }
        #demo { width: 1160px; height: 512px; margin: 60px auto 0 auto; }
        #demo img { width: 820px; height: 512px; max-width: 820px; }
        #demo li { float: left; position: relative; width: 82px; height: 100%; overflow: hidden; cursor: pointer; transition: 0.5s; transform-origin: bottom; filter: alpha(opacity=50); opacity: 0.5; }
        #demo li img { transition: 1.2s; }
        #demo li a { display: block; }
        #demo li div { position: absolute; bottom: 0; left: 0; width: 100%; background: #000; line-height: 32px; filter: alpha(opacity=70); opacity: 0.7; text-indent: 2em; }
        #demo li div a { color: #FFF; text-decoration: none; }
        #demo li div a:hover { color: #F00; text-decoration: none; }
        #demo li.active { cursor: pointer; transform: scale(1.02, 1.08); z-index: 3; width: 820px; filter: alpha(opacity=100); opacity: 1; }
        #demo li.active img { transform: translate(0px); }
        #demo li:nth-of-type(1) { transform-origin: bottom left; }
        #demo li:nth-of-type(5) { transform-origin: bottom right; }
    </style>
    <script type="text/javascript">
        $(document).ready(function(e){
            var opt	=	{
                "speed"	:	"fast"		,	//变换速度,三速度可选 slow,normal,fast;
                "by"	:	"mouseover"		,	//触发事件,click或者mouseover;
                "auto"	:	true		,	//是否自动播放;
                "sec"	:	3000	 		//自动播放间隔;
            };
            $("#demo").IMGDEMO(opt);
        });
    </script>
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <#include "../menu.ftl"/>
    <div class="layui-body">
    <h3>欢迎进入医疗管理系统</h3>
    <br><br>
    <ul id="demo">
        <li class="active">
            <img src="/static/images/1.png"  />
        </li>
        <li>
            <img src="/static/images/2.png"  />
        </li>
        <li>
            <img src="/static/images/3.png"  />
        </li>
        <li>
            <img src="/static/images/4.png"  />
        </li>
        <li>
            <img src="/static/images/5.png"  />
        </li>
    </ul>
</div>
    <script src="../../static/js/layui.all.js"></script>
</body>
</html>