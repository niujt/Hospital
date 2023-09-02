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
        <form class="layui-form"  onsubmit="return upd('/admin/hospitalization')">
            <table class="layui-table">
                <tr>
                    <td style="width: 100px">楼层：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="floor" >
                                    <option value=""></option>
                                    <option value="1" <#if h.floor=='1'>selected</#if>>一层</option>
                                    <option value="2" <#if h.floor=='2'>selected</#if>>二层</option>
                                    <option value="3" <#if h.floor=='3'>selected</#if>>三层</option>
                                    <option value="4" <#if h.floor=='4'>selected</#if>>四层</option>
                                    <option value="5" <#if h.floor=='5'>selected</#if>>五层</option>
                                </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>门牌号：</td>
                    <td>
                        <input type="text" name="door" value="${h.door}" required  lay-verify="required"  autocomplete="off" class="layui-input">
                        <input type="hidden" name="id" value="${h.id}">
                    </td>
                </tr>
                <tr>
                    <td>床号：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="bed" >
                                    <option value=""></option>
                                    <option value="1" <#if h.bed=='1'>selected</#if>>一号床</option>
                                    <option value="2" <#if h.bed=='2'>selected</#if>>二号床</option>
                                    <option value="3" <#if h.bed=='3'>selected</#if>>三号床</option>
                                    <option value="4" <#if h.bed=='4'>selected</#if>>四号床</option>
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
                                        <option value="${p.id}"
                                        <#if h.patientid==p.id>selected</#if>>${p.name}</option>
                                    </#list>
                                </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>患病：</td>
                    <td>
                        <input type="text" name="medicalname" value="${h.medicalname}" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>住院时间：</td>
                    <td>
                        <input type="date" name="intime" value="${(h.intime?string('yyyy-MM-dd'))!}" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>出院时间：</td>
                    <td>
                        <input type="date" name="outtime" required value="${(h.outtime?string('yyyy-MM-dd'))!}"  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>操作：</td>
                    <td>
                        <button type="submit" class="layui-btn">更新</button>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<#include "foot.ftl"/>
</body>
</html>