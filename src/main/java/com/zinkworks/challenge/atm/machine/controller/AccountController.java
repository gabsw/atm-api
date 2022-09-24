package com.zinkworks.challenge.atm.machine.controller;

import com.zinkworks.challenge.atm.machine.dto.BalanceRead;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalCreate;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalRead;
import com.zinkworks.challenge.atm.machine.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    AccountService accountService;

    @GetMapping("/{accountId}/balance")
    public BalanceRead getBalance(@PathVariable final int accountId) {
        return accountService.computeCurrentBalance(accountId);
    }

    @PostMapping("/{accountId}/withdrawal")
    public ResponseEntity<WithdrawalRead> createWithdrawal(
        @PathVariable final int accountId,
        @Valid @RequestBody WithdrawalCreate withdrawal) {
        WithdrawalRead createdWithdrawal = accountService.createWithdrawal(accountId, withdrawal);
        return new ResponseEntity<>(createdWithdrawal, HttpStatus.CREATED);
    }
}