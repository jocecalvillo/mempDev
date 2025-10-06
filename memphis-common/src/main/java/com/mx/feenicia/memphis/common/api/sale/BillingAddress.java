package com.mx.feenicia.memphis.common.api.sale;

import lombok.Data;

@Data
public class BillingAddress {
    private String street;
    private String street2;
    private String state;
    private String country;
    private String zipCode;
}
