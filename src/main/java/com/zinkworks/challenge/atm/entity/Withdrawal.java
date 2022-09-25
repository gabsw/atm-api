package com.zinkworks.challenge.atm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "withdrawals", schema = "atm")
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(name = "amount", nullable = false)
    private int amount;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    @Generated(GenerationTime.INSERT)
    private Instant createdAt;

    @OneToMany(mappedBy = "withdrawal", cascade = CascadeType.ALL)
    private List<DispensedBill> bills = new ArrayList<>();

    public void addDispensedBill(final DispensedBill dispensedBill) {
        dispensedBill.setWithdrawal(this);
        bills.add(dispensedBill);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
