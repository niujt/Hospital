package com.hospital.controller;

import com.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
