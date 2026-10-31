<script src="/static/vendor/layui/2.13.9/layui.js"></script>
<link rel="stylesheet" href="/static/css/hospital-feedback.css?v=20261031-1">
<script src="/static/js/hospital-feedback.js?v=20261031-1"></script>
<script src="../static/js/jquery-3.3.1.min.js"></script>
<script src="/static/js/hospital-modal.js?v=20261031-2"></script>
<script>
    //JavaScript代码区域
    layui.use('element', function(){
        var element = layui.element;

    });
    function showAdd(url) {
        HospitalModal.open({url: url, title: '添加记录'});
    }
    function edit(url, id) {
        HospitalModal.open({url: url + id, title: '编辑详情'});
    }
    function del(url,id) {
        HospitalFeedback.confirm('确认要删除吗？', function() {
            console.log(id);
            HospitalFeedback.request({
                url:url+id,
                type:'delete',
                success:function(data){
                    HospitalFeedback.result(data.message, function () { (window.HospitalWorkspaceEmbedded ? window.location : parent.location).reload(); });
                },
                error:function (data) {
                    HospitalFeedback.error(data);
                }
            });
        });
    }
</script>