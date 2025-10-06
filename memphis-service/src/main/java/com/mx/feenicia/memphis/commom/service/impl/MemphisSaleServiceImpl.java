package com.mx.feenicia.memphis.commom.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mx.feenicia.memphis.common.model.Card;
import com.mx.feenicia.memphis.common.api.sale.BillingAddress;
import com.mx.feenicia.memphis.common.api.sale.CardInformation;
import com.mx.feenicia.memphis.common.api.sale.MemphisApiSaleRequest;
import com.mx.feenicia.memphis.common.dictionary.MemphisConstants;
import com.mx.feenicia.memphis.common.dictionary.ResponseCode;
import com.mx.feenicia.memphis.commom.entity.TblConfigurationMemphis;
import com.mx.feenicia.memphis.commom.entity.TblHistorical;
import com.mx.feenicia.memphis.commom.entity.TblMerchant;
import com.mx.feenicia.memphis.commom.exception.MemphisResponseException;
import com.mx.feenicia.memphis.common.feenicia.AtenaResponse;
import com.mx.feenicia.memphis.common.model.Data;
import com.mx.feenicia.memphis.common.model.MemphisSaleRequest;
import com.mx.feenicia.memphis.commom.repository.TblConfigurationMemphisRepository;
import com.mx.feenicia.memphis.commom.repository.TblHistoricalRepository;
import com.mx.feenicia.memphis.commom.repository.TblMerchantRepository;
import com.mx.feenicia.memphis.commom.repository.TblResponseCodesRepository;
import com.mx.feenicia.memphis.commom.security.EncryptFeenicia;
import com.mx.feenicia.memphis.commom.security.MerchantSecurity;
import com.mx.feenicia.memphis.commom.service.MemphisSaleService;
import com.mx.feenicia.memphis.commom.util.MemphisHttpClient;
import com.mx.feenicia.memphis.commom.util.MerchantUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.Date;

import java.util.Optional;

import static com.mx.feenicia.memphis.commom.util.TokenGenerator.generateTKA;
import static com.mx.feenicia.memphis.commom.util.MemphisLogUtils.logMemphisResponseDetails;
import static com.mx.feenicia.memphis.common.dictionary.ResponseCode.*;


@Service

public class MemphisSaleServiceImpl implements MemphisSaleService {

    private static final Logger log = LoggerFactory.getLogger(MemphisSaleServiceImpl.class);

    private final TblHistoricalRepository historicalRepository;

    private final TblMerchantRepository merchantRepository;

    private final MerchantSecurity merchantSecurity;

    private final TblResponseCodesRepository responseCodesRepository;

    private final TblConfigurationMemphisRepository tblConfigurationMemphisRepository;

    private final MemphisHttpClient memphisHttpClient;


    @Value("${atena.cardholdername.empty}")
    private String atenaCardholdernameEmpty;


    private static final Gson GSON = new Gson();
    private static final String APPROVED_RESPONSE_CODE = "00";

    public MemphisSaleServiceImpl(TblHistoricalRepository historicalRepository,
                                  TblMerchantRepository merchantRepository,
                                  MerchantSecurity merchantSecurity,
                                  TblConfigurationMemphisRepository tblConfigurationMemphisRepository,
                                  TblResponseCodesRepository responseCodesRepository, MemphisHttpClient memphisHttpClient) {
        this.historicalRepository = historicalRepository;
        this.merchantRepository = merchantRepository;
        this.merchantSecurity = merchantSecurity;
        this.responseCodesRepository = responseCodesRepository;
        this.tblConfigurationMemphisRepository = tblConfigurationMemphisRepository;
        this.memphisHttpClient = memphisHttpClient;
    }

    @Override
    public AtenaResponse payment(MemphisSaleRequest memphisSaleRequest, Authentication authentication) {
        String transactionIdStr = String.valueOf(memphisSaleRequest.getExtendedData().getTransactionId());
        Long transactionId = Long.valueOf(transactionIdStr);

        if (historicalRepository.existsByTransactionId(transactionId)) {
            log.info("Transaction already exists: {}", transactionId);
            throw new MemphisResponseException(TRANSACTION_ALREADY_EXISTS);
        }

        // Merchant Identification
        String merchantId = MerchantUtils.getMerchantFromAuthentication(authentication);


        // Getting Merchant Info
        Optional<TblMerchant> tblMerchant = merchantRepository.findByMerchantAndAffiliation(merchantId, memphisSaleRequest.getAffiliation().toString());
        if (tblMerchant.isEmpty()) {
            throw new MemphisResponseException(MERCHANT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        Optional<TblConfigurationMemphis> tblConfigurationMemphis = tblConfigurationMemphisRepository.findByProcesador("memphis");

        if (tblConfigurationMemphis.isEmpty()) {
            throw new MemphisResponseException(CONFIGURATION_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        String tkr = tblConfigurationMemphis.get().getTkr();
       String commerceId = tblConfigurationMemphis.get().getAfiliacion();
       String uriMemphis = tblConfigurationMemphis.get().getUriMemphis() + tblConfigurationMemphis.get().getUriRelativeMemphis();
       String commerceName = tblConfigurationMemphis.get().getNombreComercio();

        // Getting Card Info
        Card card = getMerchKeyNCrackEm(memphisSaleRequest, merchantId);

        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String tokenResponse = null;

        // Getting Token TKA con manejo adecuado de errores
        try {
            tokenResponse = generateTKA(tkr, timestamp, commerceId);
        } catch (Exception e) {
            log.error("Error generating TKA token", e);
             //throw new MemphisResponseException(ResponseCode.TOKEN_GENERATION_ERROR);
            AtenaResponse response = new AtenaResponse();
            response.setSuccess(false);
            response.setCode("05");
            response.setDescription("Error generating TKA token");
            Data data = new Data();
            data.setTotalamount(memphisSaleRequest.getAmount().doubleValue());
            data.setResultCode("05");
            data.setApprovedCode("");
            data.setDescription("");
            data.setTransactiondate(new Date().toString());
            response.setData(data);
            response.setClaveOperacion(0L);
            return response;
        }

        saveTokenRequest(memphisSaleRequest, tokenResponse, tblMerchant.get());

        // Request
        MemphisApiSaleRequest saleRequest = map2memphisSaleRequest(memphisSaleRequest, card);
        String saleResponse = getMemphisTx(saleRequest, tokenResponse, timestamp,uriMemphis, commerceId,commerceName);

       if (saleResponse == null) {
            throw new MemphisResponseException(ERROR_MANUAL_SALE_ENDPOINT);
        }

        saveSaleRequest(memphisSaleRequest, saleResponse, tblMerchant.get());

        // Procesar la respuesta de la venta
        return processSaleResponse(memphisSaleRequest, saleResponse, tblMerchant.get());
    }

    private Card getMerchKeyNCrackEm(MemphisSaleRequest memphisSaleRequest, String merchantId) {
        Card card = new Card();
        EncryptFeenicia encryptFeenicia = new EncryptFeenicia();

        // Merch Keys
        MerchantSecurity.MerchantKeys merchantKeys = merchantSecurity.getMerchantDecipherKeys(merchantId);
        String iv = merchantKeys.requestIv();
        String key = merchantKeys.requestKey();

        // Validar y desencriptar fecha de expiración
        String expDate = encryptFeenicia.decrypt(
                memphisSaleRequest.getExpDate(),
                EncryptFeenicia.hexStringToByteArray(iv),
                EncryptFeenicia.hexStringToByteArray(key)
        );

        if (expDate == null || expDate.length() < 4) {
            log.error("Invalid expiration date: {}", expDate);
            //   throw new MemphisResponseException(INVALID_CARD_DATA);
        }

        String year = expDate.substring(0, 2);
        String month = expDate.substring(2, 4);

        // Desencriptar nombre del tarjetahabiente
        String cardHolderName = encryptFeenicia.decrypt(
                memphisSaleRequest.getCardholderName(),
                EncryptFeenicia.hexStringToByteArray(iv),
                EncryptFeenicia.hexStringToByteArray(key)
        );

        if (atenaCardholdernameEmpty.equalsIgnoreCase(cardHolderName)) {
            log.info("Cardholder name is empty");
            throw new MemphisResponseException(ATENA_CARDHOLDERNAME_EMPTY);
        }

        // Desencriptar número de tarjeta
        String cardNumber = encryptFeenicia.decrypt(
                memphisSaleRequest.getPan(),
                EncryptFeenicia.hexStringToByteArray(iv),
                EncryptFeenicia.hexStringToByteArray(key)
        );

        // Validar número de tarjeta
        if (cardNumber == null || cardNumber.length() < 13) {
            log.error("Invalid card number: {}");
            throw new MemphisResponseException(INVALID_CARD_NUMBER);
        }

        // Desencriptar CVV
        String cvv = encryptFeenicia.decrypt(
                memphisSaleRequest.getCvv2(),
                EncryptFeenicia.hexStringToByteArray(iv),
                EncryptFeenicia.hexStringToByteArray(key)
        );

        card.setName(cardHolderName);
        card.setNumber(cardNumber);
        card.setExpiryMonth(month);
        card.setExpiryYear(year);
        card.setCvv(cvv);
        return card;
    }

    private void saveTokenRequest(MemphisSaleRequest memphisSaleRequest, String tokenResponse, TblMerchant merchant) {
        try {
            TblHistorical entity = new TblHistorical();
            entity.setTblMerchant(merchant);
            entity.setTransactionId(Long.valueOf(
                    memphisSaleRequest.getExtendedData().getTransactionId()));
            entity.setCreatedDate(new Date());
            entity.setUpdatedDate(new Date());
            entity.setAmount(memphisSaleRequest.getAmount());
            entity.setCurrency(memphisSaleRequest.getExtendedData().getCurrency());
            entity.setTransactionType("TOKEN");

            String jsonFeeRequest = GSON.toJson(memphisSaleRequest);
            JsonObject jsonFeeObject = GSON.fromJson(jsonFeeRequest, JsonObject.class);
            JsonObject jsonFeeClean = cleanJson(jsonFeeObject);

            entity.setJsonFeeniciaRequest(jsonFeeClean.toString());
            entity.setJsonMemphisResponse(tokenResponse);

            historicalRepository.save(entity);
        } catch (Exception e) {
            log.error("Error saving token request", e);
        }
    }

    private JsonObject cleanJson(JsonObject jsonFeeObject) {
        // Lista de campos sensibles a remover
        String[] sensitiveFields = {"cardholderName", "pan", "cvv2", "expDate"};

        for (String field : sensitiveFields) {
            if (jsonFeeObject.has(field)) {
                jsonFeeObject.remove(field);
            }
        }

        if (jsonFeeObject.has("extendedData")) {
            JsonObject extendedData = jsonFeeObject.getAsJsonObject("extendedData");
            String[] extendedSensitiveFields = {
                    "billingData", "shippingData", "siteDomain", "customerData", "productData"
            };

            for (String field : extendedSensitiveFields) {
                if (extendedData.has(field)) {
                    extendedData.remove(field);
                }
            }
        }

        return jsonFeeObject;
    }

    private MemphisApiSaleRequest map2memphisSaleRequest(MemphisSaleRequest request, Card card) {
        MemphisApiSaleRequest memphisApiSaleRequest = new MemphisApiSaleRequest();

        // Card Information
        CardInformation cardInformation = new CardInformation();
        cardInformation.setHolder_name(card.getName());
        cardInformation.setCard_number(card.getNumber());

        // Last 4 digits y BIN
        String cardNumber = card.getNumber();
        if (cardNumber != null && cardNumber.length() >= 4) {
            cardInformation.setLast4(cardNumber.substring(cardNumber.length() - 4));
            cardInformation.setBin(cardNumber.substring(0, 6)); // BIN típicamente 6 dígitos
        } else {
            throw new MemphisResponseException(INVALID_CARD_NUMBER);
        }

        cardInformation.setExpiration_year(card.getExpiryYear());
        cardInformation.setExpiration_month(card.getExpiryMonth());
        cardInformation.setCvv(card.getCvv());
        memphisApiSaleRequest.setCardInformation(cardInformation);

        // Billing Address
        BillingAddress billingAddress = createBillingAddress(request);
        memphisApiSaleRequest.setBillingAddress(billingAddress);

        // Other fields
        memphisApiSaleRequest.setIsoType(MemphisConstants.ISO_TYPE);
        //big decimal
        memphisApiSaleRequest.setAmount(String.valueOf(request.getAmount()));

        // Formatear transactionId
        String transactionId = formatTransactionId(String.valueOf(request.getExtendedData().getTransactionId()));
        memphisApiSaleRequest.setReference(transactionId);

        memphisApiSaleRequest.setEntry_mode(MemphisConstants.ENTRY_MODE);
        memphisApiSaleRequest.setAuthentication(MemphisConstants.AUTENTICATION);
        memphisApiSaleRequest.setDraftCapture(MemphisConstants.DRAFT_CAPTURE);
        memphisApiSaleRequest.setTpv(false);
        memphisApiSaleRequest.setPeriodic(false);
        memphisApiSaleRequest.setEmail(request.getExtendedData().getCustomerData().getEmail());
        memphisApiSaleRequest.setClientip(MemphisConstants.CLIENT_IP);

        return memphisApiSaleRequest;
    }

    private String formatTransactionId(String originalId) {
        try {
            // Intentar formatear como número
            Long id = Long.parseLong(originalId);
            return String.format("%012d", id);
        } catch (NumberFormatException e) {
            // Si no es numérico, usar padding con ceros
            return String.format("%012s", originalId).replace(' ', '0');
        }
    }

    private BillingAddress createBillingAddress(MemphisSaleRequest request) {
        BillingAddress billingAddress = new BillingAddress();

        if (request.getExtendedData() != null && request.getExtendedData().getBillingData() != null) {
            billingAddress.setCountry(
                    Optional.ofNullable(request.getExtendedData().getBillingData().getCountry()).orElse("")
            );
            billingAddress.setZipCode(
                    Optional.ofNullable(request.getExtendedData().getBillingData().getPostCode()).orElse("")
            );
            billingAddress.setState(
                    Optional.ofNullable(request.getExtendedData().getBillingData().getState()).orElse("")
            );
            billingAddress.setStreet(
                    Optional.ofNullable(request.getExtendedData().getBillingData().getStreet()).orElse("")
            );
            billingAddress.setStreet2(
                    Optional.ofNullable(request.getExtendedData().getBillingData().getStreet()).orElse("")
            );
        }

        return billingAddress;
    }


    private String getMemphisTx(MemphisApiSaleRequest saleReq, String tokenResponse, String timestamp,String uriMemphis,String commerceId,String commerceName) {
        //String uriComplete= uriMemphis + "charge";

        String uriComplete = uriMemphis;
        if (!uriComplete.endsWith("/")) uriComplete += "/";
        uriComplete += "charge";


        return memphisHttpClient.postJson(
                saleReq,
                tokenResponse,
                timestamp,
                uriComplete,
                commerceId,
                commerceName
        );
    }

    private AtenaResponse processSaleResponse(MemphisSaleRequest memphisSaleRequest, String memphisSaleResponse, TblMerchant merchant) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(memphisSaleResponse).getAsJsonObject();

            if (!jsonResponse.has("resp_code")) {
                throw new MemphisResponseException(INVALID_RESPONSE_FORMAT);
            }

            String respCode = jsonResponse.get("resp_code").getAsString();

            if (APPROVED_RESPONSE_CODE.equals(respCode)) {
                return createSuccessResponse(memphisSaleRequest, jsonResponse);
            } else {
                return handleDeclinedTransaction(memphisSaleRequest, memphisSaleResponse, merchant, jsonResponse);
            }

        } catch (Exception e) {
            log.error("Error processing sale response", e);
            throw new MemphisResponseException(RESPONSE_PROCESSING_ERROR);
        }
    }


    private AtenaResponse createSuccessResponse(MemphisSaleRequest memphisSaleRequest, JsonObject jsonResponse) {
        AtenaResponse response = new AtenaResponse();
        response.setSuccess(true);

        String authnum = "";
        if (jsonResponse.has("request_id") && !jsonResponse.get("request_id").isJsonNull()) {
            String requestId = jsonResponse.get("request_id").getAsString();
            int length = requestId.length();
            if (length >= 6) {
                authnum = requestId.substring(length - 6).toUpperCase();
            } else {
                authnum = requestId.toUpperCase(); // Si tiene menos de 6 caracteres,
            }
        }

        String description = getFieldValue(jsonResponse, "description");// reutilizar  y sacar con un util


        Data data = new Data();
        data.setTotalamount(memphisSaleRequest.getAmount().doubleValue());
        data.setResultCode(ResponseCode.SUCCESS.getCode());
        data.setApprovedCode(authnum);
        data.setDescription(description);
        data.setTransactiondate(new Date().toString());

        response.setData(data);
        response.setClaveOperacion(memphisSaleRequest.getExtendedData().getTransactionId());

        return response;
    }

    private AtenaResponse handleDeclinedTransaction(MemphisSaleRequest memphisSaleRequest, String memphisSaleResponse,
                                                    TblMerchant merchant, JsonObject jsonResponse) {
        String description = getFieldValue(jsonResponse, "description");
        String respCode = getFieldValue(jsonResponse, "resp_code");

        log.warn("Transaction declined - Code: {}, Description: {}", respCode, description);

        AtenaResponse response = new AtenaResponse();
        response.setSuccess(false);
        response.setCode(respCode);
        response.setDescription(description);

        Data data = new Data();
        data.setResultCode(respCode);
        data.setDescription(description);
        response.setData(data);

        return response;
    }


    private String getFieldValue(JsonObject jsonObject, String fieldName) {
        return jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull()
                ? jsonObject.get(fieldName).getAsString()
                : null;
    }


    private void saveSaleRequest(MemphisSaleRequest memphisSaleRequest,String memphisSaleResponse, TblMerchant merchant) {
        TblHistorical entity= new TblHistorical();
        Gson gson = new Gson();
        JsonObject jsonObjectCharge = JsonParser.parseString(memphisSaleResponse).getAsJsonObject();

        if(jsonObjectCharge.has("trace_id")){//mal la validacion
            entity.setTicketNumber(jsonObjectCharge.get("id").getAsString());
        }

        entity.setTblMerchant(merchant);
        entity.setTransactionId(memphisSaleRequest.getExtendedData().getTransactionId());
        entity.setTransactionReference(jsonObjectCharge.get("request_id").getAsString());
        entity.setCreatedDate(new Date());
        entity.setUpdatedDate(new Date());
        entity.setAmount(memphisSaleRequest.getAmount());
        entity.setCurrency(memphisSaleRequest.getExtendedData().getCurrency());

        String jsonFeeRequest = gson.toJson(memphisSaleRequest);
        JsonObject jsonFeeObject = gson.fromJson(jsonFeeRequest, JsonObject.class);
        JsonObject jsonFeeClean= cleanJson(jsonFeeObject);

        entity.setJsonFeeniciaRequest(String.valueOf(jsonFeeClean));

        entity.setJsonMemphisResponse(memphisSaleResponse);

        entity.setTransactionType("SALE");
        entity.setStatus(jsonObjectCharge.get("description").getAsString());
        entity.setErrorcode(jsonObjectCharge.get("resp_code").getAsString());
        entity.setErrormessage(jsonObjectCharge.get("description").getAsString());

        logMemphisResponseDetails(jsonObjectCharge); // imprimir  los detalles



        historicalRepository.save(entity);


    }
}




