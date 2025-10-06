package com.mx.feenicia.memphis.commom.util;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Clase utilitaria para la generación de un Token de Autenticación (TKA)
 * a partir de un Token de Registro (TKR) y una afiliación.
 *
 * <p>El proceso de generación incluye los siguientes pasos:</p>
 * <ol>
 *     <li>Obtener el timestamp actual (segundos desde epoch).</li>
 *     <li>Realizar una operación OR bit a bit entre el TKR y el timestamp.</li>
 *     <li>Aplicar una operación XOR bit a bit con la afiliación.</li>
 *     <li>Codificar el resultado en Base64.</li>
 *     <li>Hashear el valor usando bcrypt con un salt de 11 rondas.</li>
 * </ol>
 *
 * <p>Este mecanismo asegura un nivel de aleatoriedad y robustez para
 * la generación del token.</p>
 *
 * @author  Jose Calviillo
 */
public class TokenGenerator {

    public static String generateTKA(String tkr, String timestamp, String commerceId)  {
        // Convertir a buffers (equivalente a Buffer.from() en NodeJS)
        byte[] dato1 = tkr.getBytes(StandardCharsets.UTF_8);
        byte[] dato2 = timestamp.getBytes(StandardCharsets.UTF_8);
        byte[] dato3 = commerceId.getBytes(StandardCharsets.UTF_8);

        // Operación OR (equivalente a bitwise.buffer.or)
        byte[] sk1 = bufferOr(dato1, dato2);

        // Operación XOR (equivalente a bitwise.buffer.xor)
        byte[] sk2 = bufferXor(sk1, dato3);

        // Convertir a Base64
        String base64 = Base64.getEncoder().encodeToString(sk2);

        // BCrypt con salt de 11
        String tka = BCrypt.hashpw(base64, BCrypt.gensalt(11));

        return tka;
    }

    // Operación OR equivalente a bitwise.buffer.or
    private static byte[] bufferOr(byte[] a, byte[] b) {
        int maxLength = Math.max(a.length, b.length);
        byte[] result = new byte[maxLength];

        for (int i = 0; i < maxLength; i++) {
            byte byteA = (i < a.length) ? a[i] : 0;
            byte byteB = (i < b.length) ? b[i] : 0;
            result[i] = (byte) (byteA | byteB);
        }
        return result;
    }

    // Operación XOR equivalente a bitwise.buffer.xor
    private static byte[] bufferXor(byte[] a, byte[] b) {
        int maxLength = Math.max(a.length, b.length);
        byte[] result = new byte[maxLength];

        for (int i = 0; i < maxLength; i++) {
            byte byteA = (i < a.length) ? a[i] : 0;
            byte byteB = (i < b.length) ? b[i] : 0;
            result[i] = (byte) (byteA ^ byteB);
        }
        return result;
    }


}