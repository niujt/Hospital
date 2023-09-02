<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>疾病管理</title>
    <link rel="stylesheet" href="/static/css/layui.min.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <#include "../menu.ftl"/>


    <div class="layui-body">
        <!-- 内容主体区域 -->
        <div style="padding: 15px;">
            <h5>疾病管理</h5>
            <#--<form action="/admin/medicalhistoryManage" method="get">-->
            <#--<table>-->
            <#--<tr>-->
            <#--<td>患者:</td>-->
            <#--<td><input name="patientname" autocomplete="off"  class="layui-input lay-size=sm" ></td>-->
            <#--<td>&nbsp;&nbsp;确诊人:</td>-->
            <#--<td><input name="doctorname" autocomplete="off" class="layui-input lay-size=sm" ></td>-->
            <#--<td>   <button class="layui-btn" type="submit"><i class="layui-icon">&#xe615;</i></button></div></td>-->
            <#--</tr>-->
            <#--</table>-->
            <#--</form>-->
            <table class="layui-table">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>疾病名称</th>
                    <th>介绍</th>
                    <th>操作&nbsp;&nbsp;&nbsp;&nbsp;
                        <button class="layui-btn" onclick="showAdd('/admin/illnessAdd')"><i class="layui-icon">&#xe608;</i> 添加</button>
                    </th>
                </tr>
                </thead>
                <tbody>
                <#list illnesses as i>
                    <tr>
                        <td>${i_index+1}</td>
                        <td>${i.name!''}</td>
                        <td>这是一种...（请点击详情查看疾病详情）</td>
                        <td>
                            <button class="layui-btn" onclick="edit('/admin/illness/',${i.id})"><i class="layui-icon">&#xe642;</i></button>
                            <button class="layui-btn" onclick="del('/admin/illness/',${i.id})"><i class="layui-icon">&#xe640;</i></button>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
            <#--<div id="test1"></div>-->
        </div>
    </div>
    <#include "../foot.ftl"/>
</div>
<script>
    layui.use('laypage', function(){
        var laypage = layui.laypage;

        //执行一个laypage实例
        laypage.render({
            elem: 'test1' //注意，这里的 test1 是 ID，不用加 # 号
            ,count: 5 //数据总数，从服务端得到
            ,limit:5
        });
    });
</script>
</body>
</html>