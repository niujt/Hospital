package com.hospital.controller;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

/** CVE-2025-31672: ambiguous OOXML ZIP entries must fail for stream and file readers. */
class PoiOoxmlSecurityTest {
    @TempDir Path directory;

    private byte[] workbook() throws Exception {
        try (XSSFWorkbook book = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            book.createSheet("统计表").createRow(0).createCell(0).setCellValue("合法内容");
            book.write(out);
            return out.toByteArray();
        }
    }

    private byte[] duplicateEntry(String duplicateName) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(workbook()));
             ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out)) {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                ByteArrayOutputStream content = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) != -1) content.write(buffer, 0, n);
                for (int copy = 0; copy < (entry.getName().equals(duplicateName) ? 2 : 1); copy++) {
                    zip.putArchiveEntry(new ZipArchiveEntry(entry.getName()));
                    zip.write(content.toByteArray());
                    zip.closeArchiveEntry();
                }
            }
        }
        return out.toByteArray();
    }

    @Test void normalXlsxRemainsReadableFromStreamAndFile() throws Exception {
        byte[] bytes = workbook();
        Path file = directory.resolve("normal.xlsx");
        Files.write(file, bytes);
        try (XSSFWorkbook streamBook = new XSSFWorkbook(new ByteArrayInputStream(bytes));
             XSSFWorkbook fileBook = new XSSFWorkbook(file.toFile())) {
            assertEquals("合法内容", streamBook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
            assertEquals("合法内容", fileBook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
        }
    }

    @Test void duplicateWorksheetRejectedFromStream() throws Exception {
        byte[] malicious = duplicateEntry("xl/worksheets/sheet1.xml");
        Exception error = assertThrows(Exception.class, () -> {
            try (XSSFWorkbook ignored = new XSSFWorkbook(new ByteArrayInputStream(malicious))) { }
        });
        assertTrue(messages(error).contains("more than 1 entry with the name"), messages(error));
    }

    @Test void duplicateWorkbookRejectedFromFile() throws Exception {
        Path file = directory.resolve("ambiguous.xlsx");
        Files.write(file, duplicateEntry("xl/workbook.xml"));
        Exception error = assertThrows(Exception.class, () -> {
            try (XSSFWorkbook ignored = new XSSFWorkbook(file.toFile())) { }
        });
        assertTrue(messages(error).contains("more than 1 entry with the name"), messages(error));
    }

    private String messages(Throwable error) {
        StringBuilder result = new StringBuilder();
        for (Throwable cause = error; cause != null; cause = cause.getCause()) result.append(cause.getMessage()).append(' ');
        return result.toString();
    }
}
