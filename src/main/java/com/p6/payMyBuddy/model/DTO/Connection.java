package com.p6.payMyBuddy.model.DTO;

import lombok.Data;

@Data
public class Connection {

    private String email;
    private String firstName;
    private String lastName;

    public Connection() {
    }

    public Connection(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
