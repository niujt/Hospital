<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>新增页面</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
</head>
<body>
<div class="layui-layout layui-layout-admin">
    <!-- 内容主体区域 -->
    <div style="padding: 15px;">
        <form data-modal-form class="layui-form"  onsubmit="return add('/admin/admin/')">
            <table class="layui-table">
                <tr>
                    <td style="width: 100px">管理员账号：</td>
                    <td>
                        <input type="text" name="username" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>管理员密码：</td>
                    <td>
                        <input type="password" name="password" required minlength="8" maxlength="72" placeholder="8至72个字符" lay-verify="required" autocomplete="new-password" class="layui-input">
                    </td>
                </tr>
                <tr data-modal-actions>
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