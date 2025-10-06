package com.mx.feenicia.memphis.common.model;

@lombok.Data
public class MemphisCancelResponse {
    private boolean success;
    private String code;
    private String description;
    private Data data;
    private Long claveOperacion;
}
