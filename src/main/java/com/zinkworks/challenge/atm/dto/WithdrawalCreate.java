package com.zinkworks.challenge.atm.dto;

import com.zinkworks.challenge.atm.validation.AllowedAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalCreate {
    @AllowedAmount
    private int amount;
}
