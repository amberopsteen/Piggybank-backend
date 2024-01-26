package com.testing.piggybank.model;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "account"
)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "name")
    private String name;

    @Column(name = "userid")
    private long userId;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }
}