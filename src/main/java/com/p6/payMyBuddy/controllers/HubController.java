package com.p6.payMyBuddy.controllers;

import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HubController {

    private UserService userService;

    public HubController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/transfer")
    public String getTransfer(Model model, HttpSession session) {
        return "transfer.html";
    }

    @GetMapping("/profile")
    public String getProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user"); //TODO Sometimes User in Null. Why ?
        model.addAttribute("email", user.getEmail());
        model.addAttribute("lastname",user.getLastName());
        model.addAttribute("firstname", user.getFirstName());
        return "profile.html";
    }

    @GetMapping("/hubPage")
    public String getHub(HttpSession session){
        return "hubPage.html";
    }

}
