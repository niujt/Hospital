<div class="layui-header">
    <div class="layui-logo"><span style="display: none">欢迎进入</span>医疗系统<span <#if login.role!=1>style="display:none"</#if>>后台管理<span></div>
    <!-- 头部区域（可配合layui已有的水平导航） -->
    <ul class="layui-nav layui-layout-left">
        <#--<li class="layui-nav-item"><a href="/hospital/admin/index" <#if login.role!=1>style="display:none"</#if>>首页</a></li>-->
        <#--<li class="layui-nav-item"><a href="/hospital/patient/index" <#if login.role!=2>style="display:none"</#if>>首页</a></li>-->
        <#--<li class="layui-nav-item"><a href="/hospital/doctor/index" <#if login.role!=3>style="display:none"</#if>>首页</a></li>-->
        <li class="layui-nav-item" <#if login.role!=1>style="display:none"</#if>>
            <a href="javascript:">系统管理</a>
            <dl class="layui-nav-child">
                <dd><a href="/admin/doctorManage">医生管理</a></dd>
                <dd><a href="/admin/patientManage">患者管理</a></dd>
                <dd><a href="/admin/drugsManage">药品管理</a></dd>
                <dd><a href="/admin/optionManage">科目查询管理</a></dd>
                <dd><a href="/admin/illnessManage">疾病管理</a></dd>
            </dl>
        </li>
        <li class="layui-nav-item" <#if login.role!=1>style="display:none"</#if>><a href="/admin/appointmentManage">预约管理</a></li>
        <li class="layui-nav-item" <#if login.role!=1>style="display:none"</#if>><a href="/admin/medicalhistoryManage">病史管理</a></li>
        <li class="layui-nav-item" <#if login.role!=1>style="display:none"</#if>><a href="/admin/hospitalizationManage">住院信息管理</a></li>
        <li class="layui-nav-item" <#if login.role!=1>style="display:none"</#if>><a href="/admin/adminManage">管理员管理</a></li>
        <li class="layui-nav-item" <#if login.role!=3>style="display:none"</#if>><a href="/patient/medicalhistory">病史</a></li>
        <li class="layui-nav-item" <#if login.role!=3>style="display:none"</#if>><a href="/patient/hospitalization">住院信息</a></li>
        <li class="layui-nav-item" <#if login.role!=3>style="display:none"</#if>><a href="/patient/appointment">挂号</a></li>
        <li class="layui-nav-item" <#if login.role!=3>style="display:none"</#if>><a href="/patient/search">查询</a></li>
        <li class="layui-nav-item" <#if login.role!=2>style="display:none"</#if>><a href="/doctor/seekMedicalAdvice">就医&nbsp;&nbsp;查看病史</a></li>
    </ul>
    <ul class="layui-nav layui-layout-right">
        <li class="layui-nav-item">
            <a href="javascript:">
                欢迎&nbsp;&nbsp;&nbsp;&nbsp;${login.username}
            </a>
        </li>
        <li class="layui-nav-item"><a href="/loginout">退出登录</a></li>
    </ul>
</div>