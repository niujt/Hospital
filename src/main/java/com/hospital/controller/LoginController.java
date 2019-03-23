package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.entity.Login;
import com.hospital.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    LoginService loginService;
    @RequestMapping(value = "/hospital")
    public String loginAndregist(){
        return "login&regist";
    }
    @RequestMapping("/admin/adminManage")
    public String adminManage(HttpServletRequest request){
        request.setAttribute("admins",loginService.findAllAdmin());
        return "/admin/adminManage";
    }
    @RequestMapping("/index")
    public String adminIndex(){
        return "index";
    }
    @RequestMapping("/admin/admin/{id}")
    public String adminInfo(HttpServletRequest request,@PathVariable Integer id){
        request.setAttribute("login",loginService.getAdmin(id));
        return "/admin/info/admininfo";
    }
    @RequestMapping("/admin/adminAdd")
    public String adminAddPage(){
        return"admin/add/adminadd";
    }
    @RequestMapping(value = "/admin/admin",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject adminAdd(@RequestBody Login login){
        JSONObject json=new JSONObject();
        json.put("message",loginService.addAmin(login));
        return json;
    }
    @RequestMapping(value = "/admin/admin",method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject upAdmin(@RequestBody Login login){
        JSONObject json=new JSONObject();
        json.put("message",loginService.updateAdmin(login));
        return json;
    }
    @RequestMapping(value = "/admin/admin/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject delAdmin(@PathVariable Integer id){
        JSONObject json=new JSONObject();
        json.put("message",loginService.delAdmin(id));
        return json;
    }
    @RequestMapping(value = "/loginout",method = RequestMethod.GET)
    public String loginout(HttpSession session){
        session.removeAttribute("login");
        return "login&regist";
    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject login(@RequestBody Login login,HttpSession session){
        JSONObject json=new JSONObject();
        json.put("message",loginService.login(login));
        session.setAttribute("login",login);
        return json;
    }
}
