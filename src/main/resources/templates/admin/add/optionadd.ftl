<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>新增页面</title>
    <link rel="stylesheet" href="/static/css/layui.min.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <!-- 内容主体区域 -->
    <div style="padding: 15px;">
        <form class="layui-form"  onsubmit="return add('/admin/option/')">
            <table class="layui-table">
                <tr>
                    <td style="width: 100px">检查类型（名称）：</td>
                    <td>
                        <input type="text" name="name" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>检查地点：</td>
                    <td>
                        <input type="text" name="type" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>价格：</td>
                    <td>
                        <input type="text" name="price" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>操作：</td>
                    <td>
                        <button type="submit" class="layui-btn">添加</button>&nbsp;&nbsp;&nbsp;&nbsp;<span style="color: red;" id="message"></span>
                    </td>
                </tr>
                <tbody>
                </tbody>
            </table>
        </form>
    </div>
</div>
<#include "foot.ftl"/>
</body>
</html>