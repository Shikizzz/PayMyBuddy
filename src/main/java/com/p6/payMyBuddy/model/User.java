package com.p6.payMyBuddy.model;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@DynamicUpdate
@Table(name = "user")
public class User {
    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "balance")
    private double balance;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST
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
            mappedBy = "source",
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    //@ToString.Exclude
    List<Transaction> transactionsSource = new ArrayList<>();

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "target",
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    //@ToString.Exclude
    List<Transaction> transactionsTarget = new ArrayList<>();

    public void addTransaction(Transaction transaction) { //helper
        transactionsSource.add(0, transaction);
        transaction.setSource(this);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<User> getConnections() {
        return connections;
    }

    public void setConnections(List<User> connections) {
        this.connections = connections;
    }

    public List<Transaction> getTransactionsSource() {
        return transactionsSource;
    }

    public void setTransactionsSource(List<Transaction> transactionsSource) {
        this.transactionsSource = transactionsSource;
    }

    public List<Transaction> getTransactionsTarget() {
        return transactionsTarget;
    }

    public void setTransactionsTarget(List<Transaction> transactionsTarget) {
        this.transactionsTarget = transactionsTarget;
    }
}
