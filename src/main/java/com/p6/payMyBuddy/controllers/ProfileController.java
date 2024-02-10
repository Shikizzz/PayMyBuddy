package com.p6.payMyBuddy.controllers;

import com.p6.payMyBuddy.model.DTO.ChangePassword;
import com.p6.payMyBuddy.model.DTO.ModifyProfile;
import com.p6.payMyBuddy.model.Transaction;
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

import java.util.List;
@Controller
public class ProfileController extends HttpServlet {
    private UserService userService;
    public ProfileController(UserService userService) {
        this.userService = userService;
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
    public String postEditProfile(@ModelAttribute(name="ModifyProfile") ModifyProfile profile, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "error";
        }
        User user = (User) session.getAttribute("user");
        if(userService.checkPassword(profile.getPassword(), user.getPassword())){
            user.setEmail(profile.getEmail());
            user.setLastName(profile.getLastName());
            user.setFirstName(profile.getFirstName());
            user.setPassword(profile.getPassword());
            userService.updateUser(user);
            session.setAttribute("user", user);
            return "hubPage.html";
        }
        model.addAttribute("passwordError",  true);
        return "editProfile.html";
    }

    @GetMapping("/editPassword")
    public String getEditPassword(Model model, HttpSession session) {
        return "editPassword.html";
    }

    @PostMapping("/editPassword")
    public String postEditPassword(@ModelAttribute (name="changePassword") ChangePassword changePassword, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "error";
        }
        if(!userService.checkPassword(changePassword.getOldPassword(), (String) session.getAttribute("password"))){
            model.addAttribute("oldPasswordError",  true);
            return "editPassword.html";
        }
        if(changePassword.getNewPassword().length()<6){
            model.addAttribute("passwordLengthError",  true);
            return "editPassword.html";
        }
        if(!changePassword.getNewPassword().equals(changePassword.getConfirmNewPassword())){
            model.addAttribute("passwordMatchError",  true);
            return "editPassword.html";
        }
        User user = (User) session.getAttribute("user");
        user.setPassword(changePassword.getNewPassword());
        userService.updateUser(user);             //TODO check if password is encrypted in session ?
        session.setAttribute("user", user);
        return "hubPage.html";
    }
}
