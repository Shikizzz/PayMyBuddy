package com.p6.payMyBuddy.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private int id_transaction;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private double amount;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "transaction_source")
    @ToString.Exclude
    private User source;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "transaction_target")
    @ToString.Exclude
    private User target;

}
