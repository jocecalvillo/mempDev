package com.mx.feenicia.memphis.commom.soter.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mx.feenicia.memphis.commom.soter.dto.request.JwtDecodeRequest;
import com.mx.feenicia.memphis.commom.soter.dto.response.JwtDecodeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class JwtDecodeService {

    private static final Logger log = LoggerFactory.getLogger(JwtDecodeService.class);


    private static final String SERVICE_NAME = "JWT Decode";


    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Value("${servicio.soter.keystore}")
    private Resource keyStore;

    @Value("${servicio.soter.keystorePassword}")
    private String keyStorePassword;

    @Value("${servicio.soter.uri.decode}")
    private String uri;

    public JwtDecodeResponse jwtDecode(JwtDecodeRequest request) {

        JwtDecodeResponse response = new JwtDecodeResponse();

        String requestBodyJson = gson.toJson(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestConsumerService restTemplate = new RestConsumerService(uri, requestBodyJson, headers);

        try {
            restTemplate.enableSSL(keyStore, keyStorePassword);
            ResponseEntity<String> responseEntity = restTemplate.consumeRestService(HttpMethod.POST);
          //  response = gson.fromJson(responseEntity.getBody(), JwtDecodeResponse.class);

            String body = (responseEntity != null) ? responseEntity.getBody() : null;
            if (body != null && !body.isEmpty()) {
                response = gson.fromJson(body, JwtDecodeResponse.class);
            } else {
                response.setResponseCode("");
            }
        } catch (Exception e) {
            log.error("Error consuming service " + SERVICE_NAME, e);
            response.setResponseCode("");
        }

        return response;
    }

    public JwtDecodeRequest buildRequest(String token) {

        JwtDecodeRequest request = new JwtDecodeRequest();
        request.setToken(token);

        return request;
    }

}
