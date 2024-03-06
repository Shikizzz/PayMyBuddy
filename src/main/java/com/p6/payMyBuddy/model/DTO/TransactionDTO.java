package com.p6.payMyBuddy.model.DTO;

import lombok.Data;

@Data
public class TransactionDTO {

    private String connection;
    private String description;
    private double amount;

    public TransactionDTO() {
    }

    public TransactionDTO(String connection, String description, double amount) {
        this.connection = connection;
        this.description = description;
        this.amount = amount;
    }
}
