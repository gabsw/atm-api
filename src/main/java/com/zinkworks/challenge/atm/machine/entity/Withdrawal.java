package com.zinkworks.challenge.atm.machine.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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
@Table(name = "withdrawals", schema = "atm_machine")
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
