package com.p6.payMyBuddy.controllers;

import com.p6.payMyBuddy.model.DTO.Connection;
import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.services.ConnectionsService;
import com.p6.payMyBuddy.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ContactController {

    private static Logger logger = LoggerFactory.getLogger(ContactController.class);
    private ConnectionsService connectionsService;
    private UserService userService;

    public ContactController(ConnectionsService connectionsService, UserService userService) {
        this.connectionsService = connectionsService;
        this.userService = userService;
    }

    @GetMapping("/contact")
    public String getContact(@RequestParam Optional<String> error, @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size, Model model, HttpSession session) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        ArrayList<Connection> connections = new ArrayList<>(connectionsService.userToConnectionList(((User) session.getAttribute("user")).getConnections()));
        Page<Connection> connectionsPage = connectionsService.findPaginatedConnections(PageRequest.of(currentPage - 1, pageSize), connections);
        model.addAttribute("connectionsPage", connectionsPage);
        int totalPages = connectionsPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        if (error.isPresent()) {
            model.addAttribute(error.get(), true);
        }
        return "contact.html";
    }

    @Transactional
    @PostMapping("/contact")
    public String postContact(@ModelAttribute(name = "email") String email, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "error";
        }
        User user = (User) session.getAttribute("user");
        User connection = userService.getUser(email);
        ArrayList<User> connections = new ArrayList<>(user.getConnections());
        if (connection == null || connection.equals(user)) {
            return "redirect:contact?error=notFoundError";
        }
        if (userService.userToEmailList(connections).contains(connection.getEmail())) {
            return "redirect:contact?error=alreadyFriendError";
        }
        connections.add(connection);
        ArrayList<User> connectionsNoDouble = new ArrayList<>(new HashSet<>(connections)); //compare before adding
        user.setConnections(connectionsNoDouble);
        userService.updateUser(user);
        session.setAttribute("user", user);
        return "redirect:contact";
    }
}
