<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>医生管理界面</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <#include "../menu.ftl"/>
    <div class="layui-body">
        <!-- 内容主体区域 -->
        <div style="padding: 15px;">
            <h5>医生管理</h5><p><a class="layui-btn layui-btn-sm layui-btn-primary" href="/finance/admin">分配医生登录账号 / 财务管理</a></p>
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
            <div class="layui-btn-container" role="group" aria-label="医生列表操作">
                <button type="button" class="layui-btn layui-btn-sm" onclick="showAdd('/admin/doctorAdd')"><i class="layui-icon" aria-hidden="true">&#xe608;</i> 新增医生</button>
            </div>
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
                    <th width="180" scope="col">操作</th>
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
                            <div class="layui-btn-container" role="group" aria-label="记录操作">
                                <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" title="编辑" aria-label="编辑" onclick="edit('/admin/doctor/',${d.id})"><i class="layui-icon" aria-hidden="true">&#xe642;</i> 编辑</button>
                                <button type="button" class="layui-btn layui-btn-danger layui-btn-sm" title="删除" aria-label="删除" onclick="del('/admin/doctor/',${d.id})"><i class="layui-icon" aria-hidden="true">&#xe640;</i> 删除</button>
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