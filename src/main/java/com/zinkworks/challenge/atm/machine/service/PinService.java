package com.zinkworks.challenge.atm.machine.service;

import com.zinkworks.challenge.atm.machine.validation.MismatchedPinException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PinService {
    private final static int BCRYPT_STRENGTH = 10;
    public void pinMatches(final String pin, final String hashedPin) throws MismatchedPinException {
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder(BCRYPT_STRENGTH); // automatically creates a salted pw
        if (!bCrypt.matches(pin, hashedPin)) {
            throw new MismatchedPinException("The account number and pin combination do not match any account");
        }
    }
}
