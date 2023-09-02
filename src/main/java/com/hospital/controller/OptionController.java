package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.entity.Option;
import com.hospital.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class OptionController {
    @Autowired
    OptionService optionService;
    @RequestMapping("/admin/optionManage")
    public String optionManage(HttpServletRequest request){
        request.setAttribute("options",optionService.getAll());
        return "/admin/optionManage";
    }
    @RequestMapping(value = "/admin/option/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public JSONObject delOption(@PathVariable Integer id){
        JSONObject json=new JSONObject();
        json.put("message",optionService.deleteOption(id));
        return json;
    }
    @RequestMapping(value = "/admin/option",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject addOption(@RequestBody Option option){
        JSONObject json=new JSONObject();
        json.put("message",optionService.insert(option));
        return json;
    }
    @RequestMapping("/admin/optionAdd")
    public String optionAddPage(){
        return  "/admin/add/optionadd";
    }
    @RequestMapping(value = "/admin/option/{id}",method = RequestMethod.GET)
    public String drugInfo(HttpServletRequest request,@PathVariable Integer id) {
        request.setAttribute("option",optionService.getOption(id));
        return  "/admin/info/optioninfo";
    }
    @RequestMapping(value = "/admin/option",method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject updateOption(@RequestBody Option option) {
        JSONObject json=new JSONObject();
        json.put("message",optionService.updateBOption(option));
        return  json;
    }
}
