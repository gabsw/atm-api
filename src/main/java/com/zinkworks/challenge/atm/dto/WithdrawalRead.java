package com.zinkworks.challenge.atm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRead {
    private String accountNumber;
    private List<DispensedBillRead> dispensedBills;
    private int remainingBalance;
}
