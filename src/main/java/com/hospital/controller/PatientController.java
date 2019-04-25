package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.entity.*;
import com.hospital.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    @Autowired
    MedicalhistoryService medicalhistoryService;
    @RequestMapping("/admin/patientManage")
    public String patientlist(HttpServletRequest request,@RequestParam(value="name",required = false) String name,@RequestParam(value="certId",required = false) String certId){
        request.setAttribute("patients",patientService.getAllPatients(name,certId));
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
    @RequestMapping(value = "/admin/patientAdd",method = RequestMethod.GET)
    public String patientAddPage(){
        return "admin/add/patientadd";
    }
    @RequestMapping(value = "/admin/patient",method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject patientInfo(@RequestBody Patient patient){
        JSONObject json=new JSONObject();
        json.put("message",patientService.updatePatient(patient));
        return json;
    }
    @RequestMapping(value = "/admin/patient",method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject delPatient(@RequestBody Patient patient){
        JSONObject json=new JSONObject();
        json.put("message",patientService.addPatient(patient));
        return json;
    }
    @RequestMapping(value = "/patient/medicalhistory")
    public String medicalhistory(HttpSession session,HttpServletRequest request){
        Login login=(Login)session.getAttribute("login");
        Patient patient=patientService.findPatientByLoginId(login.getId());
        request.setAttribute("medicalhistorys",medicalhistoryService.getMedicalhistoryByPatientId(patient.getId()));
        return "patient/medicalhistory";
    }
    @RequestMapping(value = "/patient/hospitalization")
    public String hospitalization(HttpSession session,HttpServletRequest request){
        Login login=(Login)session.getAttribute("login");
        Patient patient=patientService.findPatientByLoginId(login.getId());
        request.setAttribute("theLast",hospitalizationService.findTheLastHospitalization(patient.getHospitalizationid()));
        Hospitalization hospitalization=new Hospitalization();
        hospitalization.setPatientid(patient.getId());
        hospitalization.setId(patient.getHospitalizationid());
        request.setAttribute("others",hospitalizationService.findOtherHospitalization(hospitalization));
        return "patient/hospitalization";
    }
    @RequestMapping(value = "/patient/appointment")
    public String appointmentInfo(HttpServletRequest request,HttpSession session){
        Login login=(Login)session.getAttribute("login");
        Patient patient=patientService.findPatientByLoginId(login.getId());
        request.setAttribute("patientid",patient.getId());
        request.setAttribute("doctors",doctorService.getAllDoctor());
        return "patient/appointment";
    }
    @RequestMapping(value = "/patient/appointment",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject appointment(@RequestBody Appointment appointment){
        JSONObject json=new JSONObject();
        Patient patient=new Patient();
        String message=appointmentService.addAppointment(appointment);
        patient.setAppointmentid(appointmentService.selectTheLastAppointment(appointment.getPatientid()));
        patient.setId(appointment.getPatientid());
        patientService.updateAppointMent(patient);
        json.put("message",message);
        return json;
    }
    @RequestMapping(value="/patient/search",method=RequestMethod.GET)
    public String search(){
        return "patient/search";
    }
}
