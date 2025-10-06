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
public class BillingData {
    private String firstName;
    private String lastName;

    private String street;
    private String city;
    private String country;
    @Pattern(regexp = "^(\\d){5}$",
            message = "The postcode must have five digits.")
    private String postCode;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email format is not valid")
    private String email;

    private String state;

    @Pattern(regexp = "^(\\+)(\\d){10,15}$",
            message = "Phone number does not match with the correct format. E.g. +593988734644")
    private String phone;
}
