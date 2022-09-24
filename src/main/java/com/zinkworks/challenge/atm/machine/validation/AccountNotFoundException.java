package com.zinkworks.challenge.atm.machine.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(final String message) {
        super(message);
    }
}
