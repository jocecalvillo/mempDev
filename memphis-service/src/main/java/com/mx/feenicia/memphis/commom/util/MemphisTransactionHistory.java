package com.mx.feenicia.memphis.commom.util;

import com.google.gson.Gson;

import com.mx.feenicia.memphis.common.api.common.MemphisApiTxResponse;
import com.mx.feenicia.memphis.commom.entity.TblHistorical;
import com.mx.feenicia.memphis.commom.exception.MemphisResponseException;
import com.mx.feenicia.memphis.common.model.AtenaTxByRequest;
import com.mx.feenicia.memphis.commom.repository.TblHistoricalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.mx.feenicia.memphis.common.dictionary.ResponseCode.SAVE_TRANSACTION_ERROR;
import static com.mx.feenicia.memphis.common.dictionary.ResponseCode.UPDATE_TRANSACTION_ERROR;

@Component
public class MemphisTransactionHistory {

    private static final Logger log = LoggerFactory.getLogger(MemphisTransactionHistory.class);

    private final TblHistoricalRepository historicalRepository;
    private static final Gson GSON = new Gson();

    public MemphisTransactionHistory(TblHistoricalRepository historicalRepository) {
        this.historicalRepository = historicalRepository;
    }

    public void saveInitializedFromAtenaRequest(TblHistorical tx,TblHistorical originalTransaction,
                                                AtenaTxByRequest atenaTxByRequest){
        try {
            tx.setCreatedDate(new Date());
            tx.setTransactionId(atenaTxByRequest.getTransactionId());
            tx.setRelatedTicketNumber(originalTransaction.getTicketNumber());
            tx.setTblMerchant(originalTransaction.getTblMerchant());
            tx.setAmount(originalTransaction.getAmount());
            tx.setCurrency(originalTransaction.getCurrency());
            tx.setJsonFeeniciaRequest(GSON.toJson(atenaTxByRequest));

             historicalRepository.saveAndFlush(tx);

        } catch (Exception e) {
            log.error("Error al guardar transacción inicializada desde Atena: {}", e.getMessage(), e);
            throw new MemphisResponseException(SAVE_TRANSACTION_ERROR);
        }
    }

    public boolean updateFromMemphisResponse(TblHistorical tx,String memphisTxResponse,String transactionType){

        try {

            MemphisApiTxResponse memphisApiTxResponse = new Gson().fromJson(memphisTxResponse, MemphisApiTxResponse.class);

            tx.setUpdatedDate(new Date());
            tx.setTransactionType( transactionType); //REFOUND CANCEL REVERSAL
            tx.setTicketNumber(memphisApiTxResponse.getId());
            tx.setTransactionReference(memphisApiTxResponse.getRequest_id());
            tx.setErrorcode(memphisApiTxResponse.getResp_code());
            tx.setErrormessage(memphisApiTxResponse.getDescription());
            tx.setStatus(memphisApiTxResponse.getResp_code());
            tx.setJsonMemphisResponse(memphisTxResponse);

            historicalRepository.saveAndFlush(tx);
            return true;

        }catch (Exception e) {
            log.error("Error al actualizar transacción con respuesta de Memphis: {}", e.getMessage(), e);
            throw new MemphisResponseException(UPDATE_TRANSACTION_ERROR);
        }
    }
}
