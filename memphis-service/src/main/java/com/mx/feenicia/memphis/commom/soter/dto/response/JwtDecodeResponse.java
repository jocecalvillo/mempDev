package com.mx.feenicia.memphis.commom.soter.dto.response;

import lombok.Data;

import java.util.Map;


@Data
public class JwtDecodeResponse {
    private String responseCode;
    private String responseMessage;
    private Map<String, String> payload;
}
