package com.p6.payMyBuddy.services;

import com.p6.payMyBuddy.model.DTO.TransactionDTO;
import com.p6.payMyBuddy.model.Transaction;
import com.p6.payMyBuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransferService {

    public Page<TransactionDTO> findPaginatedTransfers(Pageable pageable, ArrayList<TransactionDTO> transactions){
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<TransactionDTO> list;
        if (transactions.size() < startItem) {
            list = Collections.emptyList();
        }
        else{
            int toIndex = Math.min(startItem + pageSize, transactions.size());
            list = transactions.subList(startItem, toIndex);
        }
        Page<TransactionDTO> transactionsPage = new PageImpl<TransactionDTO>(list, PageRequest.of(currentPage, pageSize), transactions.size());
        return transactionsPage;
    }

    public ArrayList<TransactionDTO> transactionToTransactionDTOList(List<Transaction> transactions, User connectedUser){ //DTO converter
        ArrayList<TransactionDTO> transactionsDTO = new ArrayList<>();
        for(int i=0; i<transactions.size(); i++){
            TransactionDTO transactionDTO = new TransactionDTO();
            if (transactions.get(i).getSource().equals(connectedUser)) {
                transactionDTO = new TransactionDTO(transactions.get(i).getTarget().getFirstName(), transactions.get(i).getDescription(), -transactions.get(i).getAmount());
            }
            else {transactionDTO = new TransactionDTO(transactions.get(i).getSource().getFirstName(), transactions.get(i).getDescription(), transactions.get(i).getAmount());}
            transactionsDTO.add(transactionDTO);
        }
        return transactionsDTO;
    }
}
