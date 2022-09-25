package com.zinkworks.challenge.atm.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.zinkworks.challenge.atm.service.PinService.BCRYPT_STRENGTH;

@RestController
@RequestMapping("api/v1/pin")
public class PinController {
    // This was created with the sole purpose of encoding the pin
    @GetMapping("")
    public Map<String, String> hashPin(@RequestHeader("Authorization") final String pin) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
        return Map.of("hash", bcrypt.encode(pin));
    }
}
