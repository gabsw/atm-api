package com.zinkworks.challenge.atm.machine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalCreate {
    @Min(1) // TODO: What if they request a value that is not compatible with the amount of notes?
    private int amount;
}
