package com.zinkworks.challenge.atm.machine.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dispensed_bills", schema = "atm_machine")
public class DispensedBill {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "bill_id", nullable = false)
    private Integer billId;

    @Column(name = "withdrawal_id", nullable = false)
    private Integer withdrawalId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    @Generated(GenerationTime.INSERT)
    private Instant createdAt;
}
