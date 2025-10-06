package com.mx.feenicia.memphis.commom.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.mock.env.MockEnvironment;

public class AesPropertiesBindingTest {

    @Test
    void bindsProperties() {
        MockEnvironment env = new MockEnvironment()
                .withProperty("keys.properties.config.request-signature-iv", "AAA111")
                .withProperty("keys.properties.config.request-signature-key", "BBB222")
                .withProperty("keys.properties.config.response-iv", "CCC333")
                .withProperty("keys.properties.config.response-key", "DDD444");

        Binder binder = Binder.get(env);
        AesProperties props = new AesProperties();
        binder.bind("keys.properties.config", Bindable.ofInstance(props));

        assertEquals("AAA111", props.getRequestSignatureIV());
        assertEquals("BBB222", props.getRequestSignatureKEY());
        assertEquals("CCC333", props.getResponseIV());
        assertEquals("DDD444", props.getResponseKEY());
    }
}