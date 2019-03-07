package com.hospital.controller;

import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PatientController {
    @Autowired
    PatientService patientService;
    @RequestMapping("/admin/patientManage")
    public String patientlist(HttpServletRequest request){
        request.setAttribute("patients",patientService.getAllPatients());
        return "admin/patientManage";
    }
}
