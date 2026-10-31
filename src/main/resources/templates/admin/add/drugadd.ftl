<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>新增页面</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
</head>
<body>
<div class="layui-layout layui-layout-admin">
    <!-- 内容主体区域 -->
    <div style="padding: 15px;">
        <form data-modal-form class="layui-form"  data-finance-drug-form="add" onsubmit="return HospitalDrugForm.submit(this)">
            <table class="layui-table">
                <tr>
                    <td style="width: 100px">药品名称：</td>
                    <td>
                        <input type="text" name="name" required  lay-verify="required"  autocomplete="off" class="layui-input">
                    </td>
                </tr>
                <tr>
                    <td>药物类型：</td>
                    <td>
                        <div class="layui-form">
                            <div class="layui-form-item">
                                <select name="type" >
                                    <option value=""></option>
                                    <option value="0">颗粒剂</option>
                                    <option value="1">丸剂</option>
                                    <option value="2">散剂</option>
                                    <option value="3">酊剂</option>
                                    <option value="4">片剂</option>
                                    <option value="5">胶囊剂</option>
                                </select>
                            </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 100px">价格：</td>
                    <td>
                        <input type="text" name="price" required  class="layui-input" >
                    </td>
                </tr>
                <tr>
                    <td style="width: 100px">数量：</td>
                    <td>
                        <span>初始库存 0；保存后请到财务工作台登记进货。</span>
                    </td>
                </tr>
                <tr><td>药物提成（%）：</td><td><input type="number" name="commissionRate" value="0" min="0" max="100" step="0.0001" required class="layui-input"><div class="layui-form-mid layui-word-aux">0–100%，默认 0。仅医生和管理员可见；历史成交提成不随此设置变化。</div><p id="drug-policy-status" role="status">正在加载安全会话…</p></td></tr>
                <tr data-modal-actions>
                    <td>操作：</td>
                    <td>
                        <button type="submit" class="layui-btn">添加</button>&nbsp;&nbsp;&nbsp;&nbsp;<span style="color: red;" id="message"></span>
                    </td>
                </tr>
                <tbody>
                </tbody>
            </table>
        </form>
    </div>
</div>
<#include "foot.ftl"/>
<script src="/static/js/finance-drugs.js"></script>
</body>
</html>