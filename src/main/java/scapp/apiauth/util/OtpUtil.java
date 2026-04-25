package scapp.apiauth.util;

import java.security.SecureRandom;

public class OtpUtil {


    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private OtpUtil() {
    }

    public static String generarCodigo6Digitos() {
        int numero = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(numero);
    }
}
