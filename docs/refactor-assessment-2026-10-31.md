# Hospital 重构评估与第一批改动

## 基线与边界

- 仓库：niujt/Hospital；基线 master `7303f8e722cf15753bfddad3408895e92d39dd42`。
- 云端本地分支：`dev/ai-rebuild`。用户最新要求先看截图再决定；没有执行 push，不合并 master、不发布服务。
- 约 2,557 行 Java，Spring Boot / MyBatis 单体，FreeMarker + Layui 前端。适合渐进重构，不需要为重构而拆微服务。
- 原仓库没有测试。本轮不连接真实数据库，不使用真实患者数据，不改数据库结构或业务规则。

## 优先级

### P1：处方与库存必须作为一个业务用例

[PatientServiceImpl.seek](https://github.com/niujt/Hospital/blob/7303f8e722cf15753bfddad3408895e92d39dd42/src/main/java/com/hospital/service/impl/PatientServiceImpl.java#L85-L109)
在循环中扣库存，随后更新患者/就诊记录。检查的是库存是否大于零而非是否足够本次数量；循环中失败后仍执行后续写入，错误文本可能被覆盖。
现有代码未声明事务边界。这是静态代码发现，未对生产环境实测。

建议：独立处方服务、结构化药品数量输入、正数校验、带库存条件的原子扣减并检查受影响行数；任一失败回滚全部写入。**仅加事务注解不够**，必须同步处理以字符串返回错误仍会提交的问题。
实施前增加库存不足、多药品中途失败、并发扣减和回滚的数据库集成测试。

### P1：预约创建和回写患者预约 ID

[PatientController.appointment](https://github.com/niujt/Hospital/blob/7303f8e722cf15753bfddad3408895e92d39dd42/src/main/java/com/hospital/controller/PatientController.java#L100-L107)
先插入预约，再查询最近预约，最后回写患者；最后一步的结果未参与响应。并发下“最近预约”不是可靠的本次插入 ID。

建议：预约业务服务统一事务，用数据库生成键取得本次 ID，不再查询 max(id)。保留现有前端字段和路由，加入并发及部分失败测试。

### P1：登录不等于业务授权

[LoginInterceptor](https://github.com/niujt/Hospital/blob/7303f8e722cf15753bfddad3408895e92d39dd42/src/main/java/com/hospital/config/intercepors/LoginInterceptor.java#L17-L30)
只检查是否登录；部分患者操作接受请求传入的患者 ID。
建议统一当前用户、角色检查和患者资源归属检查，并测试跨角色、跨患者访问。
这是代码层面的风险提示，不代表已验证可利用漏洞；本轮未改变权限规则。

### P2：搜索条件与类型边界

[PatientDoctorutils](https://github.com/niujt/Hospital/blob/7303f8e722cf15753bfddad3408895e92d39dd42/src/main/java/com/hospital/uitls/PatientDoctorutils.java#L13-L68)
存在患者查询重复、用引用比较空字符串、硬编码占位 ID。应明确“不筛选 / 未匹配 / 匹配到记录”三种状态，再联动 MyBatis XML；只删除占位 ID 可能造成空 IN 语法问题。

[DrugsUtils](https://github.com/niujt/Hospital/blob/7303f8e722cf15753bfddad3408895e92d39dd42/src/main/java/com/hospital/uitls/DrugsUtils.java)
原始 Map 和拼接字符串边界脆弱，空结果执行 substring 会失败。建议逐步引入请求 DTO、输入校验和统一错误响应，避免一次性改变所有页面协议。

### P3：构建与依赖治理

为项目固定 JDK / Maven 构建说明并建立 CI；框架及依赖版本迁移单独成批，先建立测试再升级，不与 UI、事务和响应重构混合。当前 pom 未在本轮更改。

## 已落地：响应去重与回归保护

- 新增 `com.hospital.web.MessageResponses`，统一构建独立的 `JSONObject`。
- 9 个控制器、29 个简单 message-only 端点使用该工厂。
- 保留路由、HTTP 方法、服务方法与参数、返回类型、message 字段、成功/失败文本和 null 语义。
- 没有把包含其他字段或多步骤业务编排的端点强行纳入。
- 116 个显式端点特征用例，覆盖每个端点的中文成功/失败、空字符串和 null，并检查恰好一次服务委托。
- 2 个工厂用例验证实例不共享与 null 键保留。
- 3 个 standalone MockMvc 用例验证代表性 DELETE/POST/PUT 路由、请求体绑定、中文和 null JSON 序列化。此测试不加载登录拦截器，不是权限验证。

## 验证

使用云端 JDK 17.0.21 + Maven 3.9.9，依赖缓存独立于仓库。未修改全局 Java 或 Maven 配置。

1. 未改生产代码时：116 个端点特征用例通过。
2. 从基线 SHA 另行导出源代码，加相同控制器测试：119 个用例通过（包括 3 个 MockMvc 用例）。
3. 重构后：121 个用例通过，零失败、零错误。
4. 独立代码审查：29 处变更无行为差异阻塞；路由及业务编排未改。
5. 未验证：生产/MySQL数据库事务、并发、端到端权限和真实业务数据。不能把本轮单元测试描述为业务系统全面验证。

常规复现命令：`mvn clean test`（需要已安装兼容 JDK / Maven，并能下载 pom 声明的依赖）。

## 当前 UI：原生 Layui 2.13.9

用户随后明确要求“尽量用原生 Layui，不自己写 CSS”，并授权升级最新版。因此，前一轮自定义浅灰主题已撤销；历史提交保留供追溯，不代表当前最终设计。

- 完全删除本轮新增的 workspace.css / workspace.js 与模板引用，医生/管理员不新增自定义主题或样式覆盖；患者端随后获得单独的适度自定义样式许可。
- 使用原生 header / sidebar / nav-tree / grid / card / fieldset / button / table / form / layer 组件；首页保留业务入口，不恢复装饰性大轮播。
- 从旧 Layui 2.4.5 升级到官方稳定版 2.13.9，未使用 3.x alpha。
- 官方 npm 发布包的 SHA512 integrity 已校验，原样 dist 和 MIT LICENSE 放在 `static/vendor/layui/2.13.9`。来源、哈希及兼容说明见该目录 UPSTREAM.md。
- 原有表单字段、接口路径、角色条件、后端业务规则保持；统一新 vendor 路径，清理旧重复加载，裸 layer 调用改用 layui.layer。
- 官方模块 API 兼容性已检查，项目没有 laytpl 模板调用；FreeMarker 模板和后端接口未因库升级更换。
- 不在本轮重写遗留业务页面中的所有内联布局；本轮未新增 inline style。
- 患者采用 ToC 服务界面：独立顶部直达导航、预约/病史/住院入口、响应式查询栅格，不使用后台侧栏。用户明确单独允许患者自定义样式，因此 patient-portal.css 仅在四个患者业务页面引用，全部选择器限定 .patient-portal。
- 医生采用 ToB 工作台：原生后台侧栏、接诊/病史/打印现有流程；管理员继续原生管理界面。没有凭界面区分宣称后端权限已经补齐。
- 旧的自定义主题/mock 检查辅助脚本已移除，保留并扩展为 56 个 FreeMarker 模板语法测试，另新增 2 个 vendor 路径/旧资源清理回归用例。

## 实际运行与验收范围

使用受支持的云端原生终端启动实际重构后的 Spring Boot / Tomcat，隔离 H2 内存数据库中仅包含虚构演示账号和医生记录。未连接生产数据库，未使用 Sites 或用户 Mac。

管理员验收复用已有隔离登录会话；随后患者和医生分别使用独立虚构账号在新版正常登录。已在真实浏览器的 Layui 2.13.9 页面检查：
- 管理首页、侧栏导航及医生列表查询；
- 添加医生 iframe 表单打开、原生下拉框展开与选择；
- 空表单提交被 required 校验拦截，取消后列表仍为三条虚构记录，无业务写入；
- 三张 1189×761 实际截图：管理首页、医生列表、新增医生弹窗。不是静态模板预览或生成图片。

另已验证患者查询医生、预约科室/医生联动、本人病史与住院信息查询，以及医生登录后的关联预约患者列表；未执行预约、诊疗、打印或其他业务写入。患者专属新版在约 500px 窄窗中五个导航可达、卡片和查询控件纵向排列、无整体横向溢出。没有把该窄窗抽查等同于 390px 真机或全面移动验收。

这仍是关键路径抽查；完整业务提交、生产 MySQL、事务并发和全面权限隔离仍需专门验收。

## 最终本地验证

最终源码使用 JDK 17 / Maven 3.9.9 执行 clean verify：179 项测试通过，零失败、零错误，JAR 构建成功。未执行任何 git push；本地 dev/ai-rebuild 分支保留修改，按用户要求先看截图再决定。

## 登录与公开首页改版

按用户追加要求，实际 /hospital 与 /hospital/login 已重做为医院服务入口：清晰的登录、预约、记录、查询与已有指南入口，没有编造医院资质、电话或地址。公开页样式与脚本严格限定其作用域；医护管理端不加载这些样式。

登录页使用登录/注册标签和清晰错误反馈区域，移除硬编码预填账号密码。原登录/注册业务脚本与改动前逐字一致，接口、字段及角色跳转不变。本轮没有修复已发现的注册授权/校验问题。

实际隔离 H2 验收中，全空注册请求仍返回注册成功，这是原有业务缺陷而非前端验证通过。截图覆盖与业务修复分开记录，未连接真实数据库；继续禁止任何远端推送或发布。
