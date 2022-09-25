package com.zinkworks.challenge.atm.machine.dto;

import com.zinkworks.challenge.atm.machine.validation.AllowedAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalCreate {
    @AllowedAmount
    private int amount;
}
