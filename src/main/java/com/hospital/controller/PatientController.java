package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.entity.Patient;
import com.hospital.service.*;
import com.hospital.uitls.PatientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Controller
public class PatientController {
    @Autowired
    PatientService patientService;
    @Autowired
    DoctorService doctorService;
    @Autowired
    AppointmentService appointmentService;
    @Autowired
    HospitalizationService hospitalizationService;
    @RequestMapping("/admin/patientManage")
    public String patientlist(HttpServletRequest request){
        request.setAttribute("patients",patientService.getAllPatients());
        return "admin/patientManage";
    }
    @RequestMapping(value = "/admin/patient/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject delPatient(@PathVariable Integer id){
        JSONObject json=new JSONObject();
        json.put("message",patientService.delPatient(id));
        return json;
    }
    @RequestMapping(value = "/admin/patient/{id}",method = RequestMethod.GET)
    public String patientInfo(@PathVariable Integer id,HttpServletRequest request){
        request.setAttribute("patient",patientService.getPatient(id));
        request.setAttribute("appointments",appointmentService.getPatientMessage(id));
        request.setAttribute("hospitalizations",hospitalizationService.getPatientMessage(id));
        request.setAttribute("doctors",doctorService.getAllDoctor());
        return "admin/info/patientinfo";
    }
    @RequestMapping(value = "/admin/patient",method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject patientInfo(@RequestBody Map map,HttpServletRequest request){
        JSONObject json=new JSONObject();
        json.put("message",map);
     //   System.out.println(PatientUtils.getListByJsonMessage(map));
        return json;
    }
}
