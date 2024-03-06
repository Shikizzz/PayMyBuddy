package com.p6.payMyBuddy.controllers;

import com.p6.payMyBuddy.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/home")
    public String getHome(HttpSession session) {
        return "home.html";
    }

}
