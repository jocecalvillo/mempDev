package com.mx.feenicia.memphis.commom.soter.ws;

import lombok.extern.slf4j.Slf4j;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RestConsumerService {

    private HttpHeaders httpHeaders;
    private String requestBodyJson;
    private String url;
    private RestTemplate restTemplate;

    public RestConsumerService(String url, String requestBodyJson, HttpHeaders httpHeaders) {
        this.url = url;
        this.requestBodyJson = requestBodyJson;

        if (httpHeaders == null) {
            this.httpHeaders = new HttpHeaders();
        } else {
            this.httpHeaders = httpHeaders;
        }

        List<MediaType> acceptList = new ArrayList<>();
        acceptList.add(MediaType.APPLICATION_JSON);
        this.httpHeaders.setAccept(acceptList);


        this.restTemplate = new RestTemplate();
    }

    public void enableSSL(Resource resourceKeyStore, String keyStorePassword)
            throws KeyStoreException, IOException, NoSuchAlgorithmException,
            CertificateException, UnrecoverableKeyException, KeyManagementException {

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // try-with-resources
        try (FileInputStream fis = new FileInputStream(resourceKeyStore.getFile())) {
            keyStore.load(fis, keyStorePassword.toCharArray());
        }

        // SSLContext con confianza en self-signed y key material del keystore
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                SSLContextBuilder.create()
                        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                        .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
                        .build(),
                NoopHostnameVerifier.INSTANCE
        );

        //  HttpClient 5: configurar el SSLSocketFactory a través del Connection Manager
        var connManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(socketFactory)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .build();

        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        //  reemplaza el RestTemplate con el que usa SSL
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public ResponseEntity<String> consumeRestService(HttpMethod httpMethod) {
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, httpHeaders);
        return restTemplate.exchange(url, httpMethod, requestEntity, String.class);
    }
}
