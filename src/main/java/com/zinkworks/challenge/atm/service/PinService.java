package com.zinkworks.challenge.atm.service;

import com.zinkworks.challenge.atm.validation.MismatchedPinException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PinService {
    public static final int BCRYPT_STRENGTH = 10;

    public void pinMatches(final String accountNumber, final String pin, final String hashedPin)
        throws MismatchedPinException {
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder(BCRYPT_STRENGTH); // automatically creates a salted pw
        if (!bCrypt.matches(pin, hashedPin)) {
            log.info("Failed to verify pin for account {}", accountNumber);
            throw new MismatchedPinException("The account number and pin combination do not match any account");
        }
    }
}
