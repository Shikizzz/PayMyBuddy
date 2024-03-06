package com.p6.payMyBuddy.services;

import com.p6.payMyBuddy.model.Transaction;
import com.p6.payMyBuddy.model.User;
import com.p6.payMyBuddy.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


@Service
public class TransactionService {

    private TransactionRepository repository;
    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public ArrayList<Transaction> mergeTransactionList(User user){
        ArrayList<Transaction> transactionsTarget = new ArrayList<>(user.getTransactionsTarget());
        ArrayList<Transaction> transactionsSource = new ArrayList<>(user.getTransactionsSource());
        ArrayList<Transaction> merged = transactionsSource;
        merged.addAll(transactionsTarget);
        Collections.sort(merged, Comparator.comparing(Transaction::getDate)); //sorting by date
        Collections.reverse(merged); //anti-chronological
        return merged;
    }

}
