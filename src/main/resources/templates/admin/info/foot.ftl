<script src="/static/js/layui.all.js"></script>
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
        $.ajax({
            url:url,
            type:'put',
            data:json,
            contentType: "application/json;charset=utf-8",
            success:function(data){
                console.log(data.message);
                if(data.message=="已存在"){
                    $("#message").text(data.message);
                }
                else {
                    alert(data.message);
                    parent.location.reload();
                }
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