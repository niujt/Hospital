<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>详情页面</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
</head>
<body >
<div class="layui-layout layui-layout-admin">
    <!-- 内容主体区域 -->
    <div style="padding: 15px;">
        <form data-modal-form class="layui-form"  onsubmit="return upd('/admin/admin')">
            <table class="layui-table">
                <tr>
                    <td style="width: 100px">管理员账号：</td>
                    <td>
                        <input type="text" value="${admin.username?html}" name="username" required  class="layui-input" >
                        <input type="hidden" value="${admin.id}" name="id" required  class="layui-input" >
                    </td>
                </tr>
                <tr>
                    <td style="width: 100px">管理员密码：</td>
                    <td>
                        <input type="password" name="password" minlength="8" maxlength="72" autocomplete="new-password" placeholder="留空表示不修改；新密码8至72个字符" class="layui-input">
                        <p class="layui-text">不显示原密码；仅在需要重设时填写。</p>
                    </td>
                </tr>
                <tr data-modal-actions>
                    <td>操作：</td>
                    <td>
                        <button type="submit" class="layui-btn">更新</button>&nbsp;&nbsp;&nbsp;&nbsp;<span style="color: red;" id="message"></span>
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