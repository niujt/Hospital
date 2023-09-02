<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>详情页面</title>
    <link rel="stylesheet" href="/static/css/layui.min.css">
</head>
<body>
<div class="layui-layout layui-layout-admin">
    <!-- 内容主体区域 -->
    <div style="padding: 15px;">
        <form class="layui-form"  onsubmit="return upd('/admin/illness')">
            <table class="layui-table">
                <tr>
                    <td style="width: 100px">疾病名称：</td>
                    <td>
                        <input type="text" value="${illness.name}" name="name" required  class="layui-input" >
                        <input type="hidden" value="${illness.id}" name="id" required  class="layui-input" >
                    </td>
                </tr>
                <tr>
                    <td style="width: 100px">疾病介绍：</td>
                    <td>
                        <textarea  name="text" required  class="layui-input" >
                            ${illness.text}
                        </textarea>
                    </td>
                </tr>
                <tr>
                    <td>操作：</td>
                    <td>
                        <button type="submit" class="layui-btn">更新</button>
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