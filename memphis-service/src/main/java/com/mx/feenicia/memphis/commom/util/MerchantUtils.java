package com.mx.feenicia.memphis.commom.util;

import com.mx.feenicia.memphis.common.authentication.ClientUser;
import org.springframework.security.core.Authentication;

import java.util.Objects;

public class MerchantUtils {

    private MerchantUtils() {
        // Evita instanciación
    }

    /**
     * Obtiene el merchantId (sistema Feenicia) a partir de la autenticación.
     *
     * @param authentication Objeto de autenticación (no debe ser null).
     * @return merchantId asociado al usuario autenticado.
     * @throws IllegalArgumentException si la autenticación es nula o el principal no es un ClientUser.
     */
    public static String getMerchantFromAuthentication(Authentication authentication) {
        Objects.requireNonNull(authentication, "Authentication cannot be null");
        if (!(authentication.getPrincipal() instanceof ClientUser)) {
            throw new IllegalArgumentException("Principal is not a ClientUser");
        }
        ClientUser clientUser = (ClientUser) authentication.getPrincipal();
        return clientUser.getMerchantId();
    }
}
