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
        <form class="layui-form"  onsubmit="return upd('/admin/drug')">
            <table class="layui-table">
                <tr>
                    <td style="width: 100px">药品名称：</td>
                    <td>
                        <input type="text" value="${drug.name}" name="name" required  class="layui-input" >
                        <input type="hidden" value="${drug.id}" name="id" required  class="layui-input" >
                    </td>
                </tr>
                <tr>
                    <td style="width: 100px">药品类型：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="type" >
                                    <option value=""</option>
                                    <option value="0" <#if drug.type==0>selected</#if>>颗粒剂</option>
                                    <option value="1" <#if drug.type==1>selected</#if>>丸剂</option>
                                    <option value="2" <#if drug.type==2>selected</#if>>散剂</option>
                                    <option value="3" <#if drug.type==3>selected</#if>>酊剂</option>
                                    <option value="4" <#if drug.type==4>selected</#if>>片剂</option>
                                    <option value="5" <#if drug.type==5>selected</#if>>胶囊剂</option>
                                </select>
                            </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 100px">价格：</td>
                    <td>
                        <input type="text" value="${drug.price}" name="price" required  class="layui-input" >
                    </td>
                </tr>
                <tr>
                    <td style="width: 100px">数量：</td>
                    <td>
                        <input type="number" value="${drug.number}" name="number" required  class="layui-input" min="1">
                    </td>
                </tr>
                <tr>
                    <td style="width: 100px">简介：</td>
                    <td>
                        <textarea  name="text" required  class="layui-input" >
                            ${drug.text}
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