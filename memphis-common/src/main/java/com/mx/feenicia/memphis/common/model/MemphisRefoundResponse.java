package com.mx.feenicia.memphis.common.model;

@lombok.Data
public class MemphisRefoundResponse {
    private boolean success;
    private String code;
    private String description;
    private Data data;
    private Long claveOperacion;
}
