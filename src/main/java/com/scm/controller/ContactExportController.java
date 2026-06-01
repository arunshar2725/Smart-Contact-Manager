package com.scm.controller;

import com.scm.Repositories.ContactRepo;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.Services.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/user/contacts")
@RequiredArgsConstructor
public class ContactExportController {

    private final ContactRepo contactRepo;
    private final UserService userService;

    @GetMapping("/export/excel")
    public void exportExcel(
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        String username = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

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

        workbook.write(response.getOutputStream());

        workbook.close();
    }
}