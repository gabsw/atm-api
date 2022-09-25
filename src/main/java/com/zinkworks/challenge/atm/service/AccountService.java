package com.zinkworks.challenge.atm.service;

import com.zinkworks.challenge.atm.dto.DispensedBillRead;
import com.zinkworks.challenge.atm.entity.Account;
import com.zinkworks.challenge.atm.entity.Withdrawal;
import com.zinkworks.challenge.atm.dto.BalanceRead;
import com.zinkworks.challenge.atm.dto.WithdrawalCreate;
import com.zinkworks.challenge.atm.dto.WithdrawalRead;
import com.zinkworks.challenge.atm.entity.Bill;
import com.zinkworks.challenge.atm.entity.DispensedBill;
import com.zinkworks.challenge.atm.pojo.UsedBill;
import com.zinkworks.challenge.atm.repository.AccountRepository;
import com.zinkworks.challenge.atm.repository.WithdrawalRepository;
import com.zinkworks.challenge.atm.validation.AccountNotFoundException;
import com.zinkworks.challenge.atm.validation.MismatchedPinException;
import com.zinkworks.challenge.atm.validation.NotEnoughBillsException;
import com.zinkworks.challenge.atm.validation.NotEnoughFundsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    final AccountRepository accountRepository;
    final WithdrawalRepository withdrawalRepository;
    final BillService billService;

    final PinService pinService;

    public AccountService(
        final AccountRepository accountRepository,
        final WithdrawalRepository withdrawalRepository,
        final BillService billService, final PinService pinService) {
        this.accountRepository = accountRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.billService = billService;
        this.pinService = pinService;
    }


    public BalanceRead computeCurrentBalance(final String accountNumber, final String pin)
        throws AccountNotFoundException, MismatchedPinException {
        final Account account = fetchAccount(accountNumber, pin);

        return BalanceRead.builder()
                          .balance(account.getBalance())
                          .maxWithdrawalAmount(account.maxWithdrawalAmount())
                          .accountNumber(accountNumber)
                          .build();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public WithdrawalRead createWithdrawal(
        final String accountNumber,
        final String pin,
        final WithdrawalCreate withdrawalCreate)
        throws AccountNotFoundException, NotEnoughBillsException,
               NotEnoughFundsException, MismatchedPinException {

        final int withdrawalAmount = withdrawalCreate.getAmount();

        final Account account = fetchAccount(accountNumber, pin);

        checkForEnoughFunds(account.maxWithdrawalAmount(), withdrawalAmount);

        final List<Bill> allBills = billService.fetchAllBillsByDescendingFaceValue();

        final List<UsedBill> usedBills = billService.optimalBillsCombination(allBills, withdrawalAmount);

        billService.updateBills(usedBills);

        account.updateBalance(-withdrawalAmount);
        accountRepository.save(account);

        Withdrawal withdrawal = buildWithdrawal(account, usedBills, withdrawalAmount);
        withdrawalRepository.save(withdrawal);

        List<DispensedBillRead> dispensedBillReads = usedBills.stream()
                                                              .map(
                                                                  usedBill -> new DispensedBillRead(
                                                                      usedBill.getBill().getFaceValue(),
                                                                      usedBill.getQuantity()))
                                                              .toList();

        return WithdrawalRead.builder()
                             .accountNumber(accountNumber)
                             .remainingBalance(account.getBalance())
                             .dispensedBills(dispensedBillReads)
                             .build();
    }

    private Account fetchAccount(final String accountNumber, final String pin)
        throws AccountNotFoundException, MismatchedPinException {
        final Optional<Account> optionalAccount = accountRepository.findByNumber(accountNumber);

        if (optionalAccount.isEmpty()) {
            throw new AccountNotFoundException(String.format("Account with number=%s was not found.", accountNumber));
        }

        Account account = optionalAccount.get();
        pinService.pinMatches(pin, account.getPin());
        return account;
    }

    private void checkForEnoughFunds(final int maxAmount, final int requestedAmount)
        throws NotEnoughFundsException {
        if (requestedAmount > maxAmount) {
            throw new NotEnoughFundsException(
                String.format("The requested amount=%d exceeds the maximum withdrawal funds=%d.", maxAmount,
                              requestedAmount));
        }
    }

    private Withdrawal buildWithdrawal(
        final Account account,
        final List<UsedBill> usedBills,
        final int withdrawalAmount) {

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAccountId(account.getId());
        withdrawal.setAmount(withdrawalAmount);

        for (UsedBill usedBill : usedBills) {
            final DispensedBill
                dispensedBill = DispensedBill.builder()
                                             .billId(usedBill.getBill().getId())
                                             .quantity(usedBill.getQuantity())
                                             .build();
            withdrawal.addDispensedBill(dispensedBill);
        }

        return withdrawal;
    }
}

