package com.p6.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@DynamicUpdate
@Table(name = "user")
public class User {
    @Id
    @Column (name = "email")
    private String email;

    @Column (name = "password")
    private String password;

    @Column (name = "firstname")
    private String firstName;

    @Column (name = "lastname")
    private String lastName;

    @Column (name = "balance")
    private Double balance;

    @ManyToMany(
            fetch = FetchType.EAGER,  //we will have to use @Transactional when accessing connections attribute
            cascade = {
                    CascadeType.PERSIST,
            }
    )
    @JoinTable(
            name = "connections_users",
            joinColumns = @JoinColumn(name = "user_source"), //connection_source
            inverseJoinColumns = @JoinColumn(name = "user_target") //connection_target
    )
    private List<User> connections = new ArrayList<>();

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "target"
    )
    List<Transaction> transactions = new ArrayList<>();


    public void addTransaction(Transaction transaction) { //helper
        transactions.add(transaction);
        transaction.setSource(this);
    }

    public void removeTransaction(Transaction transaction) { //helper
        transactions.add(transaction);
        transaction.setSource(null);
    }
}
