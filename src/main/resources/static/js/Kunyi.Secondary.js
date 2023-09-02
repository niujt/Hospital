Kunyi.CheckSFrom=function(){
	var obj = $("input#SearchWD");
	var str = $.trim(obj.val());
	if (str == "") {
	Kunyi.Msg.ShowTipsMsg('搜索内容不能为空！', '2888', '2');
	obj.focus();
	return false;
	}
	if (str == "请输入关键字") {
	obj.focus();
	return false;
	}
	return true;
}
$(function(){
//	if($("#EMChange a").length>0){
//		$("#EMChange a").click(function(){
//			$t=$(this);
//			if($t.data("type")=="0"){
//				$("#EMChange img").stop().animate({"left":"-5px"},288,"easeOutQuad");
//				$("#FontState").text("12px");
//				$("#DIVContent").css({"font-size":"12px","line-height":"24px"});
//			}else if($t.data("type")=="1"){
//			$("#EMChange img").stop().animate({"left":"40px"},288,"easeOutQuad");
//			$("#FontState").text("14px");
//			$("#DIVContent").css({"font-size":"14px","line-height":"28px"});
//			}else{
//				$("#EMChange img").stop().animate({"left":"86px"},288,"easeOutQuad");
//				$("#FontState").text("16px");
//				$("#DIVContent").css({"font-size":"16px","line-height":"32px"});
//			}
//		});
//	}
	if($("div#anpNews").length>0){
	  $("div#anpNews a.PNBtn").each(function(index, element) {
        if($(this).attr("disabled")=="disabled"){
		  $(this).addClass("None");	
		 }
       });
	    $("div#anpNews a.FLBtn").each(function(index, element) {
         if($(this).attr("disabled")=="disabled"){
		  $(this).addClass("None");	
		 }
       });	  
     }
//	$objFixPart =$("nav .navBase");
//    if ($objFixPart.length > 0) {
//        $Top = $objFixPart.offset().top;
//        $__W.bind('scroll', function() {
//            var scrollTop = $__D.scrollTop();
//            var heg1 = $Top;
//            if (scrollTop <= heg1) {
//                if ($objFixPart.css("position") == "fixed")
//                    $objFixPart.css({
//                        "position": "absolute"
//                    });
//            } else {
//                if ($objFixPart.css('position') == "absolute")
//                    $objFixPart.css({
//                        "position": "fixed"
//                    });
//            }
//        });
//    }
//	
//	$(".baseRight .tellist ul li a").hover(function(){
//		 $(this).next().stop(false,true).fadeIn(588,"easeOutQuart");
//	},function(){
//		$(this).next().stop(false,true).fadeOut(288,"easeInQuart");
//	});
	 
});
$__W.resize(function(){
	
});
$__W.load(function(){
	 if ($.isFunction($.fn.fancybox)){
        $("a#NewsPaperImg").fancybox({
            padding: 0,
            scrollOutside: false,
            width: "98%",
            height: "98%"
        });
		 $("#ddlYears2").change(function(e) {
			$t=$(this);
			var obj=$(this).get(0);
			var year=obj.options[obj.selectedIndex].value;
			$ddlTimes=$("#ddlTimes2");
			if(year!="0"){
				$.ajax({
					type: "post",
					dataType: "json",
					url: "DoPostBack/GetTimesByYear.ashx",
					data: {year:year},
					beforeSend: function() {
					   $ddlTimes.html("<option value=\"0\">选择期数</option>");
					},
					complete:function(){},
					error: function(XMLHttpRequest, textStatus, errorThrown) { 
					},
					success: function(msg){
						for (var i = 0; i < msg.length; i++) {
						$ddlTimes.append("<option value=\""+msg[i].times+"\">第"+msg[i].times+"期</option>");             
						}
					}       
				});		
			}
			else{
			$("#ddlTimes2").html("<option value=\"0\">选择期数</option>");	
			}
		});
		$("#GoToNewspaper2").click(function(){
			if ($("#ddlTimes2").val() != null && $("#ddlTimes2").val() != "0") {
			var str = "Newspaper-"+$("#ddlYears2").val()+"-"+$("#ddlTimes2").val()+".html";
			window.location.href = str;
			}
		});
	 }
//	 if($(".Service .ServiceRight .platform a").length>0){
//		 $(".Service .ServiceRight .platform a").click(function(){
//			 $("#BtnQRMore").click();
//		 });
//	 }
});