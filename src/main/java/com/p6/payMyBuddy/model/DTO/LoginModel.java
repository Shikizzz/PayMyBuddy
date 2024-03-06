package com.p6.payMyBuddy.model.DTO;

import com.p6.payMyBuddy.model.User;
import jakarta.persistence.Entity;
import lombok.Data;
@Data
public class LoginModel {
    private String email;
    private String password;
    private boolean remember;
}
