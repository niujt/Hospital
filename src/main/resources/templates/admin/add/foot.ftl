<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<link rel="stylesheet" href="/static/css/hospital-feedback.css?v=20261031-1">
<script src="/static/js/hospital-feedback.js?v=20261031-1"></script>
<script src="/static/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
    layui.use('form', function(){
        var form = layui.form;
        form.render();
    });
    function add(url){
        var d = {};
        var t = $('form').serializeArray();
        $.each(t, function() {
            d[this.name] = this.value;
        });
        var json=JSON.stringify(d);
        HospitalFeedback.request({
            url:url,
            type:'post',
            data:json,
            contentType: "application/json;charset=utf-8",
            success:function(data){
                console.log(data.message);
                if(data.message=="已存在"||data.message=="该身份证已被注册或使用"){
                    HospitalFeedback.result(data.message);
                }
                else {
                    HospitalFeedback.result(data.message, function () { parent.location.reload(); });
                }
            },
            error:function (data) {
                HospitalFeedback.error(data);
            }
        });
        return false
    }
    layui.use(['layer', 'form'], function(){
        var layer = layui.layer
            ,form = layui.form;
        form.on('select(department)', function(data){
            var department=(data.value);
             HospitalFeedback.request({
                 type: 'GET',
                 url: '/doctor/'+department,
                 dataType:  'json',
                 success: function(data){
                     $("#doctorid").empty();
                     data=data.doctors;
                    data.forEach(function(item){
                        var name=item.name;
                        console.log(name);
                        var id=item.id;
                        console.log(id);
                        $("#doctorid").append("<option value="+id+">"+name+"</option>");
                    });
                     form.render('select');
                 }
             });
        });

    });
</script>
<script src="/static/js/hospital-drafts.js"></script>
<script src="/static/js/hospital-modal-form.js?v=20261031-2"></script>
