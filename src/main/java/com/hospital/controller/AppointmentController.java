package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.entity.Appointment;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class AppointmentController {
    @Autowired
    AppointmentService appointmentService;
    @Autowired
    DoctorService doctorService;
    @Autowired
    PatientService patientService;
    @RequestMapping("/admin/appointmentManage")
    public String appointmentManage(HttpServletRequest request,@RequestParam(value = "doctorname",required = false)String doctorname,@RequestParam(value = "patientname",required = false)String patientname){
        request.setAttribute("doctorname",doctorname);
        request.setAttribute("patientname",patientname);
        List<Appointment> appointmentList=appointmentService.getAllAppointments(doctorname,patientname);
        request.setAttribute("appointments" ,appointmentList);
        return"admin/appointmentManage";
    }
    @RequestMapping("/admin/appointmentAdd")
    public String appointmentAddPage(HttpServletRequest request){
        request.setAttribute("patients",patientService.getAllPatients());
        //request.setAttribute("doctors",doctorService.getAllDoctor());
        return"admin/add/appointmentadd";
    }
    @RequestMapping(value = "/admin/appointment/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject delAppointment(@PathVariable Integer id){
        JSONObject json=new JSONObject();
        json.put("message",appointmentService.delAppointment(id));
        return json;
    }
    @RequestMapping(value = "/admin/appointment/{id}",method = RequestMethod.GET)
    public String AppointmentInfo(@PathVariable Integer id,HttpServletRequest request){
        request.setAttribute("patients",patientService.getAllPatients());
        request.setAttribute("doctors",doctorService.getAllDoctor());
        request.setAttribute("appointment",appointmentService.getAppointment(id));
        return "admin/info/appointmentInfo";
    }
    @RequestMapping(value = "/admin/appointment",method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject AppointmentUpdate(@RequestBody Appointment appointment){
        JSONObject json=new JSONObject();
        json.put("message",appointmentService.UpdateAppointment(appointment));
        return json;
    }
    @RequestMapping(value = "/admin/appointment",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject AppointmentAdd(@RequestBody Appointment appointment){
        JSONObject json=new JSONObject();
        json.put("message",appointmentService.addAppointment(appointment));
        return json;
    }
}
