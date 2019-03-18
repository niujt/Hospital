package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.entity.Drugs;
import com.hospital.service.DrugsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DrugsController {
    @Autowired
    DrugsService drugsService;
    @RequestMapping("admin/drugsManage")
    public String drugsManage(HttpServletRequest request){
        request.setAttribute("drugs",drugsService.getAllDrugs());
        return "/admin/drugsManage";
    }
    @RequestMapping(value = "/admin/drug/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject delDrug(@PathVariable Integer id){
        JSONObject json=new JSONObject();
        json.put("message",drugsService.delDrug(id));
        return json;
    }
    @RequestMapping(value = "/admin/drug",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject addDrug(@RequestBody Drugs drugs){
        JSONObject json=new JSONObject();
        json.put("message",drugsService.addDrug(drugs));
        return json;
    }
    @RequestMapping("/admin/drugAdd")
    public String drugAddPage(){
        return  "/admin/add/drugadd";
    }
}
