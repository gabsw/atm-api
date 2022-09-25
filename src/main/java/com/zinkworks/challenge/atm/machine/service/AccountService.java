package com.zinkworks.challenge.atm.machine.service;

import com.zinkworks.challenge.atm.machine.dto.BalanceRead;
import com.zinkworks.challenge.atm.machine.dto.DispensedBillRead;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalCreate;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalRead;
import com.zinkworks.challenge.atm.machine.entity.Account;
import com.zinkworks.challenge.atm.machine.entity.Bill;
import com.zinkworks.challenge.atm.machine.entity.DispensedBill;
import com.zinkworks.challenge.atm.machine.entity.Withdrawal;
import com.zinkworks.challenge.atm.machine.repository.AccountRepository;
import com.zinkworks.challenge.atm.machine.repository.WithdrawalRepository;
import com.zinkworks.challenge.atm.machine.validation.AccountNotFoundException;
import com.zinkworks.challenge.atm.machine.validation.NotEnoughBillsException;
import com.zinkworks.challenge.atm.machine.validation.NotEnoughFundsException;
import com.zinkworks.challenge.atm.machine.validation.ForbiddenOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    final AccountRepository accountRepository;

    final WithdrawalRepository withdrawalRepository;

    final BillService billService;

    public AccountService(
        final AccountRepository accountRepository,
        final WithdrawalRepository withdrawalRepository,
        final BillService billService) {
        this.accountRepository = accountRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.billService = billService;
    }


    public BalanceRead computeCurrentBalance(final String accountNumber) throws AccountNotFoundException {
        final Account account = fetchAccount(accountNumber);

        return BalanceRead.builder()
                          .balance(account.getBalance())
                          .maxWithdrawalAmount(account.maxWithdrawalAmount())
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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public WithdrawalRead createWithdrawal(final String accountNumber, final WithdrawalCreate withdrawalCreate)
        throws AccountNotFoundException, ForbiddenOperationException, NotEnoughBillsException,
               NotEnoughFundsException {

        final int withdrawalAmount = withdrawalCreate.getAmount();

        final Account account = fetchAccount(accountNumber);

        checkForEnoughFunds(account.maxWithdrawalAmount(), withdrawalAmount);

        final List<Bill> allBills = billService.fetchAllBillsByDescendingFaceValue();

        final List<UsedBill> usedBills = billService.optimalBillsCombination(allBills, withdrawalAmount);

        billService.updateBills(usedBills);

        account.updateBalance(-withdrawalAmount);
        accountRepository.save(account);

        Withdrawal withdrawal = buildWithdrawal(account, usedBills, withdrawalAmount);
        withdrawalRepository.save(withdrawal);

        List<DispensedBillRead> dispensedBillReads = usedBills.stream()
            .map(u -> new DispensedBillRead(u.getBill().getFaceValue(), u.getQuantity()))
            .toList();

        return WithdrawalRead.builder()
                             .accountNumber(accountNumber)
                             .remainingBalance(account.getBalance())
                             .dispensedBills(dispensedBillReads)
                             .build();
    }

    private void checkForEnoughFunds(final int maxAmount, final int requestedAmount)
        throws NotEnoughFundsException {
        if (requestedAmount > maxAmount) {
            // TODO: Improves to add values
            throw new NotEnoughFundsException("The requested amount exceeds the maximum withdrawal funds.");
        }
    }



    private Withdrawal buildWithdrawal(
        final Account account,
        final List<UsedBill> usedBills,
        final int withdrawalAmount) {

        List<DispensedBill> allDispensedBills = new ArrayList<>();

        for (UsedBill usedBill : usedBills) {
            final DispensedBill
                dispensedBill = DispensedBill.builder()
                                             .billId(usedBill.getBill().getId())
                                             .quantity(usedBill.getQuantity())
                                             .build();
            allDispensedBills.add(dispensedBill);
        }

        return Withdrawal.builder()
                         .accountId(account.getId())
                         .bills(allDispensedBills)
                         .amount(withdrawalAmount)
                         .build();
    }
}

