package com.p6.payMyBuddy.controllers;

import com.p6.payMyBuddy.model.DTO.ChangePassword;
import com.p6.payMyBuddy.model.DTO.DoubleDTO;
import com.p6.payMyBuddy.model.DTO.ModifyProfile;
import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.services.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ProfileController extends HttpServlet {
    private UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String getProfile(@RequestParam Optional<String> error, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("email", user.getEmail());
        model.addAttribute("lastname", user.getLastName());
        model.addAttribute("firstname", user.getFirstName());
        double roundedBalance = ((double) (int) (user.getBalance() * 100) / 100);
        model.addAttribute("balance", roundedBalance);
        if (error.isPresent()) {
            model.addAttribute(error.get(), true);
        }
        return "profile.html";
    }

    @GetMapping("/editProfile")
    public String getEditProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("email", user.getEmail());
        model.addAttribute("lastname", user.getLastName());
        model.addAttribute("firstname", user.getFirstName());
        model.addAttribute("password", user.getPassword());
        return "editProfile.html";
    }

    @PostMapping("/editProfile")
    public String postEditProfile(@ModelAttribute(name = "ModifyProfile") ModifyProfile profile, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "error";
        }
        User user = (User) session.getAttribute("user");
        if (userService.checkPassword(profile.getPassword(), user.getPassword())) {
            user.setEmail(profile.getEmail());
            user.setLastName(profile.getLastName());
            user.setFirstName(profile.getFirstName());
            user.setPassword(profile.getPassword());
            userService.updateUser(user);
            session.setAttribute("user", user);
            return "home.html";
        }
        model.addAttribute("passwordError", true);
        return "editProfile.html";
    }

    @GetMapping("/editPassword")
    public String getEditPassword(Model model, HttpSession session) {
        return "editPassword.html";
    }

    @PostMapping("/editPassword")
    public String postEditPassword(@ModelAttribute(name = "changePassword") ChangePassword changePassword, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "error";
        }
        User user = (User) session.getAttribute("user");
        if (!userService.checkPassword(changePassword.getOldPassword(), user.getPassword())) {
            model.addAttribute("oldPasswordError", true);
            return "editPassword.html";
        }
        if (changePassword.getNewPassword().length() < 6) {
            model.addAttribute("passwordLengthError", true);
            return "editPassword.html";
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmNewPassword())) {
            model.addAttribute("passwordMatchError", true);
            return "editPassword.html";
        }
        user.setPassword(changePassword.getNewPassword());
        userService.saveUser(user);
        return "redirect:logout"; //logout to force login and store encrypted password in session
    }

    @PostMapping("/profile-addBalance")
    public String postAddBalance(@ModelAttribute(name = "doubleDTO") DoubleDTO doubleDTO, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "error";
        }
        if (doubleDTO.getAmount() <= 0) {
            return "redirect:profile";
        }
        User user = (User) session.getAttribute("user");
        userService.addBalance(user, doubleDTO.getAmount());
        session.setAttribute("user", user);
        return "redirect:profile";
    }

    @PostMapping("/profile-retrieveBalance")
    public String postRemoveBalance(@ModelAttribute(name = "doubleDTO") DoubleDTO doubleDTO, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "error";
        }
        User user = (User) session.getAttribute("user");
        if (doubleDTO.getAmount() > user.getBalance()) {
            return "redirect:profile?error=balanceError";
        }
        userService.removeBalance(user, doubleDTO.getAmount());
        session.setAttribute("user", user);
        return "redirect:profile";
    }
}
