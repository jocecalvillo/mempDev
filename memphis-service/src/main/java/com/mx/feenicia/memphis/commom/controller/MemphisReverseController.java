package com.mx.feenicia.memphis.commom.controller;

import com.mx.feenicia.memphis.commom.service.MemphisReversalService;
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
@RequestMapping("/reverse")
@Validated
public class MemphisReverseController {

    private final MemphisReversalService memphisReversalService;

    public MemphisReverseController(MemphisReversalService memphisReversalService) {
        this.memphisReversalService = memphisReversalService;
    }

    @PostMapping
    public ResponseEntity<AtenaResponse> reverseRequest(@RequestBody @Valid AtenaTxByRequest atenaTxByRequest) {
        return ResponseEntity.ok(memphisReversalService.reversalById(atenaTxByRequest));
    }

}
