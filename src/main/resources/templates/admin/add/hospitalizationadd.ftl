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
        <form class="layui-form"  onsubmit="return add('/admin/hospitalization')">
            <table class="layui-table">
                <tbody>
                <tr>
                    <td style="width: 100px">楼层：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="floor" >
                                    <option value=""></option>
                                    <option value="1">一层</option>
                                    <option value="2">二层</option>
                                    <option value="3">三层</option>
                                    <option value="4">四层</option>
                                    <option value="5">五层</option>
                                </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>门牌号：</td>
                    <td>
                        <input type="text" name="door" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>床号：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="bed" >
                                    <option value=""></option>
                                    <option value="1">一号床</option>
                                    <option value="2">二号床</option>
                                    <option value="3">三号床</option>
                                    <option value="4">四号床</option>
                                </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>患者</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="patientid" >
                                    <option value=""></option>
                                    <#list patients as p>
                                        <option value="${p.id}">${p.name}</option>
                                    </#list>
                                </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>患病</td>
                    <td>
                        <input type="text" name="medicalname"  required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>住院时间：</td>
                    <td>
                        <input type="date" name="intime" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>出院时间：</td>
                    <td>
                        <input type="date" name="outtime" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>操作：</td>
                    <td>
                        <button type="submit" class="layui-btn">添加</button>
                    </td>
                </tr>

                </tbody>
            </table>
        </form>
    </div>
</div>
<#include "foot.ftl"/>
</body>
</html>