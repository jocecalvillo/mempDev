package com.mx.feenicia.memphis.common.dictionary;

public enum ResponseCode {

    SUCCESS("00", "Success Operation"),
    BAD_REQUEST("MF400", "Data validation failed."),
    TRANSACTION_NOT_FOUND("MF401", "Feenicia transaction not found"),
    TRANSACTION_ALREADY_EXISTS("MF402", "Feenicia transaction already exists"),
    MERCHANT_NOT_FOUND("MF403", "Merchant not found"),
    TRANSACTION_ALREADY_REFUNDED("MF405", "Transaction already refunded"),
    TRANSACTION_REFUND_REQUESTED("MF406", "Transaction refund was already requested"),
    TRANSACTION_CANNOT_BE_REFUNDED("MF407", "Transaction cannot be refunded"),
    TRANSACTION_CANNOT_BE_REFUNDED_TIMEFRAME("MF408", "Request surpasses the timeframe"),
    ERROR_MANUAL_SALE_ENDPOINT("MF411","Error While Consuming Manual Sale Endpoint"),
    ATENA_CARDHOLDERNAME_EMPTY("MF4013","Cardholder cannot be empty"),


    INTERNAL_ERROR("MF599", "Internal Error"),
    CONFIGURATION_ERROR("MF600", "Configuration Error" ),
    TRANSACTION_DECLINED("MF601", "Transaction declined"),
    RESPONSE_PROCESSING_ERROR("MF602", "Response processing error" ),
    COMMUNICATION_ERROR("MF603", "Communication error" ),
    INVALID_RESPONSE_FORMAT("MF604", "Transaction declined"),
    TOKEN_GENERATION_ERROR("MF605", "Token generation error"),
    INVALID_CARD_NUMBER("MF606", "Invalid card number" ),

    CONFIGURATION_NOT_FOUND("MS607", "Configuration not found"),

    SAVE_TRANSACTION_ERROR("MS608", "Error saving transaction"),

    UPDATE_TRANSACTION_ERROR("MS609", "Error updating transaction");




    private final String code;
    private final String description;

    ResponseCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
