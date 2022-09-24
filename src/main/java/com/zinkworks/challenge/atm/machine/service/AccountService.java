package com.zinkworks.challenge.atm.machine.service;

import com.zinkworks.challenge.atm.machine.dto.BalanceRead;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalCreate;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalRead;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountService {
    public BalanceRead computeCurrentBalance(final int accountId) {
        return BalanceRead.builder()
                          .balance(1000)
                          .maxWithdrawalAmount(1200)
                          .accountId(accountId)
                          .build();
    }

    public WithdrawalRead createWithdrawal(final int accountId, final WithdrawalCreate withdrawalCreate) {
        return WithdrawalRead.builder()
                             .accountId(accountId)
                             .remainingBalance(12)
                             .dispensedBills(Map.of("50", 1, "20", 2))
                             .build();
    }
}
