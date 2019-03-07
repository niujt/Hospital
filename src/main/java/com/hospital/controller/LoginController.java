package com.hospital.controller;

import com.hospital.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    @Autowired
    LoginService loginService;
    @RequestMapping("/admin/adminManage")
    public String adminManage(HttpServletRequest request){
        request.setAttribute("admins",loginService.findAllAdmin());
        return "/admin/adminManage";
    }
}
