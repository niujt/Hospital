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
        <form class="layui-form"  onsubmit="return add('/admin/drug/')">
            <table class="layui-table">
                <tr>
                    <td style="width: 100px">药品名称：</td>
                    <td>
                        <input type="text" name="name" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>药物类型：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="type" >
                                    <option value=""></option>
                                    <option value="0">颗粒剂</option>
                                    <option value="1">丸剂</option>
                                    <option value="2">散剂</option>
                                    <option value="3">酊剂</option>
                                    <option value="4">片剂</option>
                                    <option value="5">胶囊剂</option>
                                </select>
                            </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 100px">价格：</td>
                    <td>
                        <input type="text" name="price" required  class="layui-input" >
                    </td>
                </tr>
                <tr>
                    <td style="width: 100px">数量：</td>
                    <td>
                        <input type="number"  name="number" required  class="layui-input" min="1">
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