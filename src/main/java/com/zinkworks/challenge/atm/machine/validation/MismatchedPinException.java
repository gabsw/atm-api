package com.zinkworks.challenge.atm.machine.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class MismatchedPinException extends Exception {

    public MismatchedPinException(final String message) {
        super(message);
    }
}
