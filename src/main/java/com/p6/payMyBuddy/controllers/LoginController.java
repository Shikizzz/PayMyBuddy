package com.p6.payMyBuddy.controllers;

import com.p6.payMyBuddy.model.DTO.LoginModel;
import com.p6.payMyBuddy.model.DTO.Profile;
import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.services.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController extends HttpServlet {
    private UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/")
    public String home(HttpSession session) {
        return "redirect:hubPage";
    }
    @GetMapping("/login")
    public String getLogin(HttpSession session) {
        return "login.html";
    }
    @GetMapping("/logout")
    public String getLogout(HttpSession session) {
        session.invalidate();
        return "redirect:login";
    }
    @Transactional
    @PostMapping("/login")
    public String postLogin(@ModelAttribute (name="loginModel") LoginModel loginModel, BindingResult result, Model model, HttpSession session){
        if (result.hasErrors()) {
            return "error";
        }
        User userInDB = userService.getUser(loginModel.getEmail()); //getting Email and password in DB, if User exists
        if(userInDB != null){
            if (userService.checkPassword(loginModel.getPassword(), userInDB.getPassword())) { //check password
                session.setAttribute("user", userInDB);
                return "redirect:hubPage";
            }
            else{
                model.addAttribute("loginError", true);
                return "login.html";
            }
        }
        else{
            model.addAttribute("loginError", true);
            return "login.html";
        }
    }

    @GetMapping("/register")
    public String getRegister() {
        return "register.html";
    }

    @PostMapping("/register")
    public String postRegister(@ModelAttribute (name="profile") Profile profile, BindingResult result, Model model, HttpSession session){
        if (result.hasErrors()) {
            return "error";
        }
        if(userService.existsInDB(profile.getEmail())){
            model.addAttribute("registerError", true);
            return "register";
        }
        if(profile.getPassword().length()<6){
            model.addAttribute("passwordError", true);
            return "register";
        }
        if(!profile.getPassword().equals(profile.getConfirm())){
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
        return "redirect:hubPage";
    }
}
