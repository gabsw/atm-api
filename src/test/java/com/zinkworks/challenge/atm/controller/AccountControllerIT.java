package com.zinkworks.challenge.atm.controller;

import com.zinkworks.challenge.atm.Application;
import com.zinkworks.challenge.atm.dto.BalanceRead;
import com.zinkworks.challenge.atm.dto.DispensedBillRead;
import com.zinkworks.challenge.atm.dto.WithdrawalCreate;
import com.zinkworks.challenge.atm.dto.WithdrawalRead;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.zinkworks.challenge.atm.utils.Json.toJson;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = Application.class)
@AutoConfigureMockMvc
@Sql(scripts = {
    "wipe_database.sql",
    "seed_database.sql"
})
public class AccountControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getsInitialBalance() throws Exception {
        final BalanceRead balanceRead = BalanceRead.builder()
                                                   .balance(800)
                                                   .accountNumber("123456789")
                                                   .maxWithdrawalAmount(1000)
                                                   .build();

        mockMvc.perform(get("/api/v1/account/123456789/balance")
                            .header(AUTHORIZATION, 1234)
                            .contentType(APPLICATION_JSON)
                       )
               .andExpect(status().isOk())
               .andExpect(content().json(toJson(balanceRead)));
    }

    @Test
    void withdrawsValidAmount() throws Exception {
        final WithdrawalCreate validWithdrawal = new WithdrawalCreate(15);

        final WithdrawalRead withdrawalConfirmation = WithdrawalRead.builder()
                                                                    .remainingBalance(785)
                                                                    .accountNumber("123456789")
                                                                    .dispensedBills(List.of(new DispensedBillRead(5, 1),
                                                                                            new DispensedBillRead(10,
                                                                                                                  1)))
                                                                    .build();

        mockMvc.perform(post("/api/v1/account/123456789/withdrawal")
                            .header(AUTHORIZATION, 1234)
                            .contentType(APPLICATION_JSON)
                            .content(toJson(validWithdrawal))
                       )
               .andExpect(status().isCreated())
               .andExpect(content().json(toJson(withdrawalConfirmation)));
    }

    @Test
    void withdrawsAmountThatDoesntMatchBills() throws Exception {
        final WithdrawalCreate invalidWithdrawal = new WithdrawalCreate(77);

        mockMvc.perform(post("/api/v1/account/123456789/withdrawal")
                            .header(AUTHORIZATION, 1234)
                            .contentType(APPLICATION_JSON)
                            .content(toJson(invalidWithdrawal))
                       )
               .andExpect(status().isBadRequest());
    }

    @Test
    void withdrawsWithoutEnoughFunds() throws Exception {
        final WithdrawalCreate excessiveWithdrawal = new WithdrawalCreate(55555555);

        mockMvc.perform(post("/api/v1/account/123456789/withdrawal")
                            .header(AUTHORIZATION, 1234)
                            .contentType(APPLICATION_JSON)
                            .content(toJson(excessiveWithdrawal))
                       )
               .andExpect(status().isBadRequest())
               .andExpect(status().reason(containsString("not enough funds for withdrawal"))); ;
    }

    @Test
    @Sql(scripts = {"wipe_bills.sql"})
    void withdrawsWithoutEnoughBills() throws Exception {
        final WithdrawalCreate excessiveWithdrawal = new WithdrawalCreate(5);

        mockMvc.perform(post("/api/v1/account/123456789/withdrawal")
                            .header(AUTHORIZATION, 1234)
                            .contentType(APPLICATION_JSON)
                            .content(toJson(excessiveWithdrawal))
                       )
               .andExpect(status().isConflict())
               .andExpect(status().reason(containsString("not enough bills to fulfill withdrawal"))); ;
    }

    @Test
    void mismatchedPin() throws Exception {
        final WithdrawalCreate mismatchedWithdrawal = new WithdrawalCreate(100);

        mockMvc.perform(post("/api/v1/account/123456789/withdrawal")
                            .header(AUTHORIZATION, 999999)
                            .contentType(APPLICATION_JSON)
                            .content(toJson(mismatchedWithdrawal))
                       )
               .andExpect(status().isForbidden())
               .andExpect(status().reason(containsString("account and pin combination not found")));
    }

    @Test
    void missingAuthorization() throws Exception {
        final WithdrawalCreate withdrawalWithoutPin = new WithdrawalCreate(100);

        mockMvc.perform(post("/api/v1/account/123456789/withdrawal")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(withdrawalWithoutPin))
                       )
               .andExpect(status().isBadRequest())
               .andExpect(status().reason(containsString(
                   "Required request header 'Authorization' for method parameter type String is not present")));
    }
}
