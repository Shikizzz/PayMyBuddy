package com.p6.payMyBuddy.controllers;

import com.p6.payMyBuddy.model.DTO.ChangePassword;
import com.p6.payMyBuddy.model.DTO.LoginModel;
import com.p6.payMyBuddy.model.DTO.ModifyProfile;
import com.p6.payMyBuddy.model.DTO.Profile;
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
public class LoginController extends HttpServlet {
    private UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/")
    public String home() {
        return "login";
    }
    @GetMapping("/login")
    public String home2(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @PostMapping("/login")
    public String submit(@ModelAttribute (name="loginModel") LoginModel loginModel, BindingResult result, Model model, HttpSession session){
        if (result.hasErrors()) {
            return "error";
        }
        User userInDB = userService.getUser(loginModel.getEmail()); //getting Email and password in DB, if User exists
        if(userInDB != null){
            if (userService.checkPassword(loginModel.getPassword(), userInDB.getPassword())) { //check password
                session.setAttribute("email", userInDB.getEmail());
                session.setAttribute("lastname", userInDB.getLastName());
                session.setAttribute("firstname", userInDB.getFirstName());
                session.setAttribute("password", userInDB.getPassword());
                session.setAttribute("balance", userInDB.getBalance());
                session.setAttribute("connections", userInDB.getConnections());
                session.setAttribute("transactions", userInDB.getTransactions());
                return "hubPage";
            }
            else{
                model.addAttribute("loginError", true);
                return "login";
            }
        }
        else{
            model.addAttribute("loginError", true);
            return "login";
        }
    } //+httpsession ?

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String submit(@ModelAttribute (name="profile") Profile profile, BindingResult result, Model model){
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
        userService.saveUser(user);   //TODO: add HttpSession or redirect to login
        return "hubPage";

    }



    @GetMapping("/editProfile")
    public String getEditProfile(Model model, HttpSession session) {
        model.addAttribute("email", session.getAttribute("email"));
        model.addAttribute("lastname", session.getAttribute("lastname"));
        model.addAttribute("firstname", session.getAttribute("firstname"));
        model.addAttribute("password", session.getAttribute("password"));
        return "editProfile.html";
    }

    @PostMapping("/editProfile")
    public String postEditProfile(@ModelAttribute (name="ModifyProfile") ModifyProfile profile, BindingResult result, Model model, HttpSession session) {
        if(userService.checkPassword(profile.getPassword(), (String) session.getAttribute("password"))){
            User user = new User();
            user.setEmail(profile.getEmail());
            user.setLastName(profile.getLastName());
            user.setFirstName(profile.getFirstName());
            user.setPassword(profile.getPassword());
            user.setBalance((Double) session.getAttribute("balance"));
            user.setConnections((List<User>) session.getAttribute("connections"));
            user.setTransactions((List<Transaction>) session.getAttribute("transactions"));
            userService.saveUser(user);
            return "hubPage.html"; //TODO: update HttpSession
        }
        model.addAttribute("passwordError",  true);
        return "editProfile.html";
    }

    @GetMapping("/editPassword")
    public String getEditPassword(Model model, HttpSession session) {
        return "editPassword.html";
    }

    @PostMapping("/editPassword")
    public String postEditPassword(@ModelAttribute (name="changePassword")ChangePassword changePassword, BindingResult result, Model model, HttpSession session) {
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
        User user = new User();
        user.setEmail((String) session.getAttribute("email"));
        user.setLastName((String) session.getAttribute("lastname"));
        user.setFirstName((String) session.getAttribute("firstname"));
        user.setPassword(changePassword.getNewPassword());
        user.setBalance((Double) session.getAttribute("balance"));
        user.setConnections((List<User>) session.getAttribute("connections"));
        user.setTransactions((List<Transaction>) session.getAttribute("transactions"));
        userService.saveUser(user);
        return "hubPage.html"; //TODO: update HttpSession
    }
}
