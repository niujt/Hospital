<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>药品管理</title>
    <link rel="stylesheet" href="../static/css/layui.min.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <#include "../menu.ftl"/>


    <div class="layui-body">
        <div style="padding: 15px;">
        <h5>药品管理</h5>
        <form action="/admin/drugsManage" method="get" class="layui-form">
            <table>
                <tr>
                    <td>药品名称:</td>
                    <td><input name="name" value="${name!''}" autocomplete="off"  class="layui-input lay-size=sm" ></td>
                    <td>&nbsp;&nbsp;药品类型:</td>
                    <td>
                        <select name="type">
                            <option value="">请选择药品类型</option>
                            <option value="0" >颗粒剂</option>
                            <option value="1" >丸剂</option>
                            <option value="2" >散剂</option>
                            <option value="3" >酊剂</option>
                            <option value="4" >片剂</option>
                            <option value="5" >胶囊剂</option>
                        </select>
                    </td>
                    <td>   <button class="layui-btn" type="submit"><i class="layui-icon">&#xe615;</i></button></div></td>
    </tr>
    </table>
    </form>
        <!-- 内容主体区域 -->
            <table class="layui-table">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>药品名称</th>
                    <th>药品类型</th>
                    <th>数量（盒）</th>
                    <th>单价（盒/元）</th>
                    <th>操作&nbsp;&nbsp;&nbsp;&nbsp;
                        <button class="layui-btn" onclick="showAdd('/admin/drugAdd')"><i class="layui-icon">&#xe608;</i> 添加</button>
                    </th>
                </tr>
                </thead>
                <tbody>
                <#list drugs as d>
                    <tr>
                        <td>${d_index+1}</td>
                        <td>${d.name}</td>
                        <td>
                            <#if d.type==0>颗粒剂</#if>
                            <#if d.type==1>丸剂</#if>
                            <#if d.type==2>散剂</#if>
                            <#if d.type==3>酊剂</#if>
                            <#if d.type==4>片剂</#if>
                            <#if d.type==5>胶囊剂</#if>
                        </td>
                        <td>${d.number}</td>
                        <td>${d.price}</td>
                        <td>
                            <button class="layui-btn" onclick="edit('/admin/drug/',${d.id})"><i class="layui-icon">&#xe642;</i></button>
                            <button class="layui-btn" onclick="del('/admin/drug/',${d.id})"><i class="layui-icon">&#xe640;</i></button>
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