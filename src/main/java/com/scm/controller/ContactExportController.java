package com.scm.controller;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import com.scm.Repositories.ContactRepo;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.Services.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user/contacts")
@RequiredArgsConstructor
public class ContactExportController {

    private final ContactRepo contactRepo;
    private final UserService userService;

    // ==========================
    // EXCEL EXPORT
    // ==========================
    @GetMapping("/export/excel")
    public void exportExcel(
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        String email = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(email);

        List<Contact> contacts = contactRepo.findByUser(user);

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=contacts.xlsx");

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Contacts");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Name");
        header.createCell(1).setCellValue("Email");
        header.createCell(2).setCellValue("Phone");
        header.createCell(3).setCellValue("Category");

        int rowNum = 1;

        for (Contact c : contacts) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(c.getName());

            row.createCell(1).setCellValue(c.getEmail());

            row.createCell(2).setCellValue(c.getPhoneNumber());

            row.createCell(3).setCellValue(
                    c.getCategory() != null
                            ? c.getCategory()
                            : "General");
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // ==========================
    // PDF EXPORT
    // ==========================
    @GetMapping("/export/pdf")
    public void exportPdf(
            HttpServletResponse response,
            Authentication authentication) throws Exception {

        String email = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(email);

        List<Contact> contacts = contactRepo.findByUser(user);

        response.setContentType("application/pdf");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=contacts.pdf");

        Document document = new Document(PageSize.A4);

        PdfWriter.getInstance(
                document,
                response.getOutputStream());

        document.open();

        document.add(new Paragraph("Smart Contact Manager"));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("My Contacts"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);

        table.addCell("Name");
        table.addCell("Email");
        table.addCell("Phone");
        table.addCell("Category");

        for (Contact c : contacts) {

            table.addCell(
                    c.getName() != null ? c.getName() : "");

            table.addCell(
                    c.getEmail() != null ? c.getEmail() : "");

            table.addCell(
                    c.getPhoneNumber() != null
                            ? c.getPhoneNumber()
                            : "");

            table.addCell(
                    c.getCategory() != null
                            ? c.getCategory()
                            : "General");
        }

        document.add(table);

        document.close();
    }
}