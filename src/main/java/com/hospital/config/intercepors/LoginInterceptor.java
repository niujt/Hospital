package com.hospital.config.intercepors;

import com.hospital.entity.Login;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
@Component
public class LoginInterceptor implements HandlerInterceptor {
 
    //这个方法是在访问接口之前执行的，我们只需要在这里写验证登陆状态的业务逻辑，就可以在用户调用指定接口之前验证登陆状态了
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //每一个项目对于登陆的实现逻辑都有所区别，我这里使用最简单的Session提取User来验证登陆。
        HttpSession session = request.getSession();
        //这里的User是登陆时放入session的
        Login login = (Login) session.getAttribute("login");
        //如果session中没有user，表示没登陆
        if (login == null){
            //这个方法返回false表示忽略当前请求，如果一个用户调用了需要登陆才能使用的接口，如果他没有登陆这里会直接忽略掉
            //当然你可以利用response给用户返回一些提示信息，告诉他没登陆
            response.sendRedirect("/hospital/login");
            return false;
        }else {
            return true;    //如果session里有login，表示该用户已经登陆，放行，用户即可继续调用自己需要的接口
        }
    }
 
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }
 
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}