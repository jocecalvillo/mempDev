package com.mx.feenicia.memphis.commom.controller;


import com.mx.feenicia.memphis.commom.service.MemphisCancelService;
import com.mx.feenicia.memphis.common.feenicia.AtenaResponse;
import com.mx.feenicia.memphis.common.model.AtenaTxByRequest;
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
@RequestMapping("/cancel")
@Validated
public class MemphisCancelController {

    private final MemphisCancelService memphisCancelService;

    public MemphisCancelController(MemphisCancelService memphisCancelService) {
        this.memphisCancelService = memphisCancelService;
    }

    @PostMapping
    public ResponseEntity<AtenaResponse> cancelRequest(@RequestBody @Valid AtenaTxByRequest atenaTxByRequest) {
        return ResponseEntity.ok(memphisCancelService.cancelById(atenaTxByRequest));
    }
}
