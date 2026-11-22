package com.food_truck.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class SenhaUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private SenhaUtil() { }

    public static String gerarSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String gerarHash(String senha, String saltBase64) {
        char[] chars = senha.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(chars,
                Base64.getDecoder().decode(saltBase64), 120_000, 256);
        try {
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível proteger a senha.", e);
        } finally {
            spec.clearPassword();
        }
    }

    public static boolean verificar(String senha, String salt, String hashEsperado) {
        byte[] informado = Base64.getDecoder().decode(gerarHash(senha, salt));
        byte[] esperado = Base64.getDecoder().decode(hashEsperado);
        return MessageDigest.isEqual(informado, esperado);
    }
}
