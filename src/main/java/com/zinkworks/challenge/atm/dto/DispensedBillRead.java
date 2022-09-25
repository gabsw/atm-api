package com.zinkworks.challenge.atm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DispensedBillRead {
    private int faceValue;
    private int quantity;
}
