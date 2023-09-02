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
            <form class="layui-form"  onsubmit="return add('/admin/appointment/')">
            <table class="layui-table">
                <tr>
                    <td style="width: 100px">患者名称：</td>
                    <td colspan="3">
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
                    <td>预约科室：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select id="department" lay-filter="department" >
                                    <option value="">请选择科室</option>
                                    <option value="精神科">精神科</option>
                                    <option value="妇产科">妇产科</option>
                                    <option value="口腔科">口腔科</option>
                                    <option value="骨科">骨科</option>
                                    <option value="外科">外科</option>
                                    <option value="内科">内科</option>
                                </select>
                            </div>
                        </div>
                    </td>
                    <td>预约医生：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="doctorid" id="doctorid">
                                </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>预约时间：</td>
                    <td colspan="3">
                            <input type="date" name="time" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>门诊费：</td>
                    <td colspan="3">
                        <input type="text" name="expenses" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>操作：</td>
                    <td colspan="3">
                        <button type="submit" class="layui-btn">添加</button>
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