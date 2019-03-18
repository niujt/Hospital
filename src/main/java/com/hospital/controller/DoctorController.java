package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DoctorController {
    @Autowired
    DoctorService doctorService;
    @RequestMapping("/admin/doctorManage")
    public String doctorManage(HttpServletRequest request){
        request.setAttribute("doctors",doctorService.getAllDoctor());
        return "admin/doctorManage";
    }
    @RequestMapping(value = "/admin/doctor/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject delDoctor(@PathVariable Integer id){
        JSONObject json=new JSONObject();
        json.put("message",doctorService.delDoctor(id));
        return json;
    }
}
