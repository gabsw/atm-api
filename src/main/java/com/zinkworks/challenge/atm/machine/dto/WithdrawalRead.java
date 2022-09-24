package com.zinkworks.challenge.atm.machine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRead {
    private int accountId;
    private Map<String, Integer> dispensedBills;
    private int remainingBalance;
}
