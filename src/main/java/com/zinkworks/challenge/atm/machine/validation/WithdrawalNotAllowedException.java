package com.zinkworks.challenge.atm.machine.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WithdrawalNotAllowedException extends Exception {

    public WithdrawalNotAllowedException(final String message) {
        super(message);
    }
}
