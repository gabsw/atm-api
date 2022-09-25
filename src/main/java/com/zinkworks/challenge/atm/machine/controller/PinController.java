package com.zinkworks.challenge.atm.machine.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// This was created with the sole purpose of encoding the pin

@RestController
@RequestMapping("api/v1/pin")
public class PinController {
    @GetMapping("")
    public Map<String, String> hashPin(@RequestHeader("Authorization") final String pin) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(10);
        return Map.of("hash", bcrypt.encode(pin));
    }
}
