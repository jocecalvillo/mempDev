package com.mx.feenicia.memphis.common.model;

import com.mx.feenicia.memphis.common.api.sale.Product;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MemphisSaleRequest {

    @NotNull
    @Positive
    private Long affiliation;

    @NotNull
    @NotBlank
    private String cardholderName;

    @NotNull
    @NotBlank
    private String pan;

    @NotNull
    @NotBlank
    private String cvv2;

    @NotNull
    @NotBlank
    private String expDate;

    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

    @Valid
    @NotNull
    private ExtendedData extendedData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExtendedData {

        @Valid
        private BillingData billingData;

        @Valid
        private BillingData shippingData;

        @Pattern(regexp = "(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z0-9][a-z0-9-]{0,61}[a-z0-9]",
                message = "The domain format is not correct. E.g. company.com")
        private String siteDomain;

        @Valid
        private CustomerData customerData;

        @NotNull
        @NotBlank
        @NotEmpty
        private String currency;

        @NotNull
        @PositiveOrZero
        private Long transactionId;

        @Valid
        @NotNull
        private List<Product> productData;

    }

}
