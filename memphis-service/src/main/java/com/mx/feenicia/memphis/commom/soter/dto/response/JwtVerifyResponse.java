package com.mx.feenicia.memphis.commom.soter.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class JwtVerifyResponse {

    private String responseCode;
    private String responseMessage;
    private boolean valid;
    private Map<String, String> payload;
}
