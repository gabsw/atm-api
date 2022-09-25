package com.zinkworks.challenge.atm.machine.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// TODO: Internal Server Error
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NotEnoughBillsException extends Exception {

    public NotEnoughBillsException(final String message) {
        super(message);
    }
}
