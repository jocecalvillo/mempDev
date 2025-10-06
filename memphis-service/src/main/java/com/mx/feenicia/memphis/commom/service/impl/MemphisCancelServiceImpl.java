package com.mx.feenicia.memphis.commom.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mx.feenicia.memphis.common.api.common.MemphisApiTxRequest;
import com.mx.feenicia.memphis.common.api.common.MemphisApiTxResponse;
import com.mx.feenicia.memphis.common.dictionary.Memphis;
import com.mx.feenicia.memphis.common.dictionary.MemphisConstants;
import com.mx.feenicia.memphis.common.dictionary.ResponseCode;
import com.mx.feenicia.memphis.commom.entity.TblConfigurationMemphis;
import com.mx.feenicia.memphis.commom.entity.TblHistorical;
import com.mx.feenicia.memphis.commom.exception.MemphisResponseException;
import com.mx.feenicia.memphis.common.feenicia.AtenaResponse;
import com.mx.feenicia.memphis.common.model.AtenaTxByRequest;
import com.mx.feenicia.memphis.common.model.Data;
import com.mx.feenicia.memphis.commom.repository.TblConfigurationMemphisRepository;
import com.mx.feenicia.memphis.commom.repository.TblHistoricalRepository;
import com.mx.feenicia.memphis.commom.service.MemphisCancelService;
import com.mx.feenicia.memphis.commom.util.MemphisHttpClient;
import com.mx.feenicia.memphis.commom.util.MemphisTransactionHistory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.mx.feenicia.memphis.common.dictionary.ResponseCode.CONFIGURATION_NOT_FOUND;
import static com.mx.feenicia.memphis.common.dictionary.ResponseCode.TRANSACTION_NOT_FOUND;
import static com.mx.feenicia.memphis.commom.util.TokenGenerator.generateTKA;

@Service

public class MemphisCancelServiceImpl implements MemphisCancelService {


    private static final Logger log = LoggerFactory.getLogger(MemphisCancelServiceImpl.class);

    @Value("${memphis.properties.refund.timeframe}")
    private long timeframe;

    @Value("${memphis.properties.refund.request-limit}")
    private int cancelRequestLimit;
    private final TblHistoricalRepository historicalRepository;

    private final MemphisHttpClient memphisHttpClient;

    private final TblConfigurationMemphisRepository tblConfigurationMemphisRepository;

    private final MemphisTransactionHistory txHistory;

    public MemphisCancelServiceImpl(TblHistoricalRepository historicalRepository, MemphisHttpClient memphisHttpClient, TblConfigurationMemphisRepository tblConfigurationMemphisRepository, MemphisTransactionHistory txHistory) {
        this.historicalRepository = historicalRepository;
        this.memphisHttpClient = memphisHttpClient;
        this.tblConfigurationMemphisRepository = tblConfigurationMemphisRepository;
        this.txHistory = txHistory;
    }

    @Override
    public AtenaResponse cancelById(AtenaTxByRequest atenaTxByRequest)   {

        TblHistorical originalTransaction = validation(atenaTxByRequest);
        String transactionIdStr = String.valueOf(atenaTxByRequest.getTransactionId());

        BigDecimal totalAmount = originalTransaction.getAmount();

        // Save Atena request
        TblHistorical cancelTransaction = new TblHistorical();

        txHistory.saveInitializedFromAtenaRequest(cancelTransaction,originalTransaction, atenaTxByRequest);


        MemphisApiTxRequest request=map2memphisCancelRequest(totalAmount,transactionIdStr);

        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String tokenResponse;

        Optional<TblConfigurationMemphis> tblConfigurationMemphis = tblConfigurationMemphisRepository.findByProcesador("memphis");

        if (tblConfigurationMemphis.isEmpty()) {
            throw new MemphisResponseException(CONFIGURATION_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        String tkr = tblConfigurationMemphis.get().getTkr();
        String commerceId = tblConfigurationMemphis.get().getAfiliacion();
        String uriMemphis = tblConfigurationMemphis.get().getUriMemphis() + tblConfigurationMemphis.get().getUriRelativeMemphis();
        String commerceName = tblConfigurationMemphis.get().getNombreComercio();


        try {
            tokenResponse = generateTKA(tkr, timestamp, commerceId);
        } catch (Exception e) {
            log.error("Error generating TKA token", e);
            throw new MemphisResponseException(ResponseCode.TOKEN_GENERATION_ERROR);
        }

        String memphisCancelResponseAsString=getMemphisTx(request,tokenResponse,timestamp,uriMemphis,commerceId,originalTransaction.getTicketNumber(),commerceName);

        //update(refundTransaction, memphisRefoundResponseAsString);
        txHistory.updateFromMemphisResponse(cancelTransaction, memphisCancelResponseAsString, "CANCEL");

        JsonObject jsonObjectCharge = JsonParser.parseString(memphisCancelResponseAsString).getAsJsonObject();

        if (!jsonObjectCharge.has("trace_id")) {
            AtenaResponse memphisCancelResponse = new AtenaResponse();
            memphisCancelResponse.setSuccess(false);
            Data data = new Data();
            data.setResultCode(jsonObjectCharge.get("resp_code").getAsString());

            return memphisCancelResponse;
        }

        MemphisApiTxResponse memphisApiTxResponse = new Gson().fromJson(memphisCancelResponseAsString, MemphisApiTxResponse.class);


        String authnum = "";

        Data data = new Data();
        AtenaResponse memphisCancelResponse = new AtenaResponse();

        if(memphisApiTxResponse.getResp_code().equals("00")){
            memphisCancelResponse.setSuccess(true);
            int length= memphisApiTxResponse.getRequest_id().length();
            authnum= memphisApiTxResponse.getRequest_id().substring(length-6,length).toUpperCase();
            data.setResultCode(memphisApiTxResponse.getResp_code());
        }else{
            memphisCancelResponse.setSuccess(false);
            data.setResultCode(memphisApiTxResponse.getResp_code());
        }

        data.setApprovedCode(authnum);
        data.setDescription(memphisApiTxResponse.getDescription());

        memphisCancelResponse.setClaveOperacion(Long.valueOf(memphisApiTxResponse.getRequest_id()));
        memphisCancelResponse.setCode(memphisApiTxResponse.getResp_code());
        memphisCancelResponse.setDescription(memphisApiTxResponse.getDescription());
        memphisCancelResponse.setData(data);


        return memphisCancelResponse;
    }

    private TblHistorical validation(AtenaTxByRequest atenaTxByRequest) {
        long refundTransactionId = atenaTxByRequest.getRefundTransactionId();

        TblHistorical transactionOriginal =historicalRepository.findByTransactionIdAndTransactionTypeAndStatus(refundTransactionId, "SALE", "APROBADA")
                .orElseThrow(() ->  new MemphisResponseException(TRANSACTION_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Timeframe
        long timeElapsed = ((new Date()).getTime() - transactionOriginal.getCreatedDate().getTime());

        if (TimeUnit.MILLISECONDS.toDays(timeElapsed) >= timeframe) {
            throw new MemphisResponseException(ResponseCode.TRANSACTION_CANNOT_BE_REFUNDED_TIMEFRAME, HttpStatus.BAD_REQUEST);
        }

        String approvalTicketNumber = transactionOriginal.getTicketNumber();

        if (historicalRepository.existsByRelatedTicketNumberAndStatusAndTransactionTypeIn(approvalTicketNumber, Memphis.STATUS_APPROVAL, List.of(Memphis.VOID, Memphis.REFUND))) {
            throw new MemphisResponseException(ResponseCode.TRANSACTION_ALREADY_REFUNDED, HttpStatus.BAD_REQUEST);
        }

        if (historicalRepository.existsByRelatedTicketNumberAndStatusAndTransactionTypeIn(approvalTicketNumber, Memphis.STATUS_INITIALIZED, List.of(Memphis.VOID, Memphis.REFUND))) {
            throw new MemphisResponseException(ResponseCode.TRANSACTION_REFUND_REQUESTED, HttpStatus.BAD_REQUEST);
        }

        long numberAttempts = historicalRepository.countByRelatedTicketNumberAndStatusAndTransactionTypeIn(approvalTicketNumber, Memphis.STATUS_DECLINED, List.of(Memphis.VOID, Memphis.REFUND));

        log.info("Request to refund the transaction [{}]", refundTransactionId);

        if (numberAttempts >= cancelRequestLimit) {
            throw new MemphisResponseException(ResponseCode.TRANSACTION_CANNOT_BE_REFUNDED, HttpStatus.BAD_REQUEST);
        }

        return transactionOriginal;

    }

    private MemphisApiTxRequest map2memphisCancelRequest(BigDecimal amount, String transactionId) {
        MemphisApiTxRequest memphisApiTxRequest = new MemphisApiTxRequest();
        memphisApiTxRequest.setIsoType(MemphisConstants.ISO_TYPE);
        memphisApiTxRequest.setAmount(String.valueOf(amount));
        memphisApiTxRequest.setReference(transactionId);
        memphisApiTxRequest.setEntry_mode(MemphisConstants.ENTRY_MODE);
        memphisApiTxRequest.setAuthentication(MemphisConstants.AUTENTICATION);
        memphisApiTxRequest.setDraftCapture(MemphisConstants.DRAFT_CAPTURE);
        memphisApiTxRequest.setTpv(false);
        memphisApiTxRequest.setPeriodic(false);
        return memphisApiTxRequest;
    }

    private String getMemphisTx(MemphisApiTxRequest refundRequest, String tokenResponse, String timestamp, String uriMemphis, String commerceId, String ticketNumber, String comerceName) {
        String uriComplete = uriMemphis;
        if (!uriComplete.endsWith("/")) uriComplete += "/";
        uriComplete += "cancel/" + ticketNumber;

        return memphisHttpClient.postJson(
                refundRequest,
                tokenResponse,
                timestamp,
                uriComplete,
                commerceId,
                comerceName
        );
    }
}
