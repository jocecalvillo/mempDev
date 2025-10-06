package com.mx.feenicia.memphis.commom.security;

import com.google.gson.Gson;
import com.mx.feenicia.memphis.commom.config.AesProperties;
import com.mx.feenicia.memphis.common.dictionary.ResponseCode;
import com.mx.feenicia.memphis.commom.exception.MemphisResponseException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class MerchantSecurity {

    private static final Logger log = LoggerFactory.getLogger(MerchantSecurity.class);


    private static final String X_REQUESTED_WITH_HEADER = "X-Requested-With";
    private static final String ZEUS_KEYMAKER_ENDPOINT = "/merchant/key/read";
    private static final String FNZA_ZEUS_SIGNATURE = "FNZA_ZEUS";
    private static final String SUCCESS_RESPONSE_CODE = "00";

    private final AesProperties aesProperties;
    private final RestTemplate restTemplateZeusKeyMaker;
    private final Gson gson = new Gson();

    public MerchantKeys getMerchantDecipherKeys(String merchantId) {
        MerchantKeysResponse response = getMerchantKeys(merchantId);

        if (!EncryptFeenicia.validateHexString(response.requestIv()) ||
                !EncryptFeenicia.validateHexString(response.requestKey())) {
            log.error("Invalid hexadecimal values received from Zeus KeyMaker");
            throw new MemphisResponseException(ResponseCode.INTERNAL_ERROR);
        }

        if (!EncryptFeenicia.validateHexString(aesProperties.getResponseIV()) ||
                !EncryptFeenicia.validateHexString(aesProperties.getResponseKEY())) {
            log.error("Invalid hexadecimal values in AES properties");
            throw new MemphisResponseException(ResponseCode.CONFIGURATION_ERROR);
        }

        // Convertir llaves del sistema de hex a bytes una sola vez
        byte[] responseIV = EncryptFeenicia.hexStringToByteArray(aesProperties.getResponseIV());
        byte[] responseKey = EncryptFeenicia.hexStringToByteArray(aesProperties.getResponseKEY());

        EncryptFeenicia encryptFeenicia = new EncryptFeenicia();

        // Descifra las llaves del merchant
        String merchantSignatureIv = encryptFeenicia.decrypt(
                response.requestIv(),
                responseIV,
                responseKey);

        String merchantSignatureKey = encryptFeenicia.decrypt(
                response.requestKey(),
                responseIV,
                responseKey);

        return new MerchantKeys(merchantSignatureIv, merchantSignatureKey);
    }

    /**
     * Obtiene las llaves cifradas del comercio.
     */
    private MerchantKeysResponse getMerchantKeys(String merchantId) {
        Request request = new Request(merchantId);
        String requestJson = gson.toJson(request);

        EncryptFeenicia encryptFeenicia = new EncryptFeenicia();

        byte[] signatureKey = EncryptFeenicia.hexStringToByteArray(aesProperties.getRequestSignatureKEY());
        byte[] signatureIv = EncryptFeenicia.hexStringToByteArray(aesProperties.getRequestSignatureIV());

        String signature = encryptFeenicia.generateAccessToken(
                requestJson, signatureKey, signatureIv, FNZA_ZEUS_SIGNATURE);

        HttpHeaders headers = new HttpHeaders();
        headers.add(X_REQUESTED_WITH_HEADER, signature);

        HttpEntity<Request> entity = new HttpEntity<>(request, headers);

        // Consume del servicio de Zeus KeyMaker
        MerchantKeysResponse response = restTemplateZeusKeyMaker.postForObject(
                ZEUS_KEYMAKER_ENDPOINT, entity, MerchantKeysResponse.class);

        validateResponse(response);
        return response;
    }

    /**
     * Valida la respuesta del servicio Zeus KeyMaker
     */
    private void validateResponse(MerchantKeysResponse response) {
        if (response == null) {
            log.error("Zeus KeyMaker returned null response");
            throw new MemphisResponseException(ResponseCode.INTERNAL_ERROR);
        }

        if (log.isDebugEnabled()) {
            log.debug("Zeus KeyMaker Response Code: {}", response.responseCode());
        }

        if (!SUCCESS_RESPONSE_CODE.equals(response.responseCode())) {
            log.warn("Zeus KeyMaker returned error code: {}", response.responseCode());
            throw new MemphisResponseException(ResponseCode.INTERNAL_ERROR);
        }
    }

    /**
     * Peticion del servicio Zeus Keymaker
     */
    private record Request(String merchant) {
        // Usamos record para inmutabilidad y menos boilerplate
    }

    /**
     * Respuesta del servicio Zeus Keymaker
     */
    private record MerchantKeysResponse(String responseCode, String requestIv, String requestKey) {
        // Encapsula la respuesta de Zeus Keymaker
    }

    public record MerchantKeys(String requestIv, String requestKey) {
        // Empty
    }




}
