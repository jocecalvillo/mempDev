package com.mx.feenicia.memphis.commom.config.auth;

import lombok.Data;

import java.util.Map;

@Data
public class JwtPayloadBuilder {
    private String merchantId;
    private String affiliation;

    public JwtPayloadBuilder(Map<String,String> payload) {
        if (payload != null) {
            this.merchantId = payload.get("merchantId");
            this.affiliation = payload.get("affiliation");
        }
    }
}
