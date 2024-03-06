package com.p6.payMyBuddy.model.DTO;

import lombok.Data;

@Data
public class DoubleDTO {
    private double amount;

    public DoubleDTO() {
    }
    public DoubleDTO(double amount) {
        this.amount = amount;
    }
}
