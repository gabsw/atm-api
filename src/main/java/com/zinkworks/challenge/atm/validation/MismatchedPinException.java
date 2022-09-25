package com.zinkworks.challenge.atm.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "account and pin combination not found")
public class MismatchedPinException extends Exception {

    public MismatchedPinException(final String message) {
        super(message);
    }
}
