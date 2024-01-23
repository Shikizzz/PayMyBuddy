package com.p6.payMyBuddy.model.DTO;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class Profile {

    private String email;
    private String lastName;
    private String firstName;
    private String password;

    private String confirm;

    public Profile() {
    }

    public Profile(String email, String lastName, String firstName, String password, String confirm) {
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
        this.confirm = confirm;
    }
}
