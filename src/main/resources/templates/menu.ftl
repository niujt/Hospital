<div class="layui-header">
    <div class="layui-logo">医疗系统<#if login.role==1>后台管理</#if></div>
    <ul class="layui-nav layui-layout-right">
        <li class="layui-nav-item">
            <a href="javascript:">
                欢迎&nbsp;&nbsp;&nbsp;&nbsp;${login.username}
            </a>
        </li>
        <li class="layui-nav-item"><a href="/loginout">退出登录</a></li>
    </ul>
</div>
<div class="layui-side layui-bg-black">
    <div class="layui-side-scroll">
    <ul class="layui-nav layui-nav-tree layui-bg-black" lay-filter="workspace-navigation" aria-label="工作台导航">
        <#if login.role==1>
            <li class="layui-nav-item"><a href="/hospital/admin/index">多标签工作台</a></li>
            <li class="layui-nav-item layui-nav-itemed"><a href="javascript:;">人员</a><dl class="layui-nav-child">
                <dd><a href="/admin/doctorManage">医生管理</a></dd><dd><a href="/admin/patientManage">患者管理</a></dd><dd><a href="/admin/adminManage">管理员管理</a></dd>
            </dl></li>
            <li class="layui-nav-item layui-nav-itemed"><a href="javascript:;">诊疗</a><dl class="layui-nav-child">
                <dd><a href="/admin/appointmentManage">预约管理</a></dd><dd><a href="/admin/medicalhistoryManage">病史管理</a></dd><dd><a href="/admin/hospitalizationManage">住院信息</a></dd><dd><a href="/admin/optionManage">检查科目</a></dd><dd><a href="/admin/illnessManage">疾病管理</a></dd>
            </dl></li>
            <li class="layui-nav-item"><a href="javascript:;">药品</a><dl class="layui-nav-child"><dd><a href="/admin/drugsManage">药品与库存</a></dd></dl></li>
            <li class="layui-nav-item"><a href="javascript:;">财务</a><dl class="layui-nav-child"><dd><a href="/finance/admin">收支、订单与经营</a></dd></dl></li>
        </#if>
        <#if login.role==2>
            <li class="layui-nav-item"><a href="/finance/doctor">接诊统计与订单</a></li>
            <li class="layui-nav-item"><a href="/hospital/doctor/index">医生工作台</a></li>
        </#if>
        <li class="layui-nav-item" <#if login.role!=3>style="display:none"</#if>><a href="/patient/medicalhistory">病史</a></li>
        <li class="layui-nav-item" <#if login.role!=3>style="display:none"</#if>><a href="/patient/hospitalization">住院信息</a></li>
        <li class="layui-nav-item" <#if login.role!=3>style="display:none"</#if>><a href="/patient/appointment">挂号</a></li>
        <li class="layui-nav-item" <#if login.role!=3>style="display:none"</#if>><a href="/patient/search">查询</a></li>
        <li class="layui-nav-item" <#if login.role!=2>style="display:none"</#if>><a href="/doctor/seekMedicalAdvice">就医&nbsp;&nbsp;查看病史</a></li>
    </ul>
    </div>
</div>
