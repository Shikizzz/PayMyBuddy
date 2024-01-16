package com.p6.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column (name = "Email")
    private String email;

    @Column (name = "Password")
    private String password;

    @Column (name = "FirstName")
    private String firstname;

    @Column (name = "LastName")
    private String lastName;

    @Column (name = "Balance")
    private String balance;

    @ManyToMany
    //joinculunm etc
    private List<User> connections = new ArrayList<>();
}
