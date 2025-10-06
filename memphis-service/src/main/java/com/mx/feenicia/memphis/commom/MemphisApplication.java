package com.mx.feenicia.memphis.commom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mx.feenicia.memphis")
public class MemphisApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemphisApplication.class, args);
    }


}