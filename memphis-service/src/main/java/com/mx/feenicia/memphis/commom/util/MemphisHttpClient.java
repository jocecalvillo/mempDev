package com.mx.feenicia.memphis.commom.util;

import com.mx.feenicia.memphis.commom.exception.MemphisResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static com.mx.feenicia.memphis.common.dictionary.ResponseCode.COMMUNICATION_ERROR;


@Component
public class MemphisHttpClient {

    private static final Logger log = LoggerFactory.getLogger(MemphisHttpClient.class);


    private final RestTemplate restTemplate;
    //private final String memphisApiUrl;


    public MemphisHttpClient(RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;
    }

    /**
     * Método genérico para invocar POST JSON contra Memphis.
     */
    public String postJson(
            Object payload,
            String tokenResponse,
            String timestamp,
            String relativePath,    // p.ej. "/streampay/v1/ecommerce/charge"
            String commerceName,
            String commerceId
    )
    {
        try {


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("commerce-name", commerceName);
            headers.set("X-Auth", tokenResponse);
            headers.set("commerce-id", commerceId);
            headers.set("X-Time", timestamp);

            HttpEntity<Object> entity = new HttpEntity<>(payload, headers);

            String url =  relativePath;

            ResponseEntity<String> response =  restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            log.info("Memphis POST {} -> {}", relativePath, response.getStatusCode());
            return response.getBody();

        } catch (HttpStatusCodeException e) {
            log.error("HTTP {} calling Memphis: {}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            throw new MemphisResponseException(COMMUNICATION_ERROR);
        } catch (Exception e) {
            log.error("Error calling Memphis: {}", e.getMessage(), e);
            throw new MemphisResponseException(COMMUNICATION_ERROR);
        }
    }
}
