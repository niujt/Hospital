package com.hospital.finance;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ReceiptPdfTest {
    static Map<String,Object> fixture() {
        return FinanceService.map("saleId","demo-sale-123","issuedAt","2026-10-31T02:03:04Z","paymentMethod","WALLET",
                "patient",FinanceService.map("name","张***"),"items",Collections.singletonList(FinanceService.map("name","演示药品","quantity",2,"unitPrice","10.00","amount","20.00")),
                "totalAmount","20.00","refundedAmount","5.00","netAmount","15.00","refundStatus","PARTIAL",
                "commission","MUST_NOT_RENDER");
    }
    @Test void chineseA4ReceiptUsesBeijingTimeMaskedNameAndRefundAmounts()throws Exception{
        byte[] pdf=ReceiptPdf.render(fixture());
        assertEquals("%PDF-",new String(pdf,0,5,java.nio.charset.StandardCharsets.US_ASCII));
        PdfReader reader=new PdfReader(pdf);
        try {
            assertEquals(1,reader.getNumberOfPages());
            assertEquals(595,reader.getPageSize(1).getWidth(),1);
            assertEquals(842,reader.getPageSize(1).getHeight(),1);
            String text=PdfTextExtractor.getTextFromPage(reader,1);
            for(String expected:new String[]{"模拟票据，非报销凭证","2026-10-31 10:03:04","北京时间","张***","演示药品","20.00","5.00","15.00","已部分退款","demo-sale-123"})assertTrue(text.contains(expected),expected+" not found in "+text);
            assertFalse(text.contains("MUST_NOT_RENDER"));assertFalse(text.contains("commission"));
        } finally {reader.close();}
    }
    @Test void longChineseTableRepeatsHeadersAndDemoNoticeAcrossPages() throws Exception {
        Map<String,Object> data=fixture();
        java.util.List<Map<String,Object>> rows=new java.util.ArrayList<>();
        for(int i=0;i<150;i++) rows.add(FinanceService.map("name","演示药品明细"+i,"quantity",1,"unitPrice","10.00","amount","10.00"));
        data.put("items",rows);
        PdfReader reader=new PdfReader(ReceiptPdf.render(data));
        try {
            assertTrue(reader.getNumberOfPages()>2);
            for(int page=1;page<=reader.getNumberOfPages();page++) {
                String text=PdfTextExtractor.getTextFromPage(reader,page);
                assertTrue(text.contains("模拟票据，非报销凭证"),"missing notice page "+page);
            }
            assertTrue(PdfTextExtractor.getTextFromPage(reader,2).contains("药品名称"));
        } finally { reader.close(); }
    }
}
