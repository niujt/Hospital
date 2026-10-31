<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>就医</title>
    <link rel="stylesheet" href="/static/vendor/layui/2.13.9/css/layui.css">
</head>
<body>
<div class="layui-tab">
    <ul class="layui-tab-title">
        <li class="layui-this">诊断</li>
        <li>病史</li>
        <li>药品订单</li>
        <li>入院手续</li>
    </ul>
    <div class="layui-tab-content">
        <div class="layui-tab-item layui-show">
            <form class="layui-form" id="form1" data-modal-form data-modal-submit-label="保存诊断" onsubmit="return seekinfo()">
                <table class="layui-table">
                    <tr>
                        <td>患者名称</td>
                        <td><input type="text" value="${patient.name}" readonly class="layui-input">
                            <input type="hidden" value="${patient.id}" name="patientid" readonly class="layui-input">
                        </td>
                    </tr>
                    <tr>
                        <td>患者描述</td>
                        <td><textarea class="layui-input" name="describes"></textarea></td>
                    </tr>
                    <tr>
                        <td>确诊病情</td>
                        <td><input class="layui-input" name="illname" placeholder="请输入得了什么病"></td>
                    </tr>
                    <tr>
                        <td>检查项目</td>
                        <td>
                            <#list options as option><input type="checkbox" name="${"option_"+option_index}" value="${option.id}">${option.name}(${option.price}元)</#list>
                        </td>
                    </tr>
                    <tr>
                        <td>住院天数</td>
                        <td><input name="days" class="layui-input" type="text" min="0" required placeholder="0天则不住院，不需要在填写住院信息"></td>
                    </tr>
                    <tr data-modal-actions>
                        <td colspan="2">
                            <button class="layui-btn" type="submit">确诊</button>
                        </td>
                    </tr>
                </table>
            </form>

        </div>
        <div class="layui-tab-item">
            <iframe id="encounter-history" title="当前患者病史" width="100%" frameborder="0"></iframe>
        </div>
        <div class="layui-tab-item">
            <blockquote class="layui-elem-quote">仅处理本次预约。完成接诊登记后，可创建本次药品订单，由患者本人支付。</blockquote>
            <iframe id="encounter-orders" title="本次接诊与药品订单" width="100%" frameborder="0"></iframe>
        </div>
        <div class="layui-tab-item">
            <form class="layui-form" id="form3" data-modal-form data-modal-submit-label="保存入院"  onsubmit="return zation()">
                <table class="layui-table">
                    <tr>
                        <td style="width: 100px">楼层：</td>
                        <td>
                            <div class="layui-form">
                                <div class="layui-form-item">
                                    <select name="floor" >
                                        <option value=""></option>
                                        <option value="1" >一层</option>
                                        <option value="2" >二层</option>
                                        <option value="3" >三层</option>
                                        <option value="4" >四层</option>
                                        <option value="5" >五层</option>
                                    </select>
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>门牌号：</td>
                        <td>
                            <input type="text" name="door"  required  lay-verify="required"  autocomplete="off" class="layui-input">
                        </td>
                    </tr>
                    <tr>
                        <td>床号：</td>
                        <td>
                            <div class="layui-form">
                                <div class="layui-form-item">
                                    <select name="bed" >
                                        <option value=""></option>
                                        <option value="1" >一号床</option>
                                        <option value="2" >二号床</option>
                                        <option value="3" >三号床</option>
                                        <option value="4" >四号床</option>
                                    </select>
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>患者</td>
                        <td>
                            <input type="text" value="${patient.name}" readonly class="layui-input">
                            <input type="hidden" value="${patient.id}" name="patientid" readonly class="layui-input">
                        </td>
                    </tr>
                    <tr>
                        <td>患病：</td>
                        <td>
                            <input type="text" name="medicalname"  required  lay-verify="required"  autocomplete="off" class="layui-input">
                        </td>
                    </tr>
                    <tr>
                        <td>住院时间：</td>
                        <td>
                            <input type="date" name="intime"  required  lay-verify="required"  autocomplete="off" class="layui-input">
                        </td>
                    </tr>
                    <tr data-modal-actions>
                        <td>操作：</td>
                        <td>
                            <button type="submit" class="layui-btn">入院</button>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<script src="/static/js/jquery-3.3.1.min.js"></script>
<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<link rel="stylesheet" href="/static/css/hospital-feedback.css?v=20261031-1">
<script src="/static/js/hospital-feedback.js?v=20261031-1"></script>
<script>
    //注意：选项卡 依赖 element 模块，否则无法进行功能性操作
    layui.use('element', function(){
        var element = layui.element;

        //…
    });
    function seekinfo(){
        var d = {};
        var t = $('#form1').serializeArray();
        $.each(t, function() {
            d[this.name] = this.value;
        });
        var json=JSON.stringify(d);
        HospitalFeedback.request({
            url:"/doctor/seekinfo",
            type:'post',
            data:json,
            contentType: "application/json;charset=utf-8",
            success:function(data){
                console.log(data.message);
                HospitalFeedback.result(data.message, function () { parent.location.reload(); });

            },
            error:function (data) {
                HospitalFeedback.error(data);
            }
        });
        return false
    }
    function zation(){
        var d = {};
        var t = $('#form3').serializeArray();
        $.each(t, function() {
            d[this.name] = this.value;
        });
        var json=JSON.stringify(d);
        HospitalFeedback.request({
            url:"/doctor/zation",
            type:'post',
            data:json,
            contentType: "application/json;charset=utf-8",
            success:function(data){
                console.log(data.message);
                HospitalFeedback.result(data.message, function () { parent.location.reload(); });

            },
            error:function (data) {
                HospitalFeedback.error(data);
            }
        });
        return false
    }
</script>
<script src="/static/js/hospital-drafts.js"></script>
<script src="/static/js/hospital-modal-form.js?v=20261031-2"></script>
<script src="/static/js/hospital-frame.js"></script>
<script src="/static/js/doctor-encounter.js"></script>
</body>
</html>
