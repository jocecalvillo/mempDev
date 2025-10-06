package com.mx.feenicia.memphis.common.api.common;


import com.mx.feenicia.memphis.common.api.sale.BinInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemphisApiTxResponse {
    private String request_id;
    private String request_date;
    private boolean request_status;
    private String resp_code;
    private String description;
    private String authorization;
    private String id;
    private String trace_id;
    private BinInformation binInformation;
}
