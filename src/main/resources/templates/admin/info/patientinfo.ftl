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
        <form class="layui-form"  onsubmit="return upd('/admin/patient')">
            <table class="layui-table">
                <thead>
                <tr>
                    <td colspan="2">基本信息</td>
                </tr>
                </thead>
                    <tr>
                        <td style="width: 100px">登录账号：</td>
                        <td>
                            <input type="text" value="${patient.username!''}" name="username" required  class="layui-input" >
                            <input type="hidden" value="${patient.loginid!''}" name="loginid" required  class="layui-input" >
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 100px">登录密码：</td>
                        <td>
                            <input type="text" value="${patient.password!''}" name="password" required  class="layui-input" >
                        </td>
                    </tr>
                <tr>
                    <td style="width: 100px">姓名：</td>
                    <td>
                        <input type="text" value="${patient.name}" name="name" required  class="layui-input" >
                    </td>
                </tr>
                <tr>
                    <td>年龄：</td>
                    <td>
                        <input type="text" value="${patient.age}" name="age" required  class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>身份证号：</td>
                    <td>
                        <input type="text" value="${patient.certId}" name="certId" required   class="layui-input">
                        <input type="hidden" value="${patient.id}" name="id" >
                    </td>
                </tr>
                <tr>
                    <td>性别：</td>
                    <td>
                        <div class="layui-input-block">
                            <input type="radio" name="sex" value="0" title="男" <#if patient.sex==0>checked</#if>>
                            <input type="radio" name="sex" value="1" title="女" <#if patient.sex==1>checked</#if>>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>地址：</td>
                    <td>
                        <input type="text" value="${patient.address}" name="address" required  class="layui-input" >
                    </td>
                </tr>
                <thead>
                <tr>
                    <td colspan="2">住院信息</td>
                </tr>
                </thead>
                <#list hospitalizations as hospitalization>
                    <tr>
                        <td>序号</td>
                        <td>${hospitalization_index+1}</td>
                    </tr>
                <tr>
                    <td style="width: 100px">楼层：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="floor" readonly="readonly">
                                    <option value=""></option>
                                    <option value="1" <#if hospitalization.floor=="1">selected</#if>>一层</option>
                                    <option value="2" <#if hospitalization.floor=="2">selected</#if>>二层</option>
                                    <option value="3" <#if hospitalization.floor=="3">selected</#if>>三层</option>
                                    <option value="4" <#if hospitalization.floor=="4">selected</#if>>四层</option>
                                    <option value="5" <#if hospitalization.floor=="5">selected</#if>>五层</option>
                                </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>门牌号：</td>
                    <td>
                        <input readonly="readonly" type="text" name="door" value="${hospitalization.door}"  required  lay-verify="required"  autocomplete="off" class="layui-input">
                        <#--<input type="hidden" name="medicalhistoryid" value="${hospitalizationid}">-->
                    </td>
                </tr>
                <tr>
                    <td>床号：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="bed" readonly="readonly">
                                    <option value=""></option>
                                    <option value="1" <#if hospitalization.bed=="1">selected</#if>>一号床</option>
                                    <option value="2" <#if hospitalization.bed=="2">selected</#if>>二号床</option>
                                    <option value="3" <#if hospitalization.bed=="3">selected</#if>>三号床</option>
                                    <option value="4" <#if hospitalization.bed=="4">selected</#if>>四号床</option>
                                </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>患病：</td>
                    <td>
                        <input type="text" readonly="readonly" name="medicalname" value="${hospitalization.medicalname}" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>住院时间：</td>
                    <td>
                        <input type="date" readonly="readonly" name="intime" value="${(hospitalization.intime?string('yyyy-MM-dd'))!}" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>住院时间：</td>
                    <td>
                        <input type="date" readonly="readonly" name="outtime" required value="${(hospitalization.outtime?string('yyyy-MM-dd'))!}"  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                </#list>
                <thead>
                <tr>
                    <td colspan="2">预约信息</td>
                </tr>
                </thead>
                <#list appointments as appointment>
                    <tr>
                        <td>序号</td>
                        <td>${appointment_index+1}</td>
                    </tr>
                <tr>
                    <td>预约医生：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="doctorid" readonly="readonly">
                                    <option value=""></option>
                                    <#list doctors as d>
                                        <option value="${d.id}"
                                                <#if d.id==appointment.doctorid>selected</#if>
                                        >${d.name}</option>
                                    </#list>
                                </select>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>预约时间：</td>
                    <td>
                        <input readonly="readonly" type="date" value="${(appointment.time?string('yyyy-MM-dd'))!}" name="time" required  lay-verify="required" autocomplete="off" class="layui-input">
                        <#--<input type="hidden" value="${patient.appointmentid}" name="appointmentid" >-->
                    </td>
                </tr>
                </#list>
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