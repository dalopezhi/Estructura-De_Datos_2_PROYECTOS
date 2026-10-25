package storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Configuracion {

    private String rutaServidor;

    public boolean cargar() {

        try (BufferedReader lector = new BufferedReader(
                new FileReader("data/configuracion.txt"))) {

            rutaServidor = lector.readLine();

            if (rutaServidor == null || rutaServidor.trim().isEmpty()) {
                System.out.println("La ruta del servidor esta vacia.");
                return false;
            }

            rutaServidor = rutaServidor.trim();
            return true;

        } catch (IOException error) {
            System.out.println("Error al cargar la configuracion.");
            return false;
        }
    }

    public String getRutaServidor() {
        return rutaServidor;
    }
}


