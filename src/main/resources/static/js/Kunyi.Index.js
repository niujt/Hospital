$(function () {
    $(".focusBox").slide({ titCell: ".num li", mainCell: ".pic", effect: "leftLoop", vis: 1, autoPlay: true, interTime: 6288, easing: "easeInOutQuint", delayTime: 888, startFun: function (i, c) {
       $("#BannerInfo").text($(".pic li:not(.clone)").eq(i).find("a").attr("title"));
    }, endFun: function (i, c) { } 
    });
	/*
	
	$("#BannerSwitch").click(function(e){
	  $t=$(this);
	  if($t.data("type")==0){	  
	  $(".bannerBase .tools").stop().animate({"right":(Kunyi.BaseMark.width()>=1200?"-220px":"-256px")},588,"easeOutQuart",function(){ $t.addClass("hidden");$t.data("type",1);$t.find("span").text("展开");});
	  }else{		
		$(".bannerBase .tools").stop().animate({"right":(Kunyi.BaseMark.width()>=1200?"36px":"0px")},588,"easeOutQuart",function(){ $t.removeClass("hidden");$t.data("type",0);$t.find("span").text("隐藏");});  
	  }
	});*/
$(".expertBase .prevNext .prevBtn").click(function () {
    $obj = $(".expertBase .experts ul");
		$current = parseInt($obj.data("num"), 10);
		$Index = $current - 1;
		$LiSize=$obj.find("li").size();
		$BaseWidth=Kunyi.BaseMark.width();
		$Size =0;
		$w=0;
		$plus=0;
		if($BaseWidth>=1200&&$BaseWidth<1500){
			$Size= Math.ceil($LiSize/8);
			$w=(122+32)*8;
		}else if($BaseWidth>=980&&$BaseWidth<1200){
			$Size= Math.ceil($LiSize/7);
			$w=(122+22)*7;
		}else{
			$Size= Math.ceil($LiSize/6);
			$w=(240+12)*6;
			$plus=30;
		}
		if ($Index >= 0) {
            $obj.data("num", $Index);
            $obj.stop().animate({
                left: (- $w * $Index+$plus) + "px"
            }, 888, "easeOutQuart");
        }else{
			$obj.data("num", ($Size-1));
            $obj.stop().animate({
                left: (- $w * ($Size-1)+$plus) + "px"
            }, 888, "easeOutQuart");
		}
	});
    $(".expertBase .prevNext .nextBtn").click(function () {
        $obj = $(".expertBase .experts ul");
		$current = parseInt($obj.data("num"), 10);
		$Index = $current + 1;
		$LiSize=$obj.find("li").size();
		$BaseWidth=Kunyi.BaseMark.width();
		$Size =0;
		$w=0;
		$plus=0;
		if($BaseWidth>=1200&&$BaseWidth<1500){
			$Size= Math.ceil($LiSize/8);
			$w = (122 + 32) * 8;
		}else if($BaseWidth>=980&&$BaseWidth<1200){
			$Size= Math.ceil($LiSize/7);
			$w=(122+21)*7;
		}else{			
			$Size= Math.ceil($LiSize/6);
			$w=(240+12)*6;
			$plus=30;
		}
		 if ($Index < $Size) {
            $obj.data("num", $Index);
            $obj.stop().animate({
                left: (- $w * $Index+$plus) + "px"
            }, 888, "easeOutQuart");
        }else{
			$obj.data("num", 0);
            $obj.stop().animate({
                left: (- $w * 0+$plus) + "px"
            }, 888, "easeOutQuart");
	    }
	});
	
	
	/*$("#DSearchSwitch").click(function(){
		if($IsLoser){
			$(".doctorsBase .DFastSearch").show();
		}else{
		$(".doctorsBase .DFastSearch").stop(false,true).fadeIn(888, "easeOutQuart");
		}
	});
	$(".doctorsBase .DFastSearch button").click(function(){
		if($IsLoser){
			$(".doctorsBase .DFastSearch").hide();
		}else{
		$(".doctorsBase .DFastSearch").stop(false,true).fadeOut(888, "easeOutQuart");
		}
	});
	$("#OSearchSwitch").click(function(){
		if($IsLoser){
			$(".officesBase .OFastSearch").show();
		}else{
		$(".officesBase .OFastSearch").stop(false,true).fadeIn(888, "easeOutQuart");
		}
	});
	$(".officesBase .OFastSearch button").click(function(){
		if($IsLoser){
			$(".officesBase .OFastSearch").hide();
		}else{
		  $(".officesBase .OFastSearch").stop(false,true).fadeOut(888, "easeOutQuart");
		}
	});
	$(".officesBase .tools .Aprev").click(function(){
		 $obj=$(".officesBase .contents ul");
		$current = parseInt($obj.data("num"), 10);
		$Index = $current - 1;
		$LiSize=$obj.find("li").size();
		$BaseWidth=Kunyi.BaseMark.width();
		$Size =0;
		$w=0;
		$plus=0;
		if($BaseWidth>=1200&&$BaseWidth<1500){
			$Size= Math.ceil($LiSize/4);
			$w=300*4;
			$plus=30;
		}else if($BaseWidth>=980&&$BaseWidth<1200){
			$Size= Math.ceil($LiSize/3);
			$w=326*3;
			$plus=17;
		}else{			
			$Size= Math.ceil($LiSize/5);
			$w=300*5;
			$plus=30;
		}
		Kunyi.Index.hiddenLine.hide();
		if ($Index >= 0) {
            $obj.data("num", $Index);
            $obj.stop().animate({
                left: (- $w * $Index+$plus) + "px"
            }, 888, "easeOutQuart",function(){Kunyi.Index.hiddenLine.show();});
        }else{
			$obj.data("num", ($Size-1));
            $obj.stop().animate({
                left: (- $w * ($Size-1)+$plus) + "px"
            }, 888, "easeOutQuart",function(){Kunyi.Index.hiddenLine.show();});
		}
	});
	$(".officesBase .tools .Anext").click(function(){
		$obj=$(".officesBase .contents ul");
		$current = parseInt($obj.data("num"), 10);
		$Index = $current + 1;
		$LiSize=$obj.find("li").size();
		$BaseWidth=Kunyi.BaseMark.width();
		$Size =0;
		$w=0;
		$plus=0;
		if($BaseWidth>=1200&&$BaseWidth<1500){
			$Size= Math.ceil($LiSize/4);
			$w=300*4;
			$plus=30;
		}else if($BaseWidth>=980&&$BaseWidth<1200){
			$Size= Math.ceil($LiSize/3);
			$w=326*3;
			$plus=17;
		}else{			
			$Size= Math.ceil($LiSize/5);
			$w=300*5;
			$plus=30;
		}
		Kunyi.Index.hiddenLine.hide();
		 if ($Index < $Size) {
            $obj.data("num", $Index);
            $obj.stop().animate({
                left: (- $w * $Index+$plus) + "px"
            }, 888, "easeOutQuart",function(){Kunyi.Index.hiddenLine.show();});
        }else{
			$obj.data("num", 0);
            $obj.stop().animate({
                left: (- $w * 0+$plus) + "px"
            }, 888, "easeOutQuart",function(){Kunyi.Index.hiddenLine.show();});
	    }
	});
	$(".othersBase .left .img").slide({titCell:".index li",mainCell:".Anim",effect:"leftLoop",vis:1,autoPlay:true,interTime:5188,easing:"easeOutQuint",delayTime:688, startFun: function(i,c){	
	},endFun: function(i, c){}});
	window.setTimeout(function(){		  
		  $.getScript("js/Kunyi.Doctors.js",function(){
			   $DListBase=$(".doctorsBase .DFastSearch .DListBase");
			    $(Kunyi.Doctors).each(function(i) {
					var strTemp="<div class=\"DList\" id=\"DList_"+this.Char+"\"><h6>"+this.Char+"</h6><ul>";
					   $(this.Doctors).each(function(j) {						   
strTemp+="<li><div class='DName'><a href='Doctor-"+this.DID+".html' target='_blank' class='name'>"+this.DName+"</a><div><a href='Doctor-"+this.DID+".html' target='_blank'><img src='images/nophoto.gif' data-image='"+this.DImage+"'  width='99' height='133' /></a><label>"+this.DName+"</label><em>"+this.OfficeName+"</em><span>"+this.Position+"</span><a href='Doctor-"+this.DID+".html' target='_blank' class='more'>详细</a> <a href='NewsDetail-449.html' target='_blank' class='date'>预约</a></div></div><p class='animated'>"+this.OfficeName+"</p></li>";
					   });
					 strTemp+="</ul><div class=\"clear\"></div></div>";
					 $DListBase.append(strTemp);
				});
				$DListBase.append("<p><br/></p>");
	    $(".doctorsBase .DFastSearch .DName").hover(function(e){
			 $t=$(this);
			 $p=$t.parent();
			 $obj=$t.find("div");
			$val=Kunyi.Index.DPosition($p.position().top,$p.position().left,$p.parent().parent().width());
			$obj.css({top:$val[0]+"px",left:$val[1]+"px"});
			$img=$obj.find("img")
			$img.attr("src",$img.data("image"));
			$obj.show();
		},function(){
			$t=$(this);
			$obj=$t.find("div");
			$obj.hide();
		});
		$(".doctorsBase .DFastSearch .IndexWords li").click(function(){
			$t=$(this);
			$obj=$(".doctorsBase .DFastSearch .DListBase");
			$h=$("#DList_" + $t.text()).offset().top-$obj.offset().top+$obj.scrollTop();
			$obj.stop().animate({scrollTop: $h}, 666);
		});
	});	
	$.getScript("js/Kunyi.Offices.js",function(){
		$OListBase=$(".officesBase .OListBase");
		var plus=0,strTemp="",num=0;
		  $(Kunyi.Offices).each(function(i){			 
			  var Height=288,tH=40,iH=23,h=15;			
			  if(i==0){
				  strTemp+="<div class='OList'><h6>"+this.DepName+"</h6>";plus+=tH;num++;
			  }else{
				  if(Height-plus>(tH+iH+h)){
				  strTemp+="<div class='itemHidden'></div><h6>"+this.DepName+"</h6>";plus+=(tH+h);
				  }else{
					   strTemp+="</div><div class='OList'><h6>"+this.DepName+"</h6>";plus=0; plus+=tH;num++;
				  }
			  }
			$(this.Offices).each(function(j){
					 if(Height-plus>=(iH-5)){
			strTemp+="<div class='item'><span>> </span><a href='Office-"+this.OID+".html' target='_blank'>"+this.OName+"</a></div>";plus+=iH;
					 }else{
			plus=0;
			strTemp+="</div><div class='OList'><div class='item'><span>> </span><a href='Office-"+this.OID+".html' target='_blank'>"+this.OName+"</a></div>";plus+=iH;num++;
					 }					
				});
			}); 
			strTemp+="</div>";
			$OListBase.css("width",(num*240)+"px");
			$OListBase.append(strTemp);
			$OListBase.append("<div class='clear'></div>");
	});
	},1288);*/
	$(".indexNews .notice .tab-hd li").mouseover(function(e){
		$t=$(this);
		if(!$t.hasClass("active")){
			$objs=$t.siblings();
			$objs.removeClass("active");
			$t.addClass("active");
		    $objsA=$(".indexNews .notice .tab-bd");
			$(".indexNews .notice .tab-bd.active").hide().removeClass("active");
			$objsA.eq($t.index()).stop(false,true).fadeIn(588,"easeOutQuart");
			$objsA.eq($t.index()).addClass("active")
		}
	});
});
$__W.resize(function(){
   /*if(Kunyi.BaseMark.width()>=1200){
	   Kunyi.QuickMenuBase.data("type",1);
	 $("nav .quickMenus").show();   
   }else{Kunyi.QuickMenuBase.data("type",0);
	 $("nav .quickMenus").hide();
   }
   $(".doctorsBase .contents ul,.officesBase .contents ul").removeAttr("style").data("num","0");
   $("#BannerSwitch").removeClass("hidden").data("type",0);
   $("#BannerSwitch span").text("隐藏");
   $(".bannerBase .tools").removeAttr("style");*/
    $(".expertBase .experts ul").removeAttr("style").data("num", "0");
});
$__W.load(function(){
	
});