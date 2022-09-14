<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>就医</title>
    <link rel="stylesheet" href="/static/css/layui.min.css">
</head>
<body>
<div class="layui-tab">
    <ul class="layui-tab-title">
        <li class="layui-this">诊断</li>
        <li >开药</li>
        <li>入院手续</li>
    </ul>
    <div class="layui-tab-content">
        <div class="layui-tab-item layui-show">
            <form class="layui-form" id="form1" onsubmit="return seekinfo()">
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
                    <tr>
                        <td colspan="2">
                            <button class="layui-btn" type="submit">确诊</button>
                        </td>
                    </tr>
                </table>
            </form>

        </div>
        <div class="layui-tab-item">
            <form class="layui-form" id="form2" onsubmit="return drug()">
                <table class="layui-table">
                    <tr>
                        <td>患者名称</td>
                        <td><input type="text" value="${patient.name}" readonly class="layui-input">
                            <input type="hidden" value="${patient.id}" name="patientid" readonly class="layui-input">
                        </td>
                    </tr>
                    <tr>
                        <td>配药</td>
                        <td>
                            <#list drugs as drug><input type="checkbox">${drug.name}&nbsp;&nbsp;
                               数量：<input type="number" min="0" name="${drug.id+"_number"}" class="layui-input"><br></#list>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2"><button class="layui-btn" type="submit">配药</button></td>
                    </tr>
                </table>
            </form>
        </div>
        <div class="layui-tab-item">
            <form class="layui-form" id="form3"  onsubmit="return zation()">
                <table class="layui-table">
                    <colgroup>
                        <col width="150" >
                        <col width="200" >
                        <col width="200" >
                        <col width="200">
                        <col width="200">
                        <col width="200">
                        <col width="200">
                        <col width="200">
                        <col>
                    </colgroup>
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
                    <tr>
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
<script src="/static/js/layui.all.js"></script>
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
        $.ajax({
            url:"/doctor/seekinfo",
            type:'post',
            data:json,
            contentType: "application/json;charset=utf-8",
            success:function(data){
                console.log(data.message);
                alert(data.message);
                parent.location.reload();

            },
            error:function (data) {
                layer.msg(data.message, {
                    time: 2000, //2s后自动关闭
                });
            }
        });
        return false
    }
    function drug(){
        var d = {};
        var t = $('#form2').serializeArray();
        $.each(t, function() {
            d[this.name] = this.value;
        });
        var json=JSON.stringify(d);
        $.ajax({
            url:"/doctor/drug",
            type:'put',
            data:json,
            contentType: "application/json;charset=utf-8",
            success:function(data){
                console.log(data.message);
                alert(data.message);
                // if(data.message=="已存在"){
                //     $("#message").text(data.message);
                // }
                // else {
                    parent.location.reload();
                // }
            },
            error:function (data) {
                layer.msg(data.message, {
                    time: 2000, //2s后自动关闭
                });
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
        $.ajax({
            url:"/doctor/zation",
            type:'post',
            data:json,
            contentType: "application/json;charset=utf-8",
            success:function(data){
                console.log(data.message);
                alert(data.message);
                // if(data.message=="已存在"){
                //     $("#message").text(data.message);
                // }
                // else {
                parent.location.reload();
                // }
            },
            error:function (data) {
                layer.msg(data.message, {
                    time: 2000, //2s后自动关闭
                });
            }
        });
        return false
    }
</script>
</body>
</html>