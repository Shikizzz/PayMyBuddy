package com.p6.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Id")
    private int id_transaction;

    @Column(name="Description")
    private String description;

    @Column(name="Amount")
    private double amount;

    @Column(name="Date")
    private Date date;

    @ManyToOne
    //@JoinColumn(name= "source")
    private User source;

    @ManyToOne
    //@JoinColumn()
    private User target;
}
