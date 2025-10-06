package com.mx.feenicia.memphis.commom.soter.dto.request;

import lombok.Data;

@Data
public class JwtVerifyRequest {

    private String token;
    private String secret;
}
