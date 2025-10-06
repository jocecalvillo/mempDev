package com.mx.feenicia.memphis.common.authentication;

import lombok.Data;

@Data
public class ClientUser {

    private String merchantId;
    private String publicMerchantId;
    private String privateMerchantId;
}
