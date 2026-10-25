package crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TripleDES {

    private SecretKeySpec generarClave(BigInteger secreto)
            throws Exception {

        MessageDigest hash = MessageDigest.getInstance("SHA-256");

        byte[] bytes = hash.digest(
                secreto.toString().getBytes(StandardCharsets.UTF_8));

        byte[] clave = Arrays.copyOf(bytes, 24);

        return new SecretKeySpec(clave, "DESede");
    }

    public void cifrarArchivo(String rutaOrigen,
            String rutaDestino, BigInteger secreto) throws Exception {

        byte[] contenido = Files.readAllBytes(Paths.get(rutaOrigen));

        byte[] iv = new byte[8];
        new SecureRandom().nextBytes(iv);

        Cipher cifrador = Cipher.getInstance("DESede/CBC/PKCS5Padding");

        cifrador.init(
                Cipher.ENCRYPT_MODE,
                generarClave(secreto),
                new IvParameterSpec(iv));

        byte[] cifrado = cifrador.doFinal(contenido);

        // Guardar IV seguido del contenido cifrado
        byte[] resultado = new byte[iv.length + cifrado.length];

        System.arraycopy(iv, 0, resultado, 0, iv.length);
        System.arraycopy(cifrado, 0, resultado, iv.length, cifrado.length);

        Files.write(Paths.get(rutaDestino), resultado);
    }

    public String descifrarArchivo(String rutaArchivo,
            BigInteger secreto) throws Exception {

        byte[] archivo = Files.readAllBytes(Paths.get(rutaArchivo));

        if (archivo.length < 16) {
            throw new Exception("El archivo cifrado no es valido.");
        }

        byte[] iv = Arrays.copyOfRange(archivo, 0, 8);
        byte[] cifrado = Arrays.copyOfRange(
                archivo, 8, archivo.length);

        Cipher cifrador = Cipher.getInstance("DESede/CBC/PKCS5Padding");

        cifrador.init(
                Cipher.DECRYPT_MODE,
                generarClave(secreto),
                new IvParameterSpec(iv));

        byte[] descifrado = cifrador.doFinal(cifrado);

        return new String(descifrado, StandardCharsets.UTF_8);
    }
}
