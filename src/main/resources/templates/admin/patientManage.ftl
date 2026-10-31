<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>患者页面管理</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <#include "../menu.ftl"/>
    <div class="layui-body">
        <!-- 内容主体区域 -->
        <div style="padding: 15px;">
            <h5>患者管理</h5>
            <form action="/admin/patientManage" method="get">
                <table>
                    <tr>
                        <td>昵称:</td>
                        <td><input name="name" value="${name!''}" autocomplete="off"  class="layui-input lay-size=sm" ></td>
                        <td>&nbsp;&nbsp;证件号:</td>
                        <td><input name="certId" value="${certId!''}" autocomplete="off" class="layui-input lay-size=sm" ></td>
                        <td>   <button class="layui-btn" type="submit"><i class="layui-icon">&#xe615;</i></button></div></td>
        </tr>
        </table>
        </form>
            <div class="layui-btn-container" role="group" aria-label="患者列表操作">
                <button type="button" class="layui-btn layui-btn-sm" onclick="showAdd('/admin/patientAdd')"><i class="layui-icon" aria-hidden="true">&#xe608;</i> 新增患者</button>
            </div>
            <table class="layui-table">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>昵称</th>
                    <th>年纪</th>
                    <th>性别</th>
                    <th>证件号</th>
                    <th>主治医生</th>
                    <th>是否出院</th>
                    <th>家庭住址</th>
                    <th width="180" scope="col">操作</th>
                </tr>
                </thead>
                <tbody>
                <#list patients as p>
                    <tr>
                        <td>${p_index+1}</td>
                        <td>${p.name}</td>
                        <td>${p.age}</td>
                        <td>
                            <#if p.sex==0>男</#if>
                            <#if p.sex==1>女</#if>
                        </td>
                        <td>${p.certId}</td>
                        <td>${p.doctorname!''}</td>
                        <td>
                            <#if p.isout==0>未入院</#if>
                            <#if p.isout==1>已入院</#if>
                            <#if p.isout==2>已出院</#if>
                        </td>
                        <td>${p.address}</td>
                        <td>
                            <div class="layui-btn-container" role="group" aria-label="记录操作">
                                <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" title="编辑" aria-label="编辑" onclick="edit('/admin/patient/',${p.id})"><i class="layui-icon" aria-hidden="true">&#xe642;</i> 编辑</button>
                                <button type="button" class="layui-btn layui-btn-danger layui-btn-sm" title="删除" aria-label="删除" onclick="del('/admin/patient/',${p.id})"><i class="layui-icon" aria-hidden="true">&#xe640;</i> 删除</button>
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