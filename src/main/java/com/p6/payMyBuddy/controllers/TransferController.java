package com.p6.payMyBuddy.controllers;

import com.p6.payMyBuddy.model.DTO.SendMoneyDTO;
import com.p6.payMyBuddy.model.DTO.TransactionDTO;
import com.p6.payMyBuddy.model.Transaction;
import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.services.TransactionService;
import com.p6.payMyBuddy.services.TransferService;
import com.p6.payMyBuddy.services.UserService;
import jakarta.servlet.http.HttpSession;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class TransferController {
    private UserService userService;
    private TransferService transferService;
    private TransactionService transactionService;

    public TransferController(UserService userService, TransferService transferService, TransactionService transactionService) {
        this.userService = userService;
        this.transferService = transferService;
        this.transactionService = transactionService;
    }

    @GetMapping("/transfer")
    public String getTransfer(@RequestParam Optional<String> error, @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size, Model model, HttpSession session) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        ArrayList<Transaction> allTransactions = transactionService.mergeTransactionList((User) session.getAttribute("user"));
        ArrayList<TransactionDTO> transactions = transferService.transactionToTransactionDTOList(allTransactions, (User) session.getAttribute("user"));
        Page<TransactionDTO> transactionsPage = transferService.findPaginatedTransfers(PageRequest.of(currentPage - 1, pageSize), transactions);
        model.addAttribute("transactionsPage", transactionsPage);
        int totalPages = transactionsPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        if (error.isPresent()) {
            model.addAttribute(error.get(), true);
        }
        User user = (User) session.getAttribute("user");
        ArrayList<User> connections = new ArrayList<>(user.getConnections());
        ArrayList<String> connectionsEmail = userService.userToEmailList(connections);
        model.addAttribute("usersList", connectionsEmail);
        return "transfer.html";
    }

    @PostMapping("/transfer")
    public String postTransfer(@ModelAttribute(name = "sendMoneyDTO") SendMoneyDTO sendMoneyDTO, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "error";
        }
        User user = (User) session.getAttribute("user");
        if (sendMoneyDTO.getAmount() > user.getBalance()) {
            return "redirect:transfer?error=balanceError";
        }
        userService.sendMoney(user, sendMoneyDTO.getEmail(), sendMoneyDTO.getAmount(), sendMoneyDTO.getDescription());
        User updatedUser = userService.getUser(user.getEmail());
        session.setAttribute("user", updatedUser);
        return "redirect:transfer";


    }

}
