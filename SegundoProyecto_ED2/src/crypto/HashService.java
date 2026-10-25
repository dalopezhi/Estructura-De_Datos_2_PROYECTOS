package crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashService {

    public String generarHash(String contraseña) {

        try {
            MessageDigest algoritmo = MessageDigest.getInstance("SHA-256");

            byte[] bytes = algoritmo.digest(
                    contraseña.getBytes(StandardCharsets.UTF_8));

            StringBuilder resultado = new StringBuilder();

            for (byte valor : bytes) {
                resultado.append(String.format("%02x", valor & 0xff));
            }

            return resultado.toString();

        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException(
                    "SHA-256 no esta disponible.", error);
        }
    }
}
