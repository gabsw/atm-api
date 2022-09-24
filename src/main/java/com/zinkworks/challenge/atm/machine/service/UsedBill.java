package com.zinkworks.challenge.atm.machine.service;

import com.zinkworks.challenge.atm.machine.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UsedBill {
    private Bill bill;
    private int quantity;
}
