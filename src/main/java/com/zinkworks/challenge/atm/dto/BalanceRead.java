package com.zinkworks.challenge.atm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceRead {
    private String accountNumber;
    private int balance;
    private int maxWithdrawalAmount;
}
