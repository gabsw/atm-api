package com.zinkworks.challenge.atm.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "not enough bills to fulfill withdrawal")
public class NotEnoughBillsException extends Exception {

    public NotEnoughBillsException(final String message) {
        super(message);
    }
}
