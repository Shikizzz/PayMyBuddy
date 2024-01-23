package com.p6.payMyBuddy.repository;

import com.p6.payMyBuddy.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Integer> {
}
