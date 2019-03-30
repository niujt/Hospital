package com.hospital.uitls;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

public class ExcelUtils {
    public static void createTitle(HSSFWorkbook workbook, HSSFSheet sheet){
        HSSFRow row = sheet.createRow(0);
        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
        sheet.setColumnWidth(1,12*256);
        sheet.setColumnWidth(3,17*256);

        //设置为居中加粗
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);

        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("序号");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("楼层号");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("门牌号");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("床号");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("患者");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("患病");
        cell.setCellStyle(style);

        cell = row.createCell(6);
        cell.setCellValue("住院时间");
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue("出院时间");
        cell.setCellStyle(style);
    }

    //生成excel文件
    public static void buildExcelFile(String filename,HSSFWorkbook workbook) throws Exception{
        FileOutputStream fos = new FileOutputStream(filename);
        workbook.write(fos);
        fos.flush();
        fos.close();
    }

    //浏览器下载excel
    public static void buildExcelDocument(String filename, HSSFWorkbook workbook, HttpServletResponse response) throws Exception{
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(filename, "utf-8"));
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
