package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.service.DrugsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
