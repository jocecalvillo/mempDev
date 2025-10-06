package com.mx.feenicia.memphis.common.feenicia;


import com.mx.feenicia.memphis.common.model.Data;

@lombok.Data
public class AtenaResponse {
    private boolean success;
    private String code;
    private String description;
    private Data data;
    private Long claveOperacion;
}
