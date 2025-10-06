package com.mx.feenicia.memphis.common.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AtenaTxByRequest {

    @NotNull
    @Positive
    private Long transactionId;

    @NotNull
    @Positive
    private Long refundTransactionId;
}
