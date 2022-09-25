package com.zinkworks.challenge.atm.machine.service;

import com.zinkworks.challenge.atm.machine.dto.BalanceRead;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalCreate;
import com.zinkworks.challenge.atm.machine.entity.Account;
import com.zinkworks.challenge.atm.machine.repository.AccountRepository;
import com.zinkworks.challenge.atm.machine.repository.WithdrawalRepository;
import com.zinkworks.challenge.atm.machine.validation.AccountNotFoundException;
import com.zinkworks.challenge.atm.machine.validation.NotEnoughBillsException;
import com.zinkworks.challenge.atm.machine.validation.NotEnoughFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class AccountServiceTests {

    private static final String ACCOUNT_NUMBER = "123456";

    @Mock
    AccountRepository accountRepository;

    @Mock
    BillService billService;

    @Mock
    WithdrawalRepository withdrawalRepository;

    @InjectMocks
    AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void computeBalanceForExistingAccount() throws AccountNotFoundException {

        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.ofNullable(buildAccount()));

        final BalanceRead balanceRead = accountService.computeCurrentBalance(ACCOUNT_NUMBER);

        assertEquals(100, balanceRead.getBalance());
        assertEquals(150, balanceRead.getMaxWithdrawalAmount());
        assertEquals(ACCOUNT_NUMBER, balanceRead.getAccountNumber());
    }

    @Test
    void accountNotFound() {
        when(accountRepository.findByNumber("999")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                     () -> accountService.computeCurrentBalance("999"));
    }

    @Test
    void notEnoughFunds() {
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.ofNullable(buildAccount()));

        assertThrows(NotEnoughFundsException.class,
                     () -> accountService.createWithdrawal(ACCOUNT_NUMBER, new WithdrawalCreate(999999999)));
    }

    private Account buildAccount() {
        return Account.builder()
               .balance(100)
               .overdraft(50)
               .number(ACCOUNT_NUMBER)
               .build();
    }
}
