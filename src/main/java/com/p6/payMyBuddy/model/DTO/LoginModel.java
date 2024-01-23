package com.p6.payMyBuddy.model.DTO;

import com.p6.payMyBuddy.model.User;
import jakarta.persistence.Entity;
import lombok.Data;
@Data
public class LoginModel {
    private String email;
    private String password;

    public LoginModel() {
    }

    public LoginModel(String email, String password){
        this.email=email;
        this.password=password;
    }

}
