package com.zinkworks.challenge.atm.machine.service;

import com.zinkworks.challenge.atm.machine.dto.BalanceRead;
import com.zinkworks.challenge.atm.machine.dto.DispensedBillRead;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalCreate;
import com.zinkworks.challenge.atm.machine.dto.WithdrawalRead;
import com.zinkworks.challenge.atm.machine.entity.Account;
import com.zinkworks.challenge.atm.machine.entity.Bill;
import com.zinkworks.challenge.atm.machine.entity.DispensedBill;
import com.zinkworks.challenge.atm.machine.entity.Withdrawal;
import com.zinkworks.challenge.atm.machine.pojo.UsedBill;
import com.zinkworks.challenge.atm.machine.repository.AccountRepository;
import com.zinkworks.challenge.atm.machine.repository.WithdrawalRepository;
import com.zinkworks.challenge.atm.machine.validation.AccountNotFoundException;
import com.zinkworks.challenge.atm.machine.validation.MismatchedPinException;
import com.zinkworks.challenge.atm.machine.validation.NotEnoughBillsException;
import com.zinkworks.challenge.atm.machine.validation.NotEnoughFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class AccountServiceTests {

    private final static String ACCOUNT_NUMBER = "123456";
    private final static String HASHED_PIN = "$2a$10$lbZQx9HMVIQSVlF.VS.CS.dOJW7ltWs0mmDqZCUA6vhP37sNV3op6";
    private final static Instant createdAt = Instant.parse("2022-09-25T10:00:00Z");

    @Mock
    AccountRepository accountRepository;

    @Mock
    WithdrawalRepository withdrawalRepository;

    @Mock
    BillService billService;

    @Mock
    PinService pinService;

    @InjectMocks
    AccountService accountService;

    @BeforeEach
    void setUp() throws MismatchedPinException {
        MockitoAnnotations.openMocks(this);
        doNothing().when(pinService).pinMatches(eq(HASHED_PIN), any());
    }

    @Test
    void computeBalanceForExistingAccount() throws AccountNotFoundException, MismatchedPinException {
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.ofNullable(buildAccount()));

        final BalanceRead balanceRead = accountService.computeCurrentBalance(ACCOUNT_NUMBER, HASHED_PIN);

        assertEquals(100, balanceRead.getBalance());
        assertEquals(150, balanceRead.getMaxWithdrawalAmount());
        assertEquals(ACCOUNT_NUMBER, balanceRead.getAccountNumber());
    }

    @Test
    void accountNotFound() {
        when(accountRepository.findByNumber("999")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                     () -> accountService.computeCurrentBalance("999", HASHED_PIN));
    }

    @Test
    void createWithdrawal()
        throws NotEnoughFundsException, MismatchedPinException, NotEnoughBillsException, AccountNotFoundException {
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.ofNullable(buildAccount()));
        when(billService.fetchAllBillsByDescendingFaceValue()).thenReturn(createStartingBills());
        when(billService.optimalBillsCombination(createStartingBills(), 85)).thenReturn(createUsedBills());
        when(billService.updateBills(createUsedBills())).thenReturn(createBillsToUpdate());

        final Account accountToUpdate = Account.builder()
                                               .id(12345)
                                               .balance(15)
                                               .pin(HASHED_PIN)
                                               .overdraft(50)
                                               .number(ACCOUNT_NUMBER)
                                               .build();

        when(accountRepository.save(accountToUpdate)).thenReturn(accountToUpdate);

        final Withdrawal withdrawal = Withdrawal.builder()
                                                .accountId(12345)
                                                .amount(85)
                                                .bills(createDispensedBills())
                                                .build();

        when(withdrawalRepository.save(withdrawal)).thenReturn(withdrawal);

        final WithdrawalRead withdrawalRead = accountService.createWithdrawal(ACCOUNT_NUMBER,
                                                                              HASHED_PIN,
                                                                              new WithdrawalCreate(85));

        final List<DispensedBillRead> expectedBills = List.of(new DispensedBillRead(5, 1),
                                                              new DispensedBillRead(10, 1),
                                                              new DispensedBillRead(20, 1),
                                                              new DispensedBillRead(50, 1));

        assertEquals(ACCOUNT_NUMBER, withdrawalRead.getAccountNumber());
        assertEquals(15, withdrawalRead.getRemainingBalance());
        assertEquals(expectedBills, withdrawalRead.getDispensedBills());
    }

    @Test
    void notEnoughFunds() {
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.ofNullable(buildAccount()));

        assertThrows(NotEnoughFundsException.class,
                     () -> accountService.createWithdrawal(ACCOUNT_NUMBER, HASHED_PIN,
                                                           new WithdrawalCreate(999999999)));
    }

    private Account buildAccount() {
        return Account.builder()
                      .id(12345)
                      .balance(100)
                      .pin(HASHED_PIN)
                      .overdraft(50)
                      .number(ACCOUNT_NUMBER)
                      .build();
    }

    private List<Bill> createStartingBills() {
        final Bill fiftyBill = Bill.builder()
                                   .id(123)
                                   .faceValue(50)
                                   .quantity(10)
                                   .createdAt(createdAt)
                                   .build();

        final Bill twentyBill = Bill.builder()
                                    .id(456)
                                    .faceValue(20)
                                    .quantity(30)
                                    .createdAt(createdAt)
                                    .build();

        final Bill tenBill = Bill.builder()
                                 .id(789)
                                 .faceValue(10)
                                 .quantity(30)
                                 .createdAt(createdAt)
                                 .build();

        final Bill fiveBill = Bill.builder()
                                  .id(321)
                                  .faceValue(5)
                                  .quantity(20)
                                  .createdAt(createdAt)
                                  .build();

        return List.of(fiftyBill, twentyBill, tenBill, fiveBill);
    }

    private List<UsedBill> createUsedBills() {
        final Bill fiveBill = Bill.builder()
                                  .id(321)
                                  .faceValue(5)
                                  .quantity(20)
                                  .createdAt(createdAt)
                                  .build();

        final UsedBill fiveUsedBill = UsedBill.builder()
                                              .bill(fiveBill)
                                              .quantity(1)
                                              .build();

        final Bill tenBill = Bill.builder()
                                 .id(789)
                                 .faceValue(10)
                                 .quantity(30)
                                 .createdAt(createdAt)
                                 .build();

        final UsedBill tenUsedBill = UsedBill.builder()
                                             .bill(tenBill)
                                             .quantity(1)
                                             .build();

        final Bill twentyBill = Bill.builder()
                                    .id(456)
                                    .faceValue(20)
                                    .quantity(30)
                                    .createdAt(createdAt)
                                    .build();

        final UsedBill twentyUsedBill = UsedBill.builder()
                                                .bill(twentyBill)
                                                .quantity(1)
                                                .build();

        final Bill fiftyBill = Bill.builder()
                                   .id(123)
                                   .faceValue(50)
                                   .quantity(10)
                                   .createdAt(createdAt)
                                   .build();

        final UsedBill fiftyUsedBill = UsedBill.builder()
                                               .bill(fiftyBill)
                                               .quantity(1)
                                               .build();

        return List.of(fiveUsedBill, tenUsedBill, twentyUsedBill, fiftyUsedBill);
    }

    private List<DispensedBill> createDispensedBills() {
        final DispensedBill fiftyDispensedBill = DispensedBill.builder()
                                                               .billId(123)
                                                               .quantity(1)
                                                               .build();

        final DispensedBill twentyDispensedBill = DispensedBill.builder()
                                                               .billId(456)
                                                               .quantity(1)
                                                               .build();

        final DispensedBill tenDispensedBill = DispensedBill.builder()
                                                               .billId(789)
                                                               .quantity(1)
                                                               .build();

        final DispensedBill fiveDispensedBill = DispensedBill.builder()
                                                               .billId(321)
                                                               .quantity(1)
                                                               .build();
        return List.of(fiftyDispensedBill, twentyDispensedBill, tenDispensedBill, fiveDispensedBill);
    }

    private List<Bill> createBillsToUpdate() {
        final Bill updatedFiftyBill = Bill.builder()
                                           .id(123)
                                           .faceValue(50)
                                           .quantity(9)
                                           .createdAt(createdAt)
                                           .build();

        final Bill updatedTwentyBill = Bill.builder()
                                        .id(456)
                                        .faceValue(20)
                                        .quantity(29)
                                        .createdAt(createdAt)
                                        .build();

        final Bill updatedTenBill = Bill.builder()
                                        .id(789)
                                        .faceValue(10)
                                        .quantity(29)
                                        .createdAt(createdAt)
                                        .build();

        final Bill updatedFiveBill = Bill.builder()
                                         .id(321)
                                         .faceValue(5)
                                         .quantity(19)
                                         .createdAt(createdAt)
                                         .build();

        return List.of(updatedFiveBill, updatedTenBill, updatedTwentyBill, updatedFiftyBill);
    }
}
