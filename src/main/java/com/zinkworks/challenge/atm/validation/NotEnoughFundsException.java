package com.zinkworks.challenge.atm.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "not enough funds for withdrawal")
public class NotEnoughFundsException extends Exception {

    public NotEnoughFundsException(final String message) {
        super(message);
    }
}
