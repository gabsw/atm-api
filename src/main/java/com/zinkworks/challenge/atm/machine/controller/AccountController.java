package com.zinkworks.challenge.atm.machine.controller;

import com.zinkworks.challenge.atm.machine.dto.BalanceRead;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalCreate;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalRead;
import com.zinkworks.challenge.atm.machine.service.AccountService;
import com.zinkworks.challenge.atm.machine.validation.AccountNotFoundException;
import com.zinkworks.challenge.atm.machine.validation.NotEnoughBillsException;
import com.zinkworks.challenge.atm.machine.validation.ForbiddenOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/account")
public class AccountController {

    final
    AccountService accountService;

    public AccountController(final AccountService accountService) {this.accountService = accountService;}

    @GetMapping("/{accountNumber}/balance")
    public BalanceRead getBalance(@PathVariable final String accountNumber) throws AccountNotFoundException {
        return accountService.computeCurrentBalance(accountNumber);
    }

    @PostMapping("/{accountNumber}/withdrawal")
    public ResponseEntity<WithdrawalRead> createWithdrawal(
        @PathVariable final String accountNumber,
        @Valid @RequestBody WithdrawalCreate withdrawal)
        throws ForbiddenOperationException, AccountNotFoundException, NotEnoughBillsException {
        WithdrawalRead createdWithdrawal = accountService.createWithdrawal(accountNumber, withdrawal);
        return new ResponseEntity<>(createdWithdrawal, HttpStatus.CREATED);
    }
}
