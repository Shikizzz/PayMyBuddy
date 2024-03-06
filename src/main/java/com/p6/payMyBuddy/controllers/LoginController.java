package com.p6.payMyBuddy.controllers;

import com.p6.payMyBuddy.model.DTO.LoginModel;
import com.p6.payMyBuddy.model.DTO.Profile;
import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.services.UserService;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.Optional;

@Controller
public class LoginController extends HttpServlet {
    private UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        return "redirect:home";
    }

    @GetMapping("/login")
    public String getLogin(HttpServletRequest request, HttpSession session) {
        Optional<String> emailCookie = Arrays.stream(request.getCookies())
                .filter(c -> "email".equals(c.getName()))
                .map(Cookie::getValue)
                .findAny();
        Optional<String> passwordCookie = Arrays.stream(request.getCookies())
                .filter(c -> "password".equals(c.getName()))
                .map(Cookie::getValue)
                .findAny();
        if (emailCookie.isPresent() && passwordCookie.isPresent()) {
            User userInDB = userService.getUser(emailCookie.get());
            if (passwordCookie.get().equals(userInDB.getPassword())) {
                session.setAttribute("user", userInDB);
                return "redirect:home";
            }
        }
        return "login.html";
    }

    @GetMapping("/logout")
    public String getLogout(HttpSession session, HttpServletResponse response) {
        Cookie emailCookieRemove = new Cookie("email", "");
        Cookie passwordCookieRemove = new Cookie("password", "");
        emailCookieRemove.setMaxAge(0);
        passwordCookieRemove.setMaxAge(0);
        passwordCookieRemove.setSecure(true);
        response.addCookie(emailCookieRemove);
        response.addCookie(passwordCookieRemove);
        session.invalidate();
        return "redirect:login";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute(name = "loginModel") LoginModel loginModel, BindingResult result, Model model, HttpSession session, HttpServletResponse response) {
        if (result.hasErrors()) {
            return "error";
        }
        User userInDB = userService.getUser(loginModel.getEmail()); //getting Email and password in DB, if User exists
        if (userInDB != null) {
            if (userService.checkPassword(loginModel.getPassword(), userInDB.getPassword())) { //check password
                if (loginModel.isRemember()) {
                    Cookie emailCookie = new Cookie("email", loginModel.getEmail());
                    Cookie passwordCookie = new Cookie("password", userInDB.getPassword());
                    emailCookie.setMaxAge(60 * 60 * 24 * 7);
                    emailCookie.setHttpOnly(true);
                    passwordCookie.setMaxAge(60 * 60 * 24 * 7);
                    passwordCookie.setSecure(true);
                    passwordCookie.setHttpOnly(true);
                    response.addCookie(emailCookie);
                    response.addCookie(passwordCookie);
                }
                session.setAttribute("user", userInDB);
                return "redirect:home";
            } else {
                model.addAttribute("loginError", true);
                return "login.html";
            }
        } else {
            model.addAttribute("loginError", true);
            return "login.html";
        }
    }

    @GetMapping("/register")
    public String getRegister() {
        return "register.html";
    }

    @PostMapping("/register")
    public String postRegister(@ModelAttribute(name = "profile") Profile profile, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "error";
        }
        if (userService.existsInDB(profile.getEmail())) {
            model.addAttribute("registerError", true);
            return "register";
        }
        if (profile.getPassword().length() < 6) {
            model.addAttribute("passwordError", true);
            return "register";
        }
        if (!profile.getPassword().equals(profile.getConfirm())) {
            model.addAttribute("confirmError", true);
            return "register";
        }
        User user = new User();
        user.setEmail(profile.getEmail());
        user.setLastName(profile.getLastName());
        user.setFirstName(profile.getFirstName());
        user.setPassword(profile.getPassword());
        user.setBalance(0.0);
        userService.saveUser(user);
        session.setAttribute("user", user);
        return "redirect:home";
    }
}
