package com.hospital.controller;

import com.alibaba.fastjson2.JSONObject;
import com.hospital.web.MessageResponses;
import com.hospital.finance.auth.FinanceAccess;
import com.hospital.finance.auth.FinanceCsrfGuard;
import com.hospital.entity.Login;
import com.hospital.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    LoginService loginService;
    @RequestMapping(value = "/hospital/login")
    public String loginAndregist(){
        return "login&regist";
    }
    @RequestMapping("/admin/adminManage")
    public String adminManage(HttpServletRequest request,@RequestParam(value = "username",required = false)String username){
        request.setAttribute("username",username);
        request.setAttribute("admins",loginService.findAllAdmin(username));
        return "/admin/adminManage";
    }
    @RequestMapping("/admin/admin/{id}")
    public String adminInfo(HttpServletRequest request,@PathVariable Integer id){
        request.setAttribute("admin",loginService.getAdmin(id));
        return "/admin/info/admininfo";
    }
    @RequestMapping("/admin/adminAdd")
    public String adminAddPage(){
        return"admin/add/adminadd";
    }
    @RequestMapping(value = "/admin/admin",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject adminAdd(@RequestBody Login login){
        return MessageResponses.message(loginService.addAmin(login));
    }
    @RequestMapping(value = "/admin/admin",method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject upAdmin(@RequestBody Login login){
        return MessageResponses.message(loginService.updateAdmin(login));
    }
    @RequestMapping(value = "/admin/admin/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject delAdmin(@PathVariable Integer id){
        return MessageResponses.message(loginService.delAdmin(id));
    }
    @RequestMapping(value = "/loginout",method = RequestMethod.GET)
    public String loginout(HttpSession session){
        session.removeAttribute("login");
        session.removeAttribute(FinanceAccess.SESSION_KEY);
        session.removeAttribute(FinanceCsrfGuard.SESSION_KEY);
        return "redirect:/hospital";
    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject login(@RequestBody Login login,HttpSession session){
        // Never let an unsuccessful attempt preserve or forge an authenticated identity.
        session.removeAttribute("login");
        session.removeAttribute(FinanceAccess.SESSION_KEY);
        session.removeAttribute(FinanceCsrfGuard.SESSION_KEY);
        Login credentials = new Login();
        credentials.setUsername(login.getUsername());
        credentials.setPassword(login.getPassword());
        String message = loginService.login(credentials);
        if (credentials.getId() != null && credentials.getRole() != null
                && message != null && message.equals("登录成功" + credentials.getRole())) {
            Login principal = new Login();
            principal.setId(credentials.getId());
            principal.setRole(credentials.getRole());
            principal.setUsername(credentials.getUsername());
            // Rotate the container session identifier after a successful credential check.
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().changeSessionId();
            }
            session.setAttribute("login", principal);
            session.setAttribute(FinanceAccess.SESSION_KEY, principal.getId());
        }
        return MessageResponses.message(message);
    }
    @RequestMapping(value = "/regest",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject regest(@RequestBody Login login){
        return MessageResponses.message(loginService.regist(login));
    }
    @RequestMapping("/hospital")
    public String hospital(){
        return "index";
    }
    @RequestMapping("/hospital/patient/index")
    public String patientIndex(){
        return "/patient/search";
    }
    @RequestMapping("/hospital/doctor/index")
    public String doctorIndex(){
        return "/doctor/index";
    }
    @RequestMapping("/hospital/admin/index")
    public String adminIndex(){
        return "/admin/index";
    }

}
