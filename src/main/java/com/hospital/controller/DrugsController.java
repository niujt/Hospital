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
    public String drugsManage(HttpServletRequest request,@RequestParam(value="name",required = false) String name,@RequestParam(value="type",required = false) Integer type){
        request.setAttribute("name",name);
//        request.setAttribute("type",type);
        Drugs drugs=new Drugs();
        drugs.setName(name);
        drugs.setType(type);
        request.setAttribute("drugs",drugsService.getAllDrugs(drugs));
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
    @RequestMapping(value = "/admin/drug/{id}",method = RequestMethod.GET)
    public String drugInfo(HttpServletRequest request,@PathVariable Integer id) {
        request.setAttribute("drug",drugsService.getDrug(id));
        return  "/admin/info/drugsinfo";
    }
    @RequestMapping(value = "/admin/drug",method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject updateDrug(@RequestBody Drugs drugs) {
        JSONObject json=new JSONObject();
        json.put("message",drugsService.updateDrug(drugs));
        return  json;
    }
}
