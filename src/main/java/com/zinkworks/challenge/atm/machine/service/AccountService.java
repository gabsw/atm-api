package com.zinkworks.challenge.atm.machine.service;

import com.zinkworks.challenge.atm.machine.dto.BalanceRead;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalCreate;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalRead;
import com.zinkworks.challenge.atm.machine.entity.Account;
import com.zinkworks.challenge.atm.machine.repository.AccountRepository;
import com.zinkworks.challenge.atm.machine.validation.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AccountService {

    final
    AccountRepository accountRepository;

    public AccountService(final AccountRepository accountRepository) {this.accountRepository = accountRepository;}

    public BalanceRead computeCurrentBalance(final String accountNumber) throws AccountNotFoundException {
        final Account account = fetchAccount(accountNumber);

        final int balance = account .getBalance();
        final int maxWithdrawalAmount = balance + account.getOverdraft();

        return BalanceRead.builder()
                          .balance(balance)
                          .maxWithdrawalAmount(maxWithdrawalAmount)
                          .accountNumber(accountNumber)
                          .build();
    }

    private Account fetchAccount(final String accountNumber) throws AccountNotFoundException {
        final Optional<Account> account = accountRepository.findByNumber(accountNumber);

        if (account.isEmpty()) {
            throw new AccountNotFoundException(String.format("Account with number=%s was not found.", accountNumber));
        }

        return account.get();
    }

    public WithdrawalRead createWithdrawal(final String accountNumber, final WithdrawalCreate withdrawalCreate) {
        return WithdrawalRead.builder()
                             .accountNumber(accountNumber)
                             .remainingBalance(12)
                             .dispensedBills(Map.of("50", 1, "20", 2))
                             .build();
    }
}
