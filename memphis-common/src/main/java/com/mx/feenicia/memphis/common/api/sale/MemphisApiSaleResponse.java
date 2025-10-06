package com.mx.feenicia.memphis.common.api.sale;

import lombok.Data;

@Data
public class MemphisApiSaleResponse {
    private String request_id;
    private String request_date;
    private boolean request_status;
    private int http_code;
    private String resp_code;
    private String description;
    private String authorization;
    private String id;
    private String trace_id;
    private BinInformation binInformation;
}
