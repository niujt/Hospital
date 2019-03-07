package com.hospital.controller;

import com.hospital.service.MedicalhistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MedicalhistoryController {
    @Autowired
    MedicalhistoryService medicalhistoryService;
    @RequestMapping("/admin/medicalhistoryManage")
    public String medicalhistoryManage(HttpServletRequest request){
        request.setAttribute("medicalhistorys",medicalhistoryService.getAllMedicalhistorys());
        return "admin/medicalhistoryManage";
    }
}
