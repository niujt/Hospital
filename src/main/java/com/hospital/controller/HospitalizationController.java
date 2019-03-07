package com.hospital.controller;

import com.hospital.service.HospitalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HospitalizationController {
    @Autowired
    HospitalizationService hospitalizationService;
    @RequestMapping("/admin/hospitalizationManage")
    public String hospitalizationManage(HttpServletRequest request){
        request.setAttribute("hospitalizations",hospitalizationService.getAllHospitalizations());
        return "admin/hospitalizationManage";
    }
}
