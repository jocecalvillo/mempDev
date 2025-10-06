package com.mx.feenicia.memphis.commom.util;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MemphisLogUtilsTest {

    @Test
    void logMemphisResponseDetails_handlesTypicalFields() {
        JsonObject json = new JsonObject();
        json.addProperty("request_date", "2025-01-01T00:00:00Z");
        json.addProperty("id", "ABC123");
        json.addProperty("request_id", "REQ-1");
        json.addProperty("request_status", "OK");
        json.addProperty("resp_code", "00");
        json.addProperty("description", "Approved");
        json.addProperty("authorization", "AUTH01");
        json.addProperty("http_code", "200");
        json.addProperty("trace_id", "TRACE");

        assertDoesNotThrow(() -> MemphisLogUtils.logMemphisResponseDetails(json));
    }

    @Test
    void logMemphisResponseDetails_handlesNulls() {
        JsonObject json = new JsonObject();
        // missing properties -> should not throw
        assertDoesNotThrow(() -> MemphisLogUtils.logMemphisResponseDetails(json));
    }
}