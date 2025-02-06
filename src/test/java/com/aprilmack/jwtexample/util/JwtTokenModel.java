package com.aprilmack.jwtexample.util;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class JwtTokenModel {
    private final Map<String, Object> header;
    private final Map<String, Object> payload;
}
