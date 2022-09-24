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
import com.zinkworks.challenge.atm.machine.repository.BillRepository;
import com.zinkworks.challenge.atm.machine.repository.WithdrawalRepository;
import com.zinkworks.challenge.atm.machine.validation.AccountNotFoundException;
import com.zinkworks.challenge.atm.machine.validation.WithdrawalNotAllowedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    final AccountRepository accountRepository;

    final BillRepository billRepository;

    final WithdrawalRepository withdrawalRepository;

    public AccountService(
        final AccountRepository accountRepository,
        final BillRepository billRepository,
        final WithdrawalRepository withdrawalRepository) {
        this.accountRepository = accountRepository;
        this.billRepository = billRepository;
        this.withdrawalRepository = withdrawalRepository;
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
        throws AccountNotFoundException, WithdrawalNotAllowedException {

        final int withdrawalAmount = withdrawalCreate.getAmount();

        // TODO: GET ACCOUNT
        final Account account = fetchAccount(accountNumber);

        // TODO: CHECK IF USER HAS MONEY
        checkIfWithdrawalIsPossible(account.maxWithdrawalAmount(), withdrawalAmount);

        // TODO: GET AVAILABLE BILLS
        final List<Bill> allBills = billRepository.findAllByOrderByFaceValueDesc();

        // TODO: CAN WE PAY WITH OUR BILLS
        final List<UsedBill> usedBills = optimalBillsCombination(allBills, withdrawalAmount);
        // TODO: UPDATE BILLS
        final List<Bill> billsToUpdate = getBillsToUpdate(usedBills);
        billRepository.saveAll(billsToUpdate);
        // TODO: UPDATE BALANCE
        account.updateBalance(-withdrawalAmount);
        accountRepository.save(account);
        // TODO: ADD WITHDRAWAL

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

    private void checkIfWithdrawalIsPossible(final int maxAmount, final int requestedAmount)
        throws WithdrawalNotAllowedException {
        if (requestedAmount > maxAmount) {
            // TODO: Improves to add values
            throw new WithdrawalNotAllowedException("The requested amount exceeds the maximum withdrawal funds.");
        }
    }

    private List<UsedBill> optimalBillsCombination(final List<Bill> allBills, final int requestedAmount)
        throws WithdrawalNotAllowedException {
        List<UsedBill> optimalBills = new ArrayList<>();
        int leftOverAmount = requestedAmount;

        for (Bill bill : allBills) {
            int totalBills = leftOverAmount / bill.getFaceValue();

            if (totalBills <= 0) {
                continue;
            }

            leftOverAmount -= bill.getFaceValue() * totalBills;
            optimalBills.add(new UsedBill(bill, totalBills));
        }

        if (leftOverAmount > 0) {
            throw new WithdrawalNotAllowedException("Not enough bills in the ATM to comply with withdrawal");
        }
        return optimalBills;
    }

    // O (n)
    private List<Bill> getBillsToUpdate(final List<UsedBill> usedBills) {
        for (UsedBill usedBill : usedBills) {
            final Bill originalBill = usedBill.getBill();
            final int newQuantity = originalBill.getQuantity() - usedBill.getQuantity();
            originalBill.setQuantity(newQuantity);
        }
        return usedBills.stream().map(UsedBill::getBill).toList();
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

