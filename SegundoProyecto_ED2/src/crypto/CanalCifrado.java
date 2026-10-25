package crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class CanalCifrado {

    private final SecretKeySpec clave;
    private final SecureRandom aleatorio =
            new SecureRandom();

    public CanalCifrado(byte[] secretoDH)
            throws Exception {

        MessageDigest sha =
                MessageDigest.getInstance("SHA-256");

        byte[] hash = sha.digest(secretoDH);

        byte[] clave24 =
                Arrays.copyOf(hash, 24);

        clave = new SecretKeySpec(
                clave24, "DESede");
    }

    public String cifrar(String mensaje)
            throws Exception {

        byte[] iv = new byte[8];
        aleatorio.nextBytes(iv);

        Cipher cipher =
                Cipher.getInstance(
                        "DESede/CBC/PKCS5Padding");

        cipher.init(
                Cipher.ENCRYPT_MODE,
                clave,
                new IvParameterSpec(iv));

        byte[] cifrado = cipher.doFinal(
                mensaje.getBytes(
                        StandardCharsets.UTF_8));

        byte[] resultado =
                new byte[iv.length + cifrado.length];

        System.arraycopy(
                iv, 0, resultado, 0, iv.length);

        System.arraycopy(
                cifrado, 0, resultado,
                iv.length, cifrado.length);

        return Base64.getEncoder()
                .encodeToString(resultado);
    }

    public String descifrar(String mensajeCifrado)
            throws Exception {

        byte[] datos =
                Base64.getDecoder().decode(
                        mensajeCifrado);

        if (datos.length < 16) {
            throw new IllegalArgumentException(
                    "Mensaje cifrado invalido.");
        }

        byte[] iv =
                Arrays.copyOfRange(datos, 0, 8);

        byte[] cifrado =
                Arrays.copyOfRange(
                        datos, 8, datos.length);

        Cipher cipher =
                Cipher.getInstance(
                        "DESede/CBC/PKCS5Padding");

        cipher.init(
                Cipher.DECRYPT_MODE,
                clave,
                new IvParameterSpec(iv));

        byte[] original =
                cipher.doFinal(cifrado);

        return new String(
                original,
                StandardCharsets.UTF_8);
    }
}
