package com.mx.feenicia.memphis.common.api.sale;

import lombok.Data;

@Data

public class CardInformation {
    private String holder_name;
    private String card_number;
    private String last4;
    private String bin;
    private String expiration_year;
    private String expiration_month;
    private String cvv;
}
