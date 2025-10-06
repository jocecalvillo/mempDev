package com.mx.feenicia.memphis.commom.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keys.properties.config")
public class AesProperties {
    private String requestSignatureIV;
    private String requestSignatureKEY;
    private String responseIV;
    private String responseKEY;

}
