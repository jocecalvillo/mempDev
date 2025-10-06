package com.mx.feenicia.memphis.commom.soter.ws;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mx.feenicia.memphis.commom.soter.dto.request.JwtVerifyRequest;
import com.mx.feenicia.memphis.commom.soter.dto.response.JwtVerifyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtVerifyService {
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private static final String SERVICE_NAME = "JWT Verify";

    @Value("${servicio.soter.keystore}")
    private Resource keyStore;

    @Value("${servicio.soter.keystorePassword}")
    private String keyStorePassword;

    @Value("${servicio.soter.uri.verify}")
    String uri;

    public JwtVerifyResponse jwtVerify(JwtVerifyRequest request) {

        JwtVerifyResponse response = new JwtVerifyResponse();
        String requestBodyJson = gson.toJson(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestConsumerService restTemplate = new RestConsumerService(uri, requestBodyJson, headers);

        try {
            restTemplate.enableSSL(keyStore,keyStorePassword);
            ResponseEntity<String> responseEntity = restTemplate.consumeRestService(HttpMethod.POST);
            response = gson.fromJson(responseEntity.getBody(), JwtVerifyResponse.class);
        } catch (Exception e) {
            log.error("Error consuming service " + SERVICE_NAME, e);
            response.setResponseCode("");
        }

        return response;
    }

    public JwtVerifyRequest builRequest(String token, String secret) {

        JwtVerifyRequest request = new JwtVerifyRequest();
        request.setToken(token);
        request.setSecret(secret);

        return request;
    }

}
