package service;

import crypto.HashService;
import model.Usuario;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AuthService {

    private HashService hashService = new HashService();

    public Usuario iniciarSesion(String nombreUsuario,
            String contraseña, String rutaServidor) {

        File archivo = new File(rutaServidor, "usuarios.txt");

        try (BufferedReader lector = new BufferedReader(
                new FileReader(archivo))) {

            String linea;
            String hashIngresado = hashService.generarHash(contraseña);

            while ((linea = lector.readLine()) != null) {

                String[] datos = linea.split("\\|", -1);

                if (datos.length != 3) {
                    continue;
                }

                String usuarioGuardado = datos[0].trim();
                String hashGuardado = datos[1].trim();
                String valorPremium = datos[2].trim();

                if (!valorPremium.equalsIgnoreCase("true")
                        && !valorPremium.equalsIgnoreCase("false")) {
                    continue;
                }

                if (usuarioGuardado.equals(nombreUsuario)
                        && hashGuardado.equalsIgnoreCase(hashIngresado)) {

                    boolean premium = Boolean.parseBoolean(valorPremium);

                    return new Usuario(usuarioGuardado,
                            hashGuardado, premium);
                }
            }

        } catch (IOException error) {
            System.out.println("No se pudo acceder a los usuarios del servidor.");
            return null;
        }

        System.out.println("Usuario o contraseña incorrectos.");
        return null;
    }
}
