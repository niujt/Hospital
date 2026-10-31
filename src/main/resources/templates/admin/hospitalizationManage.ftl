<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>住院信息管理</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <#include "../menu.ftl"/>


    <div class="layui-body">

        <!-- 内容主体区域 -->
        <div style="padding: 15px;">
            <h5>住院信息管理</h5>
            <form action="/admin/hospitalizationManage" method="get">
                <table>
                    <tr>
                        <td>患者:</td>
                        <td><input name="patientname" value="${patientname!''}" autocomplete="off"  class="layui-input lay-size=sm" ></td>
                        <td>&nbsp;&nbsp;入院时间:</td>
                        <td><input name="intime" value="${intime!''}" type="date" autocomplete="off" class="layui-input lay-size=sm" ></td>
                        <td>   <button class="layui-btn" type="submit"><i class="layui-icon">&#xe615;</i></button></div></td>
        </tr>
        </table>
        </form>
            <div class="layui-btn-container" role="group" aria-label="住院记录列表操作">
                <button type="button" class="layui-btn layui-btn-sm" onclick="showAdd('/admin/hospitalizationAdd')"><i class="layui-icon" aria-hidden="true">&#xe608;</i> 新增住院记录</button>
                        <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" onclick="location.href='/admin/getHospitalization'"><i class="layui-icon" aria-hidden="true">&#xe67d;</i> 导出 Excel</button>
            </div>
            <table class="layui-table">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>楼层号</th>
                    <th>门牌号</th>
                    <th>床号</th>
                    <th>患者</th>
                    <th>患病</th>
                    <th>住院时间</th>
                    <th>出院时间</th>
                    <th width="180" scope="col">操作</th>
                </tr>
                </thead>
                <tbody>
                <#list hospitalizations as h>
                    <tr>
                        <td>${h_index+1}</td>
                        <td>${h.floor}</td>
                        <td>${h.door}</td>
                        <td>${h.bed}</td>
                        <td>${h.patientname!''}</td>
                        <td>${h.medicalname}</td>
                        <td>${(h.intime?string("yyyy-MM-dd"))!}</td>
                        <td>${(h.outtime?string("yyyy-MM-dd"))!}</td>
                        <td>
                            <div class="layui-btn-container" role="group" aria-label="记录操作">
                                <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" title="编辑" aria-label="编辑" onclick="edit('/admin/hospitalization/',${h.id})"><i class="layui-icon" aria-hidden="true">&#xe642;</i> 编辑</button>
                                <button type="button" class="layui-btn layui-btn-danger layui-btn-sm" title="删除" aria-label="删除" onclick="del('/admin/hospitalization/',${h.id})"><i class="layui-icon" aria-hidden="true">&#xe640;</i> 删除</button>
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