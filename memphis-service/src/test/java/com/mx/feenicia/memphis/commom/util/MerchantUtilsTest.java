package com.mx.feenicia.memphis.commom.util;

import com.mx.feenicia.memphis.common.authentication.ClientUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MerchantUtilsTest {

    @Test
    void getMerchantFromAuthentication_returnsMerchantId() {
        ClientUser user = new ClientUser();
        user.setMerchantId("M123");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);

        String result = MerchantUtils.getMerchantFromAuthentication(auth);
        assertEquals("M123", result);
    }

    @Test
    void getMerchantFromAuthentication_throwsWhenAuthNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> MerchantUtils.getMerchantFromAuthentication(null));
        assertTrue(ex.getMessage().toLowerCase().contains("authentication"));
    }

    @Test
    void getMerchantFromAuthentication_throwsWhenPrincipalIsNotClientUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("not-a-clientuser");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> MerchantUtils.getMerchantFromAuthentication(auth));
        assertTrue(ex.getMessage().toLowerCase().contains("clientuser"));
    }
}