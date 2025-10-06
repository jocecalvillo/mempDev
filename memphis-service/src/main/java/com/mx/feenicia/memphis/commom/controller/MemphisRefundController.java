package com.mx.feenicia.memphis.commom.controller;


import com.mx.feenicia.memphis.commom.service.MemphisRefundService;
import com.mx.feenicia.memphis.common.feenicia.AtenaResponse;
import com.mx.feenicia.memphis.common.model.AtenaTxByRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refund")
@Validated
public class MemphisRefundController {

    private final MemphisRefundService memphisRefundService;

    public MemphisRefundController(MemphisRefundService memphisRefundService) {
        this.memphisRefundService = memphisRefundService;
    }

    @PostMapping
    public ResponseEntity<AtenaResponse> refundRequest(@RequestBody @Valid AtenaTxByRequest atenaTxByRequest) {
        return ResponseEntity.ok(memphisRefundService.refundById(atenaTxByRequest));
    }

}
