(function(a){function d(b){var c=b||window.event,d=[].slice.call(arguments,1),e=0,f=!0,g=0,h=0;return b=a.event.fix(c),b.type="mousewheel",c.wheelDelta&&(e=c.wheelDelta/120),c.detail&&(e=-c.detail/3),h=e,c.axis!==undefined&&c.axis===c.HORIZONTAL_AXIS&&(h=0,g=-1*e),c.wheelDeltaY!==undefined&&(h=c.wheelDeltaY/120),c.wheelDeltaX!==undefined&&(g=-1*c.wheelDeltaX/120),d.unshift(b,e,g,h),(a.event.dispatch||a.event.handle).apply(this,d)}var b=["DOMMouseScroll","mousewheel"];if(a.event.fixHooks)for(var c=b.length;c;)a.event.fixHooks[b[--c]]=a.event.mouseHooks;a.event.special.mousewheel={setup:function(){if(this.addEventListener)for(var a=b.length;a;)this.addEventListener(b[--a],d,!1);else this.onmousewheel=d},teardown:function(){if(this.removeEventListener)for(var a=b.length;a;)this.removeEventListener(b[--a],d,!1);else this.onmousewheel=null}},a.fn.extend({mousewheel:function(a){return a?this.bind("mousewheel",a):this.trigger("mousewheel")},unmousewheel:function(a){return this.unbind("mousewheel",a)}})})(KunyiLibrary);;(function($){var selectors=[];var check_binded=false;var check_lock=false;var defaults={interval:250,force_process:false};var $window=$(window);var $prior_appeared;function process(){check_lock=false;for(var index=0;index<selectors.length;index++){var $appeared=$(selectors[index]).filter(function(){return $(this).is(':appeared')});$appeared.trigger('appear',[$appeared]);if($prior_appeared){var $disappeared=$prior_appeared.not($appeared);$disappeared.trigger('disappear',[$disappeared])}$prior_appeared=$appeared}}$.expr[':']['appeared']=function(element){var $element=$(element);if(!$element.is(':visible')){return false}var window_left=$window.scrollLeft();var window_top=$window.scrollTop();var offset=$element.offset();var left=offset.left;var top=offset.top;if(top+$element.height()>=window_top&&top-($element.data('appear-top-offset')||0)<=window_top+$window.height()&&left+$element.width()>=window_left&&left-($element.data('appear-left-offset')||0)<=window_left+$window.width()){return true}else{return false}};$.fn.extend({appear:function(options){var opts=$.extend({},defaults,options||{});var selector=this.selector||this;if(!check_binded){var on_check=function(){if(check_lock){return}check_lock=true;window.setTimeout(process,opts.interval)};$(window).scroll(on_check).resize(on_check);check_binded=true}if(opts.force_process){window.setTimeout(process,opts.interval)}selectors.push(selector);return $(selector)}});$.extend({force_appear:function(){if(check_binded){process();return true};return false}})})(KunyiLibrary);;(function($){$.fn.scrollToFade=function(){$(this).each(function(){$(this).addClass('be-animating-hide')})};$(window).on('scroll',function(){var win_st=$(this).scrollTop();$('.be-animating-hide').each(function(){var offset=$(this).offset().top;var opacity=0.7-((win_st-offset)/$(this).height());$(this).css('opacity',opacity)})})}(KunyiLibrary));;(function($){var $window=$(window);var windowHeight=$window.height();$window.resize(function(){windowHeight=$window.height()});$.fn.parallax=function(xpos,speedFactor,outerHeight){var $this=$(this);var getHeight;var firstTop;var paddingTop=0;$this.each(function(){firstTop=$this.offset().top});if(outerHeight){getHeight=function(jqo){return jqo.outerHeight(true)}}else{getHeight=function(jqo){return jqo.height()}}if(arguments.length<1||xpos===null)xpos="50%";if(arguments.length<2||speedFactor===null)speedFactor=0.5;if(arguments.length<3||outerHeight===null)outerHeight=true;function update(){var pos=$window.scrollTop();$this.each(function(){var $element=$(this);var top=$element.offset().top;var height=getHeight($element);if(top+height<pos||top>pos+windowHeight){return}$this.css('backgroundPosition',xpos+" "+Math.round((firstTop-pos)*speedFactor)+"px")})}$window.bind('scroll',update).resize(update);update()}})(KunyiLibrary);;!function(a){a.fn.slide=function(b){return a.fn.slide.defaults={type:"slide",effect:"fade",autoPlay:!1,delayTime:500,interTime:2500,triggerTime:150,defaultIndex:0,titCell:".hd li",mainCell:".bd",targetCell:null,trigger:"mouseover",scroll:1,vis:1,titOnClassName:"on",autoPage:!1,prevCell:".prev",nextCell:".next",pageStateCell:".pageState",opp:!1,pnLoop:!0,easing:"swing",startFun:null,endFun:null,switchLoad:null,playStateCell:".playState",mouseOverStop:!0,defaultPlay:!0,returnDefault:!1},this.each(function(){var c=a.extend({},a.fn.slide.defaults,b),d=a(this),e=c.effect,f=a(c.prevCell,d),g=a(c.nextCell,d),h=a(c.pageStateCell,d),i=a(c.playStateCell,d),j=a(c.titCell,d),k=j.size(),l=a(c.mainCell,d),m=l.children().size(),n=c.switchLoad,o=a(c.targetCell,d),p=parseInt(c.defaultIndex),q=parseInt(c.delayTime),r=parseInt(c.interTime);parseInt(c.triggerTime);var Q,t=parseInt(c.scroll),u=parseInt(c.vis),v="false"==c.autoPlay||0==c.autoPlay?!1:!0,w="false"==c.opp||0==c.opp?!1:!0,x="false"==c.autoPage||0==c.autoPage?!1:!0,y="false"==c.pnLoop||0==c.pnLoop?!1:!0,z="false"==c.mouseOverStop||0==c.mouseOverStop?!1:!0,A="false"==c.defaultPlay||0==c.defaultPlay?!1:!0,B="false"==c.returnDefault||0==c.returnDefault?!1:!0,C=0,D=0,E=0,F=0,G=c.easing,H=null,I=null,J=null,K=c.titOnClassName,L=j.index(d.find("."+K)),M=p=-1==L?p:L,N=p,O=p,P=m>=u?0!=m%t?m%t:t:0,R="leftMarquee"==e||"topMarquee"==e?!0:!1,S=function(){a.isFunction(c.startFun)&&c.startFun(p,k,d,a(c.titCell,d),l,o,f,g)},T=function(){a.isFunction(c.endFun)&&c.endFun(p,k,d,a(c.titCell,d),l,o,f,g)},U=function(){j.removeClass(K),A&&j.eq(N).addClass(K)};if("menu"==c.type)return A&&j.removeClass(K).eq(p).addClass(K),j.hover(function(){Q=a(this).find(c.targetCell);var b=j.index(a(this));I=setTimeout(function(){switch(p=b,j.removeClass(K).eq(p).addClass(K),S(),e){case"fade":Q.stop(!0,!0).animate({opacity:"show"},q,G,T);break;case"slideDown":Q.stop(!0,!0).animate({height:"show"},q,G,T)}},c.triggerTime)},function(){switch(clearTimeout(I),e){case"fade":Q.animate({opacity:"hide"},q,G);break;case"slideDown":Q.animate({height:"hide"},q,G)}}),B&&d.hover(function(){clearTimeout(J)},function(){J=setTimeout(U,q)}),void 0;if(0==k&&(k=m),R&&(k=2),x){if(m>=u)if("leftLoop"==e||"topLoop"==e)k=0!=m%t?(0^m/t)+1:m/t;else{var V=m-u;k=1+parseInt(0!=V%t?V/t+1:V/t),0>=k&&(k=1)}else k=1;j.html("");var W="";if(1==c.autoPage||"true"==c.autoPage)for(var X=0;k>X;X++)W+="<li>"+(X+1)+"</li>";else for(var X=0;k>X;X++)W+=c.autoPage.replace("$",X+1);j.html(W);var j=j.children()}if(m>=u){l.children().each(function(){a(this).width()>E&&(E=a(this).width(),D=a(this).outerWidth(!0)),a(this).height()>F&&(F=a(this).height(),C=a(this).outerHeight(!0))});var Y=l.children(),Z=function(){for(var a=0;u>a;a++)Y.eq(a).clone().addClass("clone").appendTo(l);for(var a=0;P>a;a++)Y.eq(m-a-1).clone().addClass("clone").prependTo(l)};switch(e){case"fold":l.css({position:"relative",width:D,height:C}).children().css({position:"absolute",width:E,left:0,top:0,display:"none"});break;case"top":l.wrap('<div class="tempWrap" style="overflow:hidden; position:relative; height:'+u*C+'px"></div>').css({top:-(p*t)*C,position:"relative",padding:"0",margin:"0"}).children().css({height:F});break;case"left":l.wrap('<div class="tempWrap" style="overflow:hidden; position:relative; width:'+u*D+'px"></div>').css({width:m*D,left:-(p*t)*D,position:"relative",overflow:"hidden",padding:"0",margin:"0"}).children().css({"float":"left",width:E});break;case"leftLoop":case"leftMarquee":Z(),l.wrap('<div class="tempWrap" style="overflow:hidden; position:relative; width:'+u*D+'px"></div>').css({width:(m+u+P)*D,position:"relative",overflow:"hidden",padding:"0",margin:"0",left:-(P+p*t)*D}).children().css({"float":"left",width:E});break;case"topLoop":case"topMarquee":Z(),l.wrap('<div class="tempWrap" style="overflow:hidden; position:relative; height:'+u*C+'px"></div>').css({height:(m+u+P)*C,position:"relative",padding:"0",margin:"0",top:-(P+p*t)*C}).children().css({height:F})}}var $=function(a){var b=a*t;return a==k?b=m:-1==a&&0!=m%t&&(b=-m%t),b},_=function(b){var c=function(c){for(var d=c;u+c>d;d++)b.eq(d).find("img["+n+"]").each(function(){var b=a(this);if(b.attr("src",b.attr(n)).removeAttr(n),l.find(".clone")[0])for(var c=l.children(),d=0;d<c.size();d++)c.eq(d).find("img["+n+"]").each(function(){a(this).attr(n)==b.attr("src")&&a(this).attr("src",a(this).attr(n)).removeAttr(n)})})};switch(e){case"fade":case"fold":case"top":case"left":case"slideDown":c(p*t);break;case"leftLoop":case"topLoop":c(P+$(O));break;case"leftMarquee":case"topMarquee":var d="leftMarquee"==e?l.css("left").replace("px",""):l.css("top").replace("px",""),f="leftMarquee"==e?D:C,g=P;if(0!=d%f){var h=Math.abs(0^d/f);g=1==p?P+h:P+h-1}c(g)}},ab=function(a){if(!A||M!=p||a||R){if(R?p>=1?p=1:0>=p&&(p=0):(O=p,p>=k?p=0:0>p&&(p=k-1)),S(),null!=n&&_(l.children()),o[0]&&(Q=o.eq(p),null!=n&&_(o),"slideDown"==e?(o.not(Q).stop(!0,!0).slideUp(q),Q.slideDown(q,G,function(){l[0]||T()})):(o.not(Q).stop(!0,!0).hide(),Q.animate({opacity:"show"},q,function(){l[0]||T()}))),m>=u)switch(e){case"fade":l.children().stop(!0,!0).eq(p).animate({opacity:"show"},q,G,function(){T()}).siblings().hide();break;case"fold":l.children().stop(!0,!0).eq(p).animate({opacity:"show"},q,G,function(){T()}).siblings().animate({opacity:"hide"},q,G);break;case"top":l.stop(!0,!1).animate({top:-p*t*C},q,G,function(){T()});break;case"left":l.stop(!0,!1).animate({left:-p*t*D},q,G,function(){T()});break;case"leftLoop":var b=O;l.stop(!0,!0).animate({left:-($(O)+P)*D},q,G,function(){-1>=b?l.css("left",-(P+(k-1)*t)*D):b>=k&&l.css("left",-P*D),T()});break;case"topLoop":var b=O;l.stop(!0,!0).animate({top:-($(O)+P)*C},q,G,function(){-1>=b?l.css("top",-(P+(k-1)*t)*C):b>=k&&l.css("top",-P*C),T()});break;case"leftMarquee":var c=l.css("left").replace("px","");0==p?l.animate({left:++c},0,function(){l.css("left").replace("px","")>=0&&l.css("left",-m*D)}):l.animate({left:--c},0,function(){l.css("left").replace("px","")<=-(m+P)*D&&l.css("left",-P*D)});break;case"topMarquee":var d=l.css("top").replace("px","");0==p?l.animate({top:++d},0,function(){l.css("top").replace("px","")>=0&&l.css("top",-m*C)}):l.animate({top:--d},0,function(){l.css("top").replace("px","")<=-(m+P)*C&&l.css("top",-P*C)})}j.removeClass(K).eq(p).addClass(K),M=p,y||(g.removeClass("nextStop"),f.removeClass("prevStop"),0==p&&f.addClass("prevStop"),p==k-1&&g.addClass("nextStop")),h.html("<span>"+(p+1)+"</span>/"+k)}};A&&ab(!0),B&&d.hover(function(){clearTimeout(J)},function(){J=setTimeout(function(){p=N,A?ab():"slideDown"==e?Q.slideUp(q,U):Q.animate({opacity:"hide"},q,U),M=p},300)});var bb=function(a){H=setInterval(function(){w?p--:p++,ab()},a?a:r)},cb=function(a){H=setInterval(ab,a?a:r)},db=function(){z||(clearInterval(H),bb())},eb=function(){(y||p!=k-1)&&(p++,ab(),R||db())},fb=function(){(y||0!=p)&&(p--,ab(),R||db())},gb=function(){clearInterval(H),R?cb():bb(),i.removeClass("pauseState")},hb=function(){clearInterval(H),i.addClass("pauseState")};if(v?R?(w?p--:p++,cb(),z&&l.hover(hb,gb)):(bb(),z&&d.hover(hb,gb)):(R&&(w?p--:p++),i.addClass("pauseState")),i.click(function(){i.hasClass("pauseState")?gb():hb()}),"mouseover"==c.trigger?j.hover(function(){var a=j.index(this);I=setTimeout(function(){p=a,ab(),db()},c.triggerTime)},function(){clearTimeout(I)}):j.click(function(){p=j.index(this),ab(),db()}),R){if(g.mousedown(eb),f.mousedown(fb),y){var ib,jb=function(){ib=setTimeout(function(){clearInterval(H),cb(0^r/10)},150)},kb=function(){clearTimeout(ib),clearInterval(H),cb()};g.mousedown(jb),g.mouseup(kb),f.mousedown(jb),f.mouseup(kb)}"mouseover"==c.trigger&&(g.hover(eb,function(){}),f.hover(fb,function(){}))}else g.click(eb),f.click(fb)})}}(KunyiLibrary);eval(function(p,a,c,k,e,d){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--){d[e(c)]=k[c]||e(c)}k=[function(e){return d[e]}];e=function(){return'\\w+'};c=1};while(c--){if(k[c]){p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c])}}return p}('(5($){$.J.L=5(r){8 1={d:0,A:0,b:"h",v:"N",3:4};6(r){$.D(1,r)}8 m=9;6("h"==1.b){$(1.3).p("h",5(b){8 C=0;m.t(5(){6(!$.k(9,1)&&!$.l(9,1)){$(9).z("o")}j{6(C++>1.A){g B}}});8 w=$.M(m,5(f){g!f.e});m=$(w)})}g 9.t(5(){8 2=9;$(2).c("s",$(2).c("i"));6("h"!=1.b||$.k(2,1)||$.l(2,1)){6(1.u){$(2).c("i",1.u)}j{$(2).K("i")}2.e=B}j{2.e=x}$(2).T("o",5(){6(!9.e){$("<V />").p("X",5(){$(2).Y().c("i",$(2).c("s"))[1.v](1.Z);2.e=x}).c("i",$(2).c("s"))}});6("h"!=1.b){$(2).p(1.b,5(b){6(!2.e){$(2).z("o")}})}})};$.k=5(f,1){6(1.3===E||1.3===4){8 7=$(4).F()+$(4).O()}j{8 7=$(1.3).n().G+$(1.3).F()}g 7<=$(f).n().G-1.d};$.l=5(f,1){6(1.3===E||1.3===4){8 7=$(4).I()+$(4).U()}j{8 7=$(1.3).n().q+$(1.3).I()}g 7<=$(f).n().q-1.d};$.D($.P[\':\'],{"Q-H-7":"$.k(a, {d : 0, 3: 4})","R-H-7":"!$.k(a, {d : 0, 3: 4})","S-y-7":"$.l(a, {d : 0, 3: 4})","q-y-7":"!$.l(a, {d : 0, 3: 4})"})})(W);',62,62,'|settings|self|container|window|function|if|fold|var|this||event|attr|threshold|loaded|element|return|scroll|src|else|belowthefold|rightoffold|elements|offset|appear|bind|left|options|original|each|placeholder|effect|temp|true|of|trigger|failurelimit|false|counter|extend|undefined|height|top|the|width|fn|removeAttr|lazyload|grep|show|scrollTop|expr|below|above|right|one|scrollLeft|img|KunyiLibrary|load|hide|effectspeed'.split('|'),0,{}));
;(function($){
	$.fn.extend({
		ChangeUrl:function(options){
		  var settings={
			   addCss:"",
			   removeCss:"",
			   IsEmpty:true,
			   loadPage:"",
			   IsShowLoadingImage:true
			  };
		  options=$.extend(settings,options);
		  var element=this;
		  element=options.IsEmpty?element.empty():element;
		  element=options.addCss != ""?element.addClass(options.addCss):element;
		  element=options.removeCss != ""?element.removeClass(options.removeCss):element;
		  element=options.loadPage != ""?element.load(options.loadPage,function(responseTxt,statusTxt,xhr){
				if(statusTxt=="success"){				
				}				
		  }):element;
		  return this;
		}
	});   
})(KunyiLibrary);var Request=new Object();
Kunyi = {
    imgReSize: function (dom) {
        if (!dom) return;
        var img = $(dom);
        var imgWidth = img.width();
        var imgHeight = img.height();
        var resizeWidth, resizeHeight;
        function reSize() {
            var screenWidth = $__W.width();
            var screenHeight = $__W.height();
            if (((imgWidth * screenHeight) / imgHeight) < screenWidth) {
                resizeWidth = screenWidth;
                resizeHeight = (imgHeight * screenWidth) / imgWidth;
            } else if (((imgHeight * screenWidth) / imgWidth) < screenHeight) {
                resizeWidth = (imgWidth * screenHeight) / imgHeight;
                resizeHeight = screenHeight;
            }
            resizeMarginTop = -(resizeHeight / 2);
            resizeMarginLeft = -(resizeWidth / 2);
            img.css({ top: screenHeight / 2, left: screenWidth / 2, width: resizeWidth, height: resizeHeight, marginTop: resizeMarginTop, marginLeft: resizeMarginLeft })
        }
        $__D.ready(reSize);
        $__W.resize(reSize);
    },
    imageResize: function (dom) {
        if (!dom) return;
        var img = $(dom);
        var parent = img.parent();
        var imgWidth = img.width();
        var imgHeight = img.height();
        var resizeWidth, resizeHeight;
        function reSize() {
            var screenWidth = parent.width();
            var screenHeight = parent.height();
            if (((imgWidth * screenHeight) / imgHeight) < screenWidth) {
                resizeWidth = screenWidth;
                resizeHeight = (imgHeight * screenWidth) / imgWidth;
            } else if (((imgHeight * screenWidth) / imgWidth) < screenHeight) {
                resizeWidth = (imgWidth * screenHeight) / imgHeight;
                resizeHeight = screenHeight;
            }
            resizeMarginTop = -(resizeHeight / 2);
            resizeMarginLeft = -(resizeWidth / 2);
            img.css({ top: screenHeight / 2, left: screenWidth / 2, width: resizeWidth, height: resizeHeight, marginTop: resizeMarginTop, marginLeft: resizeMarginLeft })
        }
        $__D.ready(reSize);
        $__W.resize(reSize);
    },
    Msg: {
        ShowTipsMsg: function (msg, time, type) {
            if (type == 1) {
                Kunyi.Msg.MsgTips(time, msg, 518, 'success'); //头部提示,1、success 2、error 3、warning
            } else if (type == 2) {
                Kunyi.Msg.MsgTips(time, msg, 518, 'error'); //头部提示,1、success 2、error 3、warning
            } else if (type == 3) {
                Kunyi.Msg.MsgTips(time, msg, 518, 'warning'); //头部提示,1、success 2、error 3、warning 
            }
        },
        MsgTips: function (timeOut, msg, speed, type) {
            $(".tip_container").remove();
            var bid = parseInt(Math.random() * 100000);
            $("body").prepend('<div id="tip_container' + bid + '" class="container tip_container"><div id="tip' + bid + '" class="mtip"><span id="tsc' + bid + '"></span></div></div>');
            var $this = $(this);
            var $tip_container = $("#tip_container" + bid);
            var $tip = $("#tip" + bid);
            var $tipSpan = $("#tsc" + bid);
            //先清楚定时器
            clearTimeout(window.timer);
            //主体元素绑定事件
            $tip.attr("class", type).addClass("mtip");
            $tipSpan.html(msg);
            $tip_container.slideDown(speed);
            //提示层隐藏定时器
            window.timer = setTimeout(function () {
                $tip_container.slideUp(speed);
                $(".tip_container").remove();
            }, timeOut);
            //鼠标移到提示层时清除定时器
            $tip_container.on("mouseover", function () {
                clearTimeout(window.timer);
            });
            //鼠标移出提示层时启动定时器
            $tip_container.on("mouseout", function () {
                window.timer = setTimeout(function () {
                    $tip_container.slideUp(speed);
                    $(".tip_container").remove();
                }, timeOut / 2);
            });
            $tip_container.css({ "left": ($__W.width() - $tip_container.width()) / 2
		, "top": ($__W.height() - $tip_container.height()) / 2
            });
        }
    },
    cls: function (event) {
        event = event ? event : window.event;
        var obj = event.srcElement ? event.srcElement : event.target;
        with (obj)
            if (value == defaultValue) value = "";
    },
    res: function (event) {
        event = event ? event : window.event;
        var obj = event.srcElement ? event.srcElement : event.target;
        with (obj)
            if (value == "") value = defaultValue;
    }, CheckSearchForm: function () {
        var obj = $("input#SearchWords");
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
    }, AddFavorite: function (sURL, sTitle) {
        try {
            window.external.addFavorite("" + sURL + "", "" + sTitle + "");
        }
        catch (e) {
            try {
                if (window.sidebar) {
                    window.sidebar.addPanel("" + sTitle + "", "" + sURL + "", "");
                } else {
                    alert("加入收藏失败，该浏览器不支持自动加入收藏功能，请使用Ctrl+D进行添加。");
                }
            }
            catch (e) {
                alert("加入收藏失败，该浏览器不支持自动加入收藏功能，请使用Ctrl+D进行添加。");
            }
        }
    }, InitFun: function () {
        $("div#Loading").fadeOut(1288, "easeOutQuart", function () { $(this).remove(); });
        window.setTimeout(function () {
            $("header .toolBase").animate({ top: "0px" }, 1188, "easeOutQuint");
        }, 588);
    }, IsFinish: false, ToTop: function () {
        $("html,body").stop().animate({ scrollTop: 0 }, 888, "easeOutQuint");
    }, BaseMark: $(".BaseMark").eq(0), Index: {}
};
$(function(){
	$("nav .nav .PMenu").hover(function(){
		$t=$(this);
		$obj=$t.find(".navboxBase");
		$obj.css({"left":-$t.offset().left+"px","width":$("body").width()});
		if($IsLoser){
			$obj.show();
		}else{
		$obj.stop(true,false).fadeIn(588,"easeOutQuart");
		}
	},function(){
	    $(this).find('.navboxBase').hide(); 
	});
	$(".zfk a.top").click(function(){
		$("html,body").stop().animate({scrollTop: 0}, 888,"easeOutQuart");
	});
});
$__W.resize(function(){
});
$__W.load(function(){
	Kunyi.IsFinish=true;
	Kunyi.InitFun();
	$("img.load").lazyload({
		placeholder: "js/grey.gif",
		effect: "fadeIn"
	});
});