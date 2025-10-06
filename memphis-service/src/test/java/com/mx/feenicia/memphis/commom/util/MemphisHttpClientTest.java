package com.mx.feenicia.memphis.commom.util;

import com.mx.feenicia.memphis.commom.exception.MemphisResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MemphisHttpClientTest {

    private RestTemplate restTemplate;
    private MemphisHttpClient client;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        client = new MemphisHttpClient(restTemplate);
    }

    @Test
    void postJson_success_returnsBody_andSendsHeaders() {
        String uri = "https://example.com/charge";
        String bodyResponse = "{\"ok\":true}";
        ResponseEntity<String> response = new ResponseEntity<>(bodyResponse, HttpStatus.OK);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        String result = client.postJson(new Object(), "TKA-XYZ", "1700000000", uri, "MyShop", "C001");

        assertEquals(bodyResponse, result);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<HttpEntity<Object>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(uri), eq(HttpMethod.POST), entityCaptor.capture(), eq(String.class));

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals("MyShop", headers.getFirst("commerce-name"));
        assertEquals("TKA-XYZ", headers.getFirst("X-Auth"));
        assertEquals("C001", headers.getFirst("commerce-id"));
        assertEquals("1700000000", headers.getFirst("X-Time"));
    }

    @Test
    void postJson_httpError_wrapsInMemphisResponseException() {
        String uri = "https://example.com/charge";
        HttpStatusCodeException httpEx = new HttpStatusCodeException(HttpStatus.BAD_REQUEST, "bad"){};
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(httpEx);

        assertThrows(MemphisResponseException.class,
                () -> client.postJson(new Object(), "TKA", "ts", uri, "shop", "C001"));
    }

    @Test
    void postJson_otherError_wrapsInMemphisResponseException() {
        String uri = "https://example.com/charge";
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("boom"));

        assertThrows(MemphisResponseException.class,
                () -> client.postJson(new Object(), "TKA", "ts", uri, "shop", "C001"));
    }
}