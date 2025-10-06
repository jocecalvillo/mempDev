package com.mx.feenicia.memphis.commom.soter.dictionary;

public enum SoterResponseCodes {
    SUCCESS_OPERATION("00");

    private String responseCode;

    SoterResponseCodes(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseCode() {
        return responseCode;
    }
}
