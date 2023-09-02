<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>详情页面</title>
    <link rel="stylesheet" href="/static/css/layui.min.css">
</head>
<body >
<div class="layui-layout layui-layout-admin">
        <!-- 内容主体区域 -->
        <div style="padding: 15px;">
            <form class="layui-form"  onsubmit="return upd('/admin/medicalhistory')">
            <table class="layui-table">
                <tr>
                    <td style="width: 100px">患者名称：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                            <select name="patientid" >
                                <option value=""></option>
                                <#list patients as p>
                                    <option value="${p.id}"
                                            <#if p.id==medicalhistory.patientid>selected</#if>>${p.name}</option>
                                </#list>
                            </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>既往病史：</td>
                    <td>
                        <input type="text" name="name" required value="${medicalhistory.name}"  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>患病时间：</td>
                    <td>
                            <input type="date" value="${(medicalhistory.time?string('yyyy-MM-dd'))!}" name="time" required  lay-verify="required" autocomplete="off" class="layui-input">
                            <input type="hidden" value="${medicalhistory.id}" name="id" >
                    </td>
                </tr>
                <tr>
                    <td>确诊人：</td>
                    <td>
                        <select name="doctorid">
                            <option value=""></option>
                            <#list doctors as d>
                                <option value="${d.id}" <#if d.id==medicalhistory.doctorid>selected</#if>
                                >${d.name}</option>
                            </#list>
                        </select>
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