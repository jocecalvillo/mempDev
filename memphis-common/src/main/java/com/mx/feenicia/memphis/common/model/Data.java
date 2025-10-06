package com.mx.feenicia.memphis.common.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Data {
    private String approvedCode;
    private String processCode;
    private String referenceCode;
    private String resultCode;
    private String description;
    private double totalAmount;
    private Date transactionDate;
    private String tokenId;
}
