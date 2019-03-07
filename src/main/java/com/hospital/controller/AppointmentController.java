package com.hospital.controller;

import com.hospital.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AppointmentController {
    @Autowired
    AppointmentService appointmentService;
    @RequestMapping("/admin/appointmentManage")
    public String appointmentManage(HttpServletRequest request){
        request.setAttribute("appointments" ,appointmentService.getAllAppointments());
        return"admin/appointmentManage";
    }
}
