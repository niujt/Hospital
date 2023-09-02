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
    <div style="padding: 15px;width: 80%">
        <form class="layui-form"  onsubmit="return add('/admin/doctor/')">
            <table class="layui-table">
                <tr>
                    <td style="width: 100px">姓名：</td>
                    <td>
                        <input type="text" name="name" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>是否为专家：</td>
                    <td>
                        <select name="expert">
                            <option value="">请选择</option>
                            <option value="0" >非专家</option>
                            <option value="1" >专家</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>年龄：</td>
                    <td>
                        <input type="text" name="age" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>身份证号：</td>
                    <td>
                        <input type="text" name="certId" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>性别：</td>
                    <td>
                        <input type="radio" name="sex" class="layui-input" title="男" value="0"><input type="radio" name="sex" class="layui-input" title="女" value="1">
                    </td>
                </tr>
                <tr>
                    <td>部门：</td>
                    <td>
                        <select name="department" >
                            <option value="口腔科">口腔科</option>
                            <option value="妇产科">妇产科</option>
                            <option value="内科">内科</option>
                            <option value="外科">外科</option>
                            <option value="精神科">精神科</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>家庭住址：</td>
                    <td>
                        <input type="text" name="address" required  lay-verify="required"  autocomplete="off" class="layui-input">
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