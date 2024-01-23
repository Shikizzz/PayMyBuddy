package com.p6.payMyBuddy.model.DTO;

import lombok.Data;

@Data
public class ChangePassword {
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}
