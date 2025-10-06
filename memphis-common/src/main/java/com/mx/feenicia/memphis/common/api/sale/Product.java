package com.mx.feenicia.memphis.common.api.sale;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @NotNull
    @NotBlank
    private String id;
    @NotNull
    @NotBlank
    private String title;
    @PositiveOrZero
    private BigDecimal price;
    @NotNull
    @NotBlank
    private String sku;
    @Positive
    private Integer quantity;
}
