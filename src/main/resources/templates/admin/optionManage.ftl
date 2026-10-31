<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>查询科目管理</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <#include "../menu.ftl"/>


    <div class="layui-body">
        <!-- 内容主体区域 -->
        <div style="padding: 15px;">
            <h5>查询科目管理</h5>
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
        <div class="layui-btn-container" role="group" aria-label="检查科目列表操作">
                <button type="button" class="layui-btn layui-btn-sm" onclick="showAdd('/admin/optionAdd')"><i class="layui-icon" aria-hidden="true">&#xe608;</i> 新增检查科目</button>
            </div>
            <table class="layui-table">
            <thead>
            <tr>
                <th>序号</th>
                <th>科目名称</th>
                <th>科目地址</th>
                <th>价格</th>
                <th width="180" scope="col">操作</th>
            </tr>
            </thead>
            <tbody>
            <#list options as o>
                <tr>
                    <td>${o_index+1}</td>
                    <td>${o.name!''}</td>
                    <td>${o.type!''}</td>
                    <td>${o.price!''}(元)</td>
                    <td>
                            <div class="layui-btn-container" role="group" aria-label="记录操作">
                                <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" title="编辑" aria-label="编辑" onclick="edit('/admin/option/',${o.id})"><i class="layui-icon" aria-hidden="true">&#xe642;</i> 编辑</button>
                        <button type="button" class="layui-btn layui-btn-danger layui-btn-sm" title="删除" aria-label="删除" onclick="del('/admin/option/',${o.id})"><i class="layui-icon" aria-hidden="true">&#xe640;</i> 删除</button>
                            </div>
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