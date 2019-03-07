package com.hospital.controller;

import com.hospital.service.DrugsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
