package com.hospital.uitls;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

public class ExcelUtils {
    public static void createTitle(XSSFWorkbook workbook, Sheet sheet){
        // Preserve the legacy XLS font metrics when moving to OOXML defaults.
        workbook.getFontAt(0).setFontName("Arial");
        workbook.getFontAt(0).setFontHeightInPoints((short) 10);
        Row row = sheet.createRow(0);
        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
        sheet.setColumnWidth(1,12*256);
        sheet.setColumnWidth(3,17*256);

        //设置为居中加粗
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);

        Cell cell;
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
    public static void buildExcelFile(String filename,XSSFWorkbook workbook) throws Exception{
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            workbook.write(fos);
        }
    }

    //浏览器下载excel
    public static void buildExcelDocument(String filename, XSSFWorkbook workbook, HttpServletResponse response) throws Exception{
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String encoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename=\"hospitalization.xlsx\"; filename*=UTF-8''" + encoded);
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
