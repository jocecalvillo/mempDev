package com.mx.feenicia.memphis.common.api.sale;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class MemphisApiSaleRequest {
    private String isoType;
    private String amount;
    private String currency;
    private String reference;
    private CardInformation cardInformation;
    private String entry_mode;
    private String authentication;
    private String draftCapture;
    private boolean tpv;
    private boolean periodic;
    private BillingAddress billingAddress;
    private String email;
    private String clientip;
}
