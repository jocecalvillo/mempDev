package com.mx.feenicia.memphis.commom.util;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MemphisLogUtils {

    private static final Logger log = LoggerFactory.getLogger(MemphisLogUtils.class);

    private MemphisLogUtils() {} // evitar instanciación

    public static void logMemphisResponseDetails(JsonObject jsonObjectCharge) {
        log.info("Memphis Response (Sale)");
        String[] fieldsToLog = {"request_date","id","request_id","request_status","resp_code","description",
                "authorization","http_code","trace_id"};

        for (String field : fieldsToLog) {
            if (jsonObjectCharge.has(field) && !jsonObjectCharge.get(field).isJsonNull()) {
                log.info("{}: {}", getFieldDisplayName(field), jsonObjectCharge.get(field).getAsString());
            }
        }
    }

    private static String getFieldDisplayName(String fieldName) {
        switch (fieldName) {
            case "request_id": return "Request id";
            case "request_date": return "Date";
            case "description": return "Description";
            case "resp_code": return "Response code";
            case "request_status": return "Status";
            case "authorization": return "Authorization";
            case "http_code": return "Http Code";
            case "id": return "Id";
            case "trace_id": return "Trace id";
            default: return fieldName;
        }
    }
}
