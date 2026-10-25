package storage;

import crypto.HashService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import model.Usuario;
import crypto.HashService;

public class AccesoOffline {

    private static final String RUTA =
            "data/acceso_offline.properties";

    public static void guardar(Usuario usuario) throws IOException {

        if (usuario == null || !usuario.isPremium()) {
            return;
        }

        File archivo = new File(RUTA);
        File carpeta = archivo.getParentFile();

        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        Properties datos = new Properties();

        datos.setProperty("usuario", usuario.getNombreUsuario());
        datos.setProperty("hash", usuario.getHash());
        datos.setProperty("premium", "true");

        try (FileOutputStream salida =
                new FileOutputStream(archivo)) {

            datos.store(salida, "Acceso offline Premium");
        }
    }

    public static boolean validar(
            String nombreUsuario, String contrasena) {

        File archivo = new File(RUTA);

        if (!archivo.isFile()) {
            return false;
        }

        Properties datos = new Properties();

        try (FileInputStream entrada =
                new FileInputStream(archivo)) {

            datos.load(entrada);

            HashService hashService = new HashService();

            String hashIngresado =
            hashService.generarHash(contrasena);

            return nombreUsuario.equals(
                    datos.getProperty("usuario"))
                    && hashIngresado.equals(
                            datos.getProperty("hash"))
                    && "true".equals(
                            datos.getProperty("premium"));

        } catch (IOException e) {
            return false;
        }
    }
}
