package com.mx.feenicia.memphis.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
    @Data                      // Genera getters, setters, equals, hashCode y toString
    @NoArgsConstructor         // Genera constructor vacío
    @AllArgsConstructor        // Genera constructor con todos los campos
    public class Card {

        private String name;
        private String number;
        private String expiryMonth;
        private String expiryYear;
        private String cvv;

    }

