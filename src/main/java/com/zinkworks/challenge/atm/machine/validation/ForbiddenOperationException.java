package com.zinkworks.challenge.atm.machine.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenOperationException extends Exception {

    public ForbiddenOperationException(final String message) {
        super(message);
    }
}
