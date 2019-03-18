package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.entity.Hospitalization;
import com.hospital.service.HospitalizationService;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HospitalizationController {
    @Autowired
    HospitalizationService hospitalizationService;
    @Autowired
    PatientService patientService;
    @RequestMapping("/admin/hospitalizationManage")
    public String hospitalizationManage(HttpServletRequest request){
        request.setAttribute("hospitalizations",hospitalizationService.getAllHospitalizations());
        return "admin/hospitalizationManage";
    }
    @RequestMapping("/admin/hospitalizationAdd")
    public String hospitalizationAddPage(HttpServletRequest request){
        request.setAttribute("patients",patientService.getAllPatients());
        return"admin/add/hospitalizationadd";
    }
    @RequestMapping(value = "/admin/hospitalization",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject hospitalizationAdd(@RequestBody Hospitalization hospitalization){
        JSONObject json=new JSONObject();
        json.put("message",hospitalizationService.AddHospitalization(hospitalization));
        return json;
    }
    @RequestMapping(value = "/admin/hospitalization/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject delHospitalization(@PathVariable Integer id){
        JSONObject json=new JSONObject();
        json.put("message",hospitalizationService.deleteHospitalization(id));
        return json;
    }
}
