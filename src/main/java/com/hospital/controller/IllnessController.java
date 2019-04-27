package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.entity.Illness;
import com.hospital.service.IllnessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IllnessController {
    @Autowired
    IllnessService illnessService;
    @RequestMapping("/admin/illnessManage")
    public String illnessManage(HttpServletRequest request){
        request.setAttribute("illnesses",illnessService.getAll());
        return "/admin/illnessManage";
    }
    @RequestMapping(value = "/admin/illness/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject delIllness(@PathVariable Integer id){
        JSONObject json=new JSONObject();
        json.put("message",illnessService.deleteIllness(id));
        return json;
    }
    @RequestMapping(value = "/admin/illness",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject addDrug(@RequestBody Illness illness){
        JSONObject json=new JSONObject();
        json.put("message",illnessService.insert(illness));
        return json;
    }
    @RequestMapping("/admin/illnessAdd")
    public String illnessAddPage(){
        return  "/admin/add/illnessadd";
    }
    @RequestMapping(value = "/admin/illness/{id}",method = RequestMethod.GET)
    public String illnessInfo(HttpServletRequest request,@PathVariable Integer id) {
        request.setAttribute("illness",illnessService.getIllness(id));
        return  "/admin/info/illnessinfo";
    }
    @RequestMapping(value = "/admin/illness",method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject updateIllness(@RequestBody Illness illness) {
        JSONObject json=new JSONObject();
        json.put("message",illnessService.updateIllness(illness));
        return  json;
    }
}
