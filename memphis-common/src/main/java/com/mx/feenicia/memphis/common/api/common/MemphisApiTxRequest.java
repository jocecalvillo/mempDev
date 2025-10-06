package com.mx.feenicia.memphis.common.api.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemphisApiTxRequest {
    private String isoType;
    private String amount;
    private String reference;
    private String entry_mode;
    private String authentication;
    private String draftCapture;
    private boolean tpv;
    private boolean periodic;
}
