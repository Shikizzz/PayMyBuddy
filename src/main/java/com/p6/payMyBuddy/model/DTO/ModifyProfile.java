package com.p6.payMyBuddy.model.DTO;

import lombok.Data;

@Data
public class ModifyProfile {
    private String email;
    private String lastName;
    private String firstName;
    private String password;

    public ModifyProfile() {
    }

    public ModifyProfile(String email, String lastName, String firstName, String password) {
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
    }
}