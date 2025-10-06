package com.mx.feenicia.memphis.commom.exception;

import com.mx.feenicia.memphis.common.dictionary.ResponseCode;
import org.springframework.http.HttpStatus;

public class MemphisResponseException extends RuntimeException {

    private final String responseCode;
    private final String description;
    private final HttpStatus httpStatus;

    // Constructor con ResponseCode por defecto NOT_ACCEPTABLE
    public MemphisResponseException(ResponseCode responseCode) {
        this(responseCode, HttpStatus.NOT_ACCEPTABLE);
    }

    // Constructor con ResponseCode y HttpStatus
    public MemphisResponseException(ResponseCode responseCode, HttpStatus httpStatus) {
        this(responseCode.getCode(), responseCode.getDescription(), httpStatus);
    }

    // Constructor completo con código, descripción y HttpStatus
    public MemphisResponseException(String responseCode, String description, HttpStatus httpStatus) {
        super(String.format("Response Code %s: %s", responseCode, description));
        this.responseCode = responseCode;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    // Constructor con ResponseCode, HttpStatus y Throwable
    public MemphisResponseException(ResponseCode responseCode, HttpStatus httpStatus, Throwable throwable) {
        super(String.format("Response Code %s: %s", responseCode.getCode(), responseCode.getDescription()), throwable);
        this.responseCode = responseCode.getCode();
        this.description = responseCode.getDescription();
        this.httpStatus = httpStatus;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getDescription() {
        return description;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    }
