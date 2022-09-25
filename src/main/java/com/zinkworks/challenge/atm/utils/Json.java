package com.zinkworks.challenge.atm.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Json {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(final Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}
