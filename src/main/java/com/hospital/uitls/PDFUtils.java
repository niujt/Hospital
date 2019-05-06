package com.hospital.uitls;

import com.hospital.dao.OptionMapper;
import com.hospital.entity.Appointment;
import com.hospital.entity.Option;
import com.hospital.entity.Seek;
import com.hospital.service.OptionService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PDFUtils {
    private static BaseFont bf;//创建字体
    private static Font font;

    static {
        try {
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            font = new Font(bf, 12);//使用字体
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    public static void main(String[] args) {
//        Appointment appointment = new Appointment();
//        appointment.setId(1);
//        appointment.setDepartment("dadadad");
//        appointment.setDoctorname("小明");
//        appointment.setPatientname("小王");
//        appointment.setExpenses(new BigDecimal("23.6"));
//        appointment.setTime(new Date());
//        createAppointMent(appointment);
//        createSeekInfo(new Seek("浑身不舒服","花柳病","1,2,3,4,5",12,new BigDecimal("32.6"),"张安"));
//    }
    public static String createSeekInfo(Seek seek, OptionService optionService, String path) {
        List ids=PatientDoctorutils.getOptionIds(seek.getOptions());
        List<Option> options=new ArrayList<>();
        ids.forEach(id->{
          Option option =optionService.getOption((Integer)id);
            options.add(option);
        });
        Document document = new Document();
        try {
            String str="";
            for(int i=0;i<options.size();i++){
                str+=options.get(i).getName()+"----"+options.get(i).getType()+"("+options.get(i).getPrice()+"元)\n";
            }
            PdfWriter.getInstance(document, new FileOutputStream(path+seek.getPatientname()+DateUtils.date2String(new Date())+"就诊单.pdf"));
            document.open();
            PdfPTable pdfPTable = new PdfPTable(4);
            createCell("诊断书", 4, pdfPTable, font);
            createCell("患者姓名:", 2, pdfPTable, font);
            createCell(seek.getPatientname(), 2, pdfPTable, font);
            createCell("患者描述病情", 4, pdfPTable, font);
            createCell(seek.getDescribes(), 4, pdfPTable, font);
            createCell("初步诊断病情:", 2, pdfPTable, font);
            createCell(seek.getIllname(), 2, pdfPTable, font);
            createCell("需要检查的项目", 4, pdfPTable, font);
            createCell(str, 4, pdfPTable, font);
            createCell("诊断人:", 2, pdfPTable, font);
            createCell(seek.getDoctorname(), 2, pdfPTable, font);
            createCell("是否需要住院:", 2, pdfPTable, font);
            if (seek.getDays() > 0) {
                createCell(seek.getDays() + "天", 2, pdfPTable, font);
            } else {
                createCell("不需要", 2, pdfPTable, font);
            }
            createCell("总计:", 2, pdfPTable, font);
            createCell(seek.getPrice() + "  (元)", 2, pdfPTable, font);
            document.add(pdfPTable);
            document.close();
            return "已生成";
        } catch (Exception e) {
            e.printStackTrace();
            return "系统内部错误，生成失败";
        }
    }

    public static String createAppointMent(Appointment appointment,String path) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path+appointment.getPatientname()+DateUtils.date2String(new Date())+"挂号单.pdf"));
            document.open();
            PdfPTable pdfPTable = new PdfPTable(4);
            createCell("挂号单", 4, pdfPTable, font);
            createCell("预约号码:", 2, pdfPTable, font);
            createCell(appointment.getId() + "", 2, pdfPTable, font);
            createCell("患者姓名:", 2, pdfPTable, font);
            createCell(appointment.getPatientname(), 2, pdfPTable, font);
            createCell("预约科室:", 2, pdfPTable, font);
            createCell(appointment.getDepartment(), 2, pdfPTable, font);
            createCell("预约医生:", 2, pdfPTable, font);
            createCell(appointment.getDoctorname(), 2, pdfPTable, font);
            createCell("门诊费:", 2, pdfPTable, font);
            createCell(appointment.getExpenses() + "  (元)", 2, pdfPTable, font);
            createCell("预约时间:", 2, pdfPTable, font);
            createCell(date2String(appointment.getTime()), 2, pdfPTable, font);
            document.add(pdfPTable);
            document.close();
            return "已生成";
        } catch (Exception e) {
            e.printStackTrace();
            return "系统内部错误，生成失败";
        }
    }

    private static String date2String(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY年MM月dd日");
        return sdf.format(date);
    }

    private static void createCell(String text, int colspan, PdfPTable pdfPTable, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setColspan(colspan);
        pdfPTable.addCell(cell);

    }
}
