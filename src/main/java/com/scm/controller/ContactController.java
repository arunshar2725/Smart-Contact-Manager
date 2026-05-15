package com.scm.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.scm.Services.ContactService;
import com.scm.Services.ImageService;
import com.scm.Services.UserService;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    // add contact page
    @GetMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);

        return "user/add_contact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
            Authentication authentication, HttpSession session, Model model) {

        // process the form data

        // validate form

        if (result.hasErrors()) {

            // FIX: clear broken multipart state
            contactForm.setContactImage(null);

            result.getAllErrors().forEach(error -> logger.info(error.toString()));

            session.setAttribute("message",
                    Message.builder()
                            .content(result.getAllErrors().get(0).getDefaultMessage())
                            .type(MessageType.red)
                            .build());

            return "redirect:/user/contacts/add";
        }
        String username = Helper.getEmailOfLoggedInUser(authentication);

        // form ---> contact

        User user = userService.getUserByEmail(username);

        // process the contact picture

        // image process

        // upload krne ka code

        /// file url main imageService.uploadImage(contactForm.getContactImage(),
        /// filename)

        /// ye bahar ka h

        // if (contactForm.getContactImage() != null
        // && !contactForm.getContactImage().isEmpty()) {
        // // ✅ Image selected - upload it
        // fileURl = imageService.uploadImage(contactForm.getContactImage(), filename);
        // logger.info("Image uploaded: {}", fileURl);
        // } else {
        // // ✅ No image - use default
        // fileURl = null; // or set a default image URL
        // filename = null;
        // logger.info("No image selected");
        // }

        /// yaha tak

        // save to database
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavourite(contactForm.isFavourite());
        contact.setUser(user);
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            String fileURl = imageService.uploadImage(contactForm.getContactImage(), filename);

            contact.setPicture(fileURl);
            contact.setCloudinaryImagePublicId(filename);
        }

        System.out.println("Contact ID before saving: " + contact.getId());
        System.out.println("CONTACT OBJECT = " + contact);

        System.out.println("CONTACT ID = " + contact.getId());
        contactService.save(contact);

        System.out.println(contactForm);

        // set the contact picture url

        // set message to be displayed on the view

        session.setAttribute("message",
                Message.builder()
                        .content("You have succesfully added a new Contact")
                        .type(MessageType.green)
                        .build());

        return "redirect:/user/contacts/add";
    }

    // view contact
    @RequestMapping
    public String viewContact(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication) {

        // load all the user contacts

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";

    }

    @GetMapping("/favourite")
    public String markFavourite(
            @RequestParam("id") String contactId,
            @RequestParam("favourite") boolean favourite,

            @RequestParam(value = "page", defaultValue = "0") int page,

            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            HttpSession session) {

        Contact contact = contactService.getById(contactId);
        contact.setFavourite(favourite);
        contactService.save(contact);

        session.setAttribute("message",
                Message.builder()
                        .content(favourite ? "⭐ Added to favourites!" : "Removed from favourites!")
                        .type(favourite ? MessageType.green : MessageType.red)
                        .build());

        // FIX: same page par redirect
        return "redirect:/user/contacts?page=" + page + "&size=" + size;

    }

    // search handler

    @RequestMapping("/search")
    public String searchhandler(

            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0" + "") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model,
            Authentication authentication) {

        var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

        logger.info("field {} keyword {} ", contactSearchForm.getField(), contactSearchForm.getValue());

        Page<Contact> pageContact = null;
        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            pageContact = contactService.searchByName(
                    contactSearchForm.getValue(), size, page, sortBy, direction, user);
        }

        else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(
                    contactSearchForm.getValue(), size, page, sortBy, direction, user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhoneNumber(
                    contactSearchForm.getValue(), size, page, sortBy, direction, user);

        }

        logger.info("pageContact {}", pageContact);

        model.addAttribute("contactSearchForm", new ContactSearchForm());

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("contactSearchForm", contactSearchForm);

        return "user/search";

    }

    // delete contact
    @RequestMapping("/delete/{id}")
    public String deleteContact(
            @PathVariable("id") String id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Authentication authentication,
            HttpSession session) {

        System.out.println("Inside delete method");

        // 1. Get the current page elements count from the service to check state
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        Page<Contact> pageContact = contactService.getByUser(user, page, size, "name", "asc");

        logger.info("Attempting to delete contact with ID: '{}'", id);
        // 2. Perform the deletion
        contactService.delete(id);

        // 3. Logic: If only one contact was on the page and it's not the first page
        // (index 0),
        // redirect to the previous page index.
        int redirectPage = page;
        if (pageContact.getNumberOfElements() == 1 && page > 0) {
            redirectPage = page - 1;
        }

        session.setAttribute("message",
                Message.builder()
                        .content("Contact deleted successfully")
                        .type(MessageType.green)
                        .build());

        // Redirect with the corrected page index
        return "redirect:/user/contacts?page=" + redirectPage + "&size=" + size;
    }

    // update contact
    @RequestMapping("/view/{id}")
    public String updateContact(@PathVariable("id") String id, Model model) {
        var contact = contactService.getById(id);

        ContactForm contactForm = new ContactForm();

        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setFavourite(contact.isFavourite());
        contactForm.setDescription(contact.getDescription());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setPicture(contact.getPicture());

        model.addAttribute("contactForm", contactForm);
        model.addAttribute("id", id);

        return "user/update_contact_view";
    }

    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable("contactId") String contactId,
            @Valid @ModelAttribute ContactForm contactForm,
            BindingResult bindingResult,
            Model model,
            HttpSession session) {

        // update the contact
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", contactId);
            return "user/update_contact_view";
        }

        var con = contactService.getById(contactId);
        con.setId(contactId);
        con.setName(contactForm.getName());
        con.setEmail(contactForm.getEmail());
        con.setPhoneNumber(contactForm.getPhoneNumber());
        con.setAddress(contactForm.getAddress());
        con.setDescription(contactForm.getDescription());
        con.setFavourite(contactForm.isFavourite());
        con.setWebsiteLink(contactForm.getWebsiteLink());
        con.setLinkedInLink(contactForm.getLinkedInLink());

        // process image:

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            logger.info("file is not empty");
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            con.setCloudinaryImagePublicId(fileName);
            con.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);

        } else {
            logger.info("file is empty");
        }

        var updateCon = contactService.update(con);
        logger.info("updated contact {}", updateCon);

        session.setAttribute("message",
                Message.builder().content("Contact Updated !!").type(MessageType.green).build());

        return "redirect:/user/contacts";
    }

    @GetMapping("/export/excel")
    public void exportToExcel(Authentication authentication,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=contacts.xlsx");

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        List<Contact> contacts = contactService.getAllByUser(user);

        // Workbook banao
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Contacts");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Email");
        headerRow.createCell(2).setCellValue("Phone");
        headerRow.createCell(3).setCellValue("Address");
        headerRow.createCell(4).setCellValue("Website");
        headerRow.createCell(5).setCellValue("LinkedIn");

        // Data rows
        int rowNum = 1;
        for (Contact contact : contacts) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(contact.getName());
            row.createCell(1).setCellValue(contact.getEmail());
            row.createCell(2).setCellValue(contact.getPhoneNumber());
            row.createCell(3).setCellValue(contact.getAddress());
            row.createCell(4).setCellValue(contact.getWebsiteLink());
            row.createCell(5).setCellValue(contact.getLinkedInLink());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(Authentication authentication,
            HttpServletResponse response) throws IOException, DocumentException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=contacts.pdf");

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        List<Contact> contacts = contactService.getAllByUser(user);

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("My Contacts", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Table
        PdfPTable table = new PdfPTable(4); // 4 columns
        table.setWidthPercentage(100);

        // Header
        table.addCell("Name");
        table.addCell("Email");
        table.addCell("Phone");
        table.addCell("Address");

        // Data
        for (Contact contact : contacts) {
            table.addCell(contact.getName() != null ? contact.getName() : "");
            table.addCell(contact.getEmail() != null ? contact.getEmail() : "");
            table.addCell(contact.getPhoneNumber() != null ? contact.getPhoneNumber() : "");
            table.addCell(contact.getAddress() != null ? contact.getAddress() : "");
        }

        document.add(table);
        document.close();
    }
}