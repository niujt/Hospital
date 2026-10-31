package com.hospital.finance;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/** In-memory, read-only rendering of the already authorized receipt allowlist. */
public final class ReceiptPdf {
    private ReceiptPdf() { }
    public static byte[] render(Map<String,Object> receipt) {
        Document document = new Document(PageSize.A4, 42, 42, 42, 42);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font body = new Font(base, 11);
            PdfWriter writer = PdfWriter.getInstance(document, bytes);
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override public void onEndPage(PdfWriter current, Document page) {
                    ColumnText.showTextAligned(current.getDirectContent(), Element.ALIGN_CENTER,
                            new Phrase("模拟票据，非报销凭证 · 第 " + current.getPageNumber() + " 页", new Font(base, 9)),
                            page.getPageSize().getWidth() / 2, 23, 0);
                }
            });
            document.addTitle("模拟药品购买凭证");
            document.open();
            Paragraph title = new Paragraph("药品购买凭证", new Font(base, 22, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(12);
            document.add(title);
            Paragraph notice = new Paragraph("模拟票据，非报销凭证", new Font(base, 15, Font.BOLD, BaseColor.RED));
            notice.setAlignment(Element.ALIGN_CENTER);
            notice.setSpacingAfter(24);
            document.add(notice);
            line(document, body, "交易编号：" + receipt.get("saleId"));
            String paidAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"))
                    .format(Instant.parse(String.valueOf(receipt.get("issuedAt"))));
            line(document, body, "支付时间：" + paidAt + "（北京时间）");
            Map<?,?> patient = (Map<?,?>) receipt.get("patient");
            line(document, body, "患者：" + patient.get("name"));
            line(document, body, "支付方式：" + ("WALLET".equals(receipt.get("paymentMethod")) ? "余额支付" : "线下现金登记"));
            PdfPTable table = new PdfPTable(new float[]{4,1,2,2});
            table.setWidthPercentage(100);
            table.setHeaderRows(1);
            table.setSplitLate(false);
            table.setSpacingBefore(18);
            table.setSpacingAfter(18);
            for(String label : new String[]{"药品名称", "数量", "单价（元）", "金额（元）"}) cell(table, body, label);
            for(Object value : (List<?>)receipt.get("items")) {
                Map<?,?> item = (Map<?,?>)value;
                for(String key : new String[]{"name", "quantity", "unitPrice", "amount"}) cell(table, body, String.valueOf(item.get(key)));
            }
            document.add(table);
            line(document, body, "购买金额：" + receipt.get("totalAmount") + " 元");
            line(document, body, "已退回余额：" + receipt.get("refundedAmount") + " 元");
            line(document, body, "退款后净额：" + receipt.get("netAmount") + " 元");
            Map<?,?> tax = (Map<?,?>)receipt.get("tax");
            if(tax != null) {
                line(document, body, "演示税率：" + tax.get("rate") + "%" + (Boolean.TRUE.equals(tax.get("configured")) ? "（已配置）" : "（未配置）"));
                line(document, body, "含税购买金额：" + tax.get("grossAmount") + " 元；不含税金额：" + tax.get("netAmount") + " 元；税额：" + tax.get("taxAmount") + " 元");
                line(document, body, "退款不含税金额：" + tax.get("refundedNetAmount") + " 元；退款税额：" + tax.get("refundedTaxAmount") + " 元");
                line(document, body, "剩余不含税金额：" + tax.get("remainingNetAmount") + " 元；剩余税额：" + tax.get("remainingTaxAmount") + " 元");
                line(document, body, FinanceService.TAX_NOTICE);
            }
            String status = String.valueOf(receipt.get("refundStatus"));
            line(document, body, "退款状态：" + ("FULL".equals(status) ? "已全部退款" : "PARTIAL".equals(status) ? "已部分退款" : "未退款"));
            Paragraph footer = new Paragraph("本凭证仅用于系统演示与购买记录核对，不是税务发票，不可用于报销。\n药品名称取当前档案；数量、单价和购买金额取支付时记录。退款信息为本次查询状态。", new Font(base, 10));
            footer.setSpacingBefore(28);
            document.add(footer);
        } catch (Exception ex) {
            throw new IllegalStateException("购买凭证生成失败", ex);
        } finally {
            document.close();
        }
        return bytes.toByteArray();
    }
    private static void line(Document document, Font font, String text) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setSpacingAfter(7);
        document.add(paragraph);
    }
    private static void cell(PdfPTable table, Font font, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(9);
        table.addCell(cell);
    }
}
