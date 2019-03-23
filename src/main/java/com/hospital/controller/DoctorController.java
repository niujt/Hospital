package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.entity.Doctor;
import com.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping(value = "/admin/doctor/{id}",method = RequestMethod.GET)
    public String doctorInfo(@PathVariable Integer id,HttpServletRequest request){
        request.setAttribute("doctor",doctorService.getDoctor(id));
        return "admin/info/doctorinfo";
    }
    @RequestMapping(value = "/admin/doctor",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject AddDoctor(@RequestBody Doctor doctor){
        JSONObject json=new JSONObject();
        json.put("message",doctorService.addDoctor(doctor));
        return json;
    }
    @RequestMapping(value = "/admin/doctor",method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject updateDoctor(@RequestBody Doctor doctor){
        JSONObject json=new JSONObject();
        json.put("message",doctorService.upDoctor(doctor));
        return json;
    }
    @RequestMapping("/admin/doctorAdd")
    public String doctorAddPage(){
        return "admin/add/doctoradd";
    }
}
