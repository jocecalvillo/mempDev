package com.mx.feenicia.memphis.commom.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;


/**
 * Utility class for encryption/decryption operations using AES-256 with CBC mode.
 * Provides methods for:
 * - AES encryption/decryption
 * - SHA-256 hashing
 * - Access token generation
 * - Hex/byte array conversions
 *
 * @author Armando Jacobo
 * @since 12/03/2020
 * @modified  by  Jose Calvillo
 */

public final class EncryptFeenicia {

    private static final Logger log = LoggerFactory.getLogger(EncryptFeenicia.class);

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String SHA_256_ALGORITHM = "SHA-256";
    private static final String TOKEN_SEPARATOR = "_";

    private static final HexFormat HEX_FORMAT = HexFormat.of();

    // METODO PARA REGRESAR DE BYTE A HEXADECIMAL
    public static String byteArrayToHex(byte[] bytes) {
        return HEX_FORMAT.formatHex(bytes);
    }

    // METODO PARA REGRESAR DE HEXADECIMAL A UN ARREGLO DE BYTES
    public static byte[] hexStringToByteArray(String hexString) {
        return HEX_FORMAT.parseHex(hexString);
    }

    /* CIFRADO */
    public String encrypt(byte[] key, byte[] iv, String data) {
        if (data == null || data.isEmpty()) {
            log.warn("Attempted to encrypt null or empty data");
            return null;
        }

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return byteArrayToHex(encryptedBytes);

        } catch (Exception e) {
            log.error("AES encryption error for data length: {}", data.length(), e);
            return null;
        }
    }

    /* DECRYPT */
    public String decrypt(String encryptedData, byte[] iv, byte[] key) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            log.warn("Attempted to decrypt null or empty data");
            return null;
        }

        try {
            byte[] encryptedBytes = hexStringToByteArray(encryptedData);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("AES decryption error for data: {}", encryptedData, e);
            return null;
        }
    }

    /* GENERATE ACCESS TOKEN */
    public String generateAccessToken(String jsonRequest, byte[] key, byte[] iv, String token) {
        if (jsonRequest == null || jsonRequest.isEmpty()) {
            log.warn("Attempted to generate token for null or empty request");
            return null;
        }

        try {
            String hash = calculateSha256Hash(jsonRequest);
            String encryptedHash = encrypt(key, iv, hash);

            if (encryptedHash == null) {
                log.error("Failed to encrypt hash for token generation");
                return null;
            }

            return token + TOKEN_SEPARATOR + encryptedHash;

        } catch (Exception e) {
            log.error("Error generating access token", e);
            return null;
        }
    }

    public String calculateSha256Hash(String input) {
        if (input == null || input.isEmpty()) {
            log.warn("Attempted to calculate hash for null or empty input");
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256_ALGORITHM);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return byteArrayToHex(hashBytes).toLowerCase();

        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            return null;
        }
    }

    /* MÉTODOS ADICIONALES ÚTILES */

    public static byte[] generateRandomIv() {
        byte[] iv = new byte[16]; // 128 bits for AES
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static boolean validateHexString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) {
            return false;
        }
        return hexString.matches("[0-9A-Fa-f]+");
    }

}