<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<link rel="stylesheet" href="/static/css/hospital-feedback.css?v=20261031-1">
<script src="/static/js/hospital-feedback.js?v=20261031-1"></script>
<script src="/static/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
    layui.use('form', function(){
        var form = layui.form;
        form.render();
    });
    function upd(url){
        var d = {};
        var t = $('form').serializeArray();
        $.each(t, function() {
            d[this.name] = this.value;
        });
        var json=JSON.stringify(d);
        HospitalFeedback.request({
            url:url,
            type:'put',
            data:json,
            contentType: "application/json;charset=utf-8",
            success:function(data){
                console.log(data.message);
                if(data.message=="已存在"){
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
</script>
<script src="/static/js/hospital-drafts.js"></script>
<script src="/static/js/hospital-modal-form.js?v=20261031-2"></script>
