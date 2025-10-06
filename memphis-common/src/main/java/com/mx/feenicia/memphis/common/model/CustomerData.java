package com.mx.feenicia.memphis.common.model;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerData {
    private String firstName;
    private String lastName;


    @Pattern(regexp = "^(\\+)(\\d){10,15}$",
            message = "Phone number does not match with the correct format. E.g. +593988734644")
    private String phone;



    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email format is not valid")
    private String email;

    private String documentType;
    private String documentNumber;
}
