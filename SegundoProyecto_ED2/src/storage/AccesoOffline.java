package storage;

import crypto.HashService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AccesoOffline {

    private static final String CARPETA =
            "data/accesos_offline";

    private static File archivoUsuario(String usuario) {

        if (usuario == null
                || !usuario.matches("[a-zA-Z0-9_-]+")) {

            throw new IllegalArgumentException(
                    "Nombre de usuario invalido.");
        }

        return new File(CARPETA, usuario + ".properties");
    }

    // Guardar autorizacion despues de un
    // inicio de sesion online Premium exitoso.
    public static void guardar(
            String nombreUsuario,
            String contrasena) throws IOException {

        File carpeta = new File(CARPETA);

        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException(
                    "No se pudo crear la carpeta offline.");
        }

        HashService hashService = new HashService();

        Properties datos = new Properties();

        datos.setProperty("usuario", nombreUsuario);
        datos.setProperty(
                "hash", hashService.generarHash(contrasena));
        datos.setProperty("premium", "true");

        try (FileOutputStream salida =
                new FileOutputStream(
                        archivoUsuario(nombreUsuario))) {

            datos.store(
                    salida, "Acceso offline Premium");
        }
    }

    // Validar sin consultar al servidor.
    public static boolean validar(
            String nombreUsuario,
            String contrasena) {

        File archivo;

        try {
            archivo = archivoUsuario(nombreUsuario);
        } catch (IllegalArgumentException error) {
            return false;
        }

        if (!archivo.isFile()) {
            return false;
        }

        try (FileInputStream entrada =
                new FileInputStream(archivo)) {

            Properties datos = new Properties();
            datos.load(entrada);

            HashService hashService = new HashService();

            String hashIngresado =
                    hashService.generarHash(contrasena);

            String hashGuardado =
                    datos.getProperty("hash", "");

            return nombreUsuario.equals(
                    datos.getProperty("usuario"))
                    && "true".equals(
                            datos.getProperty("premium"))
                    && MessageDigest.isEqual(
                            hashIngresado.getBytes(
                                    StandardCharsets.UTF_8),
                            hashGuardado.getBytes(
                                    StandardCharsets.UTF_8));

        } catch (IOException error) {
            return false;
        }
    }
}
