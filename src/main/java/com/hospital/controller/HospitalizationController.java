package com.hospital.controller;

import com.alibaba.fastjson.JSONObject;
import com.hospital.entity.Hospitalization;
import com.hospital.service.HospitalizationService;
import com.hospital.service.PatientService;
import com.hospital.uitls.ExcelUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class HospitalizationController {
    @Autowired
    HospitalizationService hospitalizationService;
    @Autowired
    PatientService patientService;
    @RequestMapping("/admin/hospitalizationManage")
    public String hospitalizationManage(HttpServletRequest request,@RequestParam(value = "patientname",required = false)String patientname,@RequestParam(value = "intime",required = false)String intime){
        request.setAttribute("patientname",patientname);
        request.setAttribute("intime",intime);
        request.setAttribute("hospitalizations",hospitalizationService.getAllHospitalizations(patientname,intime));
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
    @RequestMapping(value = "/admin/hospitalization/{id}",method = RequestMethod.GET)
    public String hospitalizationInfo(HttpServletRequest request,@PathVariable Integer id){
        request.setAttribute("h",hospitalizationService.getHospitalization(id));
        request.setAttribute("patients",patientService.getAllPatients());
        return"admin/info/hospitalizationinfo";
    }
    @RequestMapping(value = "/admin/hospitalization",method = RequestMethod.PUT)
    @ResponseBody
    public JSONObject delHospitalization(@RequestBody Hospitalization hospitalization){
        JSONObject json=new JSONObject();
        json.put("message",hospitalizationService.updateHospitalization(hospitalization));
        return json;
    }
    //生成user表excel
    @GetMapping(value = "/admin/getHospitalization")
    public String getUser(HttpServletResponse response) throws Exception{
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("统计表");
        ExcelUtils.createTitle(workbook,sheet);
        List<Hospitalization> rows = hospitalizationService.getAllHospitalizations();

        //设置日期格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));

        //新增数据行，并且设置单元格数据
        int rowNum=1;
        for(Hospitalization hospitalization:rows){
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(hospitalization.getFloor());
            row.createCell(2).setCellValue(hospitalization.getBed());
            row.createCell(3).setCellValue(hospitalization.getDoor());
            row.createCell(4).setCellValue(hospitalization.getPatientname());
            row.createCell(5).setCellValue(hospitalization.getMedicalname());
            HSSFCell cell1 = row.createCell(6);
            HSSFCell cell2 = row.createCell(7);
            cell1.setCellValue(hospitalization.getIntime());
            cell2.setCellValue(hospitalization.getOuttime());
            cell1.setCellStyle(style);
            cell2.setCellStyle(style);
            rowNum++;
        }

        String fileName = "住院信息.xls";

        //生成excel文件
        //ExcelUtils.buildExcelFile(fileName, workbook);

        //浏览器下载excel
        ExcelUtils.buildExcelDocument(fileName,workbook,response);

        return "download excel";
    }
}
