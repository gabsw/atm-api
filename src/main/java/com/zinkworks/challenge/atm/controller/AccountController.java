package com.zinkworks.challenge.atm.controller;

import com.zinkworks.challenge.atm.dto.BalanceRead;
import com.zinkworks.challenge.atm.dto.WithdrawalCreate;
import com.zinkworks.challenge.atm.dto.WithdrawalRead;
import com.zinkworks.challenge.atm.service.AccountService;
import com.zinkworks.challenge.atm.validation.AccountNotFoundException;
import com.zinkworks.challenge.atm.validation.MismatchedPinException;
import com.zinkworks.challenge.atm.validation.NotEnoughBillsException;
import com.zinkworks.challenge.atm.validation.NotEnoughFundsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/account")
public class AccountController {

    final AccountService accountService;

    public AccountController(final AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountNumber}/balance")
    public BalanceRead getBalance(
        @PathVariable final String accountNumber,
        @RequestHeader("Authorization") final String pin)
    throws AccountNotFoundException, MismatchedPinException {
        return accountService.getCurrentBalance(accountNumber, pin);
    }

    @PostMapping("/{accountNumber}/withdrawal")
    public ResponseEntity<WithdrawalRead> createWithdrawal(
        @PathVariable final String accountNumber,
        @Valid @RequestBody final WithdrawalCreate withdrawal,
        @RequestHeader("Authorization") final String pin)
    throws MismatchedPinException, AccountNotFoundException, NotEnoughBillsException, NotEnoughFundsException {
        WithdrawalRead createdWithdrawal = accountService.createWithdrawal(accountNumber, pin, withdrawal);
        return new ResponseEntity<>(createdWithdrawal, HttpStatus.CREATED);
    }
}
