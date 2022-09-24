package com.zinkworks.challenge.atm.machine.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@NoArgsConstructor
@Entity
@Table(name = "accounts", schema = "atm_machine")
public class Account {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "unique_number", nullable = false)
    private String number;

    @Column(name = "pin", nullable = false)
    private String pin;

    @Column(name = "overdraft", nullable = false)
    private int overdraft;

    @Column(name = "balance", nullable = false)
    private int balance;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public int maxWithdrawalAmount() {
        return balance + overdraft;
    }

    public void updateBalance(final int amount) { // TODO: check overdraft?
        balance += amount;
    }
}
