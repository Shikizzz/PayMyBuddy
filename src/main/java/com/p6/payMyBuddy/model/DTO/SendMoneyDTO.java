package com.p6.payMyBuddy.model.DTO;

import lombok.Data;

@Data
public class SendMoneyDTO {
    private String email;
    private String description;
    private double amount;

    public SendMoneyDTO(String email,String description , double amount) {
        this.email = email;
        this.description = description;
        this.amount = amount;
    }
    public SendMoneyDTO() {
    }
}
