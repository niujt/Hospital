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
        <form class="layui-form"  onsubmit="return upd('/admin/doctor')">
            <table class="layui-table">
                    <tr>
                        <td style="width: 100px">登录账号：</td>
                        <td>
                            <input type="text" value="${doctor.username!''}" name="username" required  class="layui-input" >
                            <input type="hidden" value="${doctor.loginid!''}" name="loginid" required  class="layui-input" >
                        </td>
                    </tr>
                    <tr>
                        <td style="width: 100px">登录密码：</td>
                        <td>
                            <input type="text" value="${doctor.password!''}" name="password" required  class="layui-input" >
                        </td>
                    </tr>
                <tr>
                    <td style="width: 100px">姓名：</td>
                    <td>
                        <input type="text" value="${doctor.name}" name="name" required  class="layui-input" >
                    </td>
                </tr>
                <tr>
                    <td>是否为专家：</td>
                    <td>
                        <select name="expert">
                            <option value="">请选择</option>
                            <option value="0" <#if doctor.expert==0>selected</#if>>非专家</option>
                            <option value="1" <#if doctor.expert==1>selected</#if>>专家</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>年龄：</td>
                    <td>
                        <input type="text" value="${doctor.age}" name="age" required  class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>身份证号：</td>
                    <td>
                        <input type="text" value="${doctor.certId}" name="certId" required   class="layui-input">
                        <input type="hidden" value="${doctor.id}" name="id" >
                    </td>
                </tr>
                <tr>
                    <td>性别：</td>
                    <td>
                        <div class="layui-input-block">
                            <input type="radio" name="sex" value="0" title="男" <#if doctor.sex==0>checked</#if>>
                            <input type="radio" name="sex" value="1" title="女" <#if doctor.sex==1>checked</#if>>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>部门：</td>
                    <td>
                        <select name="department" >
                            <option value="口腔科" <#if doctor.department=="口腔科">selected</#if>>口腔科</option>
                            <option value="妇产科" <#if doctor.department=="妇产科">selected</#if>>妇产科</option>
                            <option value="内科" <#if doctor.department=="内科">selected</#if>>内科</option>
                            <option value="外科" <#if doctor.department=="外科">selected</#if>>外科</option>
                            <option value="精神科" <#if doctor.department=="精神科">selected</#if>>精神科</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>地址：</td>
                    <td>
                        <input type="text" value="${doctor.address}" name="address" required  class="layui-input" >
                    </td>
                </tr>
                <tr>
                    <td>简介：</td>
                    <td>
                        <textarea name="text" required  class="layui-input" >
                            ${doctor.text}
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