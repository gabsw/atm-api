package com.zinkworks.challenge.atm.machine.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NotEnoughFundsException extends Exception {

    public NotEnoughFundsException(final String message) {
        super(message);
    }
}
