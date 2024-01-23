package com.p6.payMyBuddy.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HubController {

    @GetMapping("/transfer")
    public String transfer() {
        return "transfer.html";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("lastname", session.getAttribute("lastname"));
        model.addAttribute("firstname", session.getAttribute("firstname"));
        return "profile.html";
    }
    @GetMapping("/contact")
    public String contact() {
        return "contact.html";
    }

    @GetMapping("/hubPage")
    public String hub(){
        return "hubPage.html";
    }

}
