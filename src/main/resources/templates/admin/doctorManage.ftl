<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>医生管理界面</title>
    <link rel="stylesheet" href="../static/css/layui.min.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <#include "../menu.ftl"/>
    <div class="layui-body">
        <!-- 内容主体区域 -->
        <div style="padding: 15px;">
            <h5>医生管理</h5>
            <form action="/admin/doctorManage" method="get">
            <table>
                <tr>
                    <td>昵称:</td>
                    <td><input name="name" autocomplete="off" value="${name!''}" class="layui-input lay-size=sm" ></td>
                    <td>&nbsp;&nbsp;证件号:</td>
                    <td><input name="certId" autocomplete="off" value="${certId!''}" class="layui-input lay-size=sm" ></td>
                    <td>   <button class="layui-btn" type="submit"><i class="layui-icon">&#xe615;</i></button></div></td>
                </tr>
            </table>
            </form>
            <table class="layui-table">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>昵称</th>
                    <th>是否为专家</th>
                    <th>年纪</th>
                    <th>性别</th>
                    <th>证件号</th>
                    <th>所属部门</th>
                    <th>家庭住址</th>
                    <th>操作&nbsp;&nbsp;&nbsp;&nbsp;
                        <button class="layui-btn" onclick="showAdd('/admin/doctorAdd')"><i class="layui-icon">&#xe608;</i> 添加</button>
                    </th>
                </tr>
                </thead>
                <tbody>
                <#list doctors as d>
                    <tr>
                        <td>${d_index+1}</td>
                        <td>${d.name}</td>
                        <td>
                            <#if d.expert==0>非专家</#if>
                            <#if d.expert==1>专家</#if>
                        </td>
                        <td>${d.age}</td>
                        <td>
                            <#if d.sex==0>男</#if>
                            <#if d.sex==1>女</#if>
                        </td>
                        <td>${d.certId}</td>
                        <td>${d.department}</td>
                        <td>${d.address}</td>
                        <td>
                            <button class="layui-btn" onclick="edit('/admin/doctor/',${d.id})"><i class="layui-icon">&#xe642;</i></button>
                            <button class="layui-btn" onclick="del('/admin/doctor/',${d.id})"><i class="layui-icon">&#xe640;</i></button>
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
<script src="../static/js/layui.all.js"></script>
<script>
    //JavaScript代码区域
    layui.use('element', function(){
        var element = layui.element;

    });
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