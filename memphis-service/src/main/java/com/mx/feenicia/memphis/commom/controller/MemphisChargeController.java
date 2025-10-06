package com.mx.feenicia.memphis.commom.controller;

import com.mx.feenicia.memphis.commom.service.MemphisSaleService;
import com.mx.feenicia.memphis.common.feenicia.AtenaResponse;
import com.mx.feenicia.memphis.common.model.MemphisSaleRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@Validated
public class MemphisChargeController {


    private final  MemphisSaleService memphisSaleService;


    public MemphisChargeController(MemphisSaleService memphisSaleService) {
        this.memphisSaleService = memphisSaleService;
    }


    @PostMapping
    public ResponseEntity<AtenaResponse> paymentRequest(@RequestBody @Valid MemphisSaleRequest muninSaleRequest, Authentication authentication) {
        return ResponseEntity.ok(memphisSaleService.payment(muninSaleRequest,authentication));
    }


}
