<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>查看病史</title>
    <link rel="stylesheet" href="/static/css/layui.min.css">
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <!-- 内容主体区域 -->
    <div style="padding: 15px;">
        <#list medicalhistorys as medicalhistory>
            <table class="layui-table">
                <tr>
                    <td>既往病史：</td>
                    <td>
                        ${medicalhistory.name}
                    </td>
                </tr>
                <tr>
                    <td>患病时间：</td>
                    <td>
                        ${(medicalhistory.time?string('yyyy-MM-dd'))!}
                    </td>
                </tr>
                <tr>
                    <td>就诊医生：</td>
                    <td>
                        ${medicalhistory.doctorname}
                    </td>
                </tr>
            </table>
            <hr>
        </#list>
    </div>
</div>
<script src="../static/js/layui.all.js"></script>
</body>
</html>
