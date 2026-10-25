package storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ArchivoService {

    
public boolean copiarArchivo(String rutaOrigen, String rutaDestino) {

        File origen = new File(rutaOrigen);
        File destino = new File(rutaDestino);
        File temporal = new File(rutaDestino + ".tmp");

        if (!origen.isFile()) {
            System.out.println("El archivo de origen no existe.");
            return false;
        }

        File carpetaDestino = destino.getParentFile();

        if (carpetaDestino != null && !carpetaDestino.exists()) {
            if (!carpetaDestino.mkdirs()) {
                System.out.println("No se pudo crear la carpeta destino.");
                return false;
            }
        }

        try (FileInputStream entrada = new FileInputStream(origen);
             FileOutputStream salida = new FileOutputStream(temporal)) {

            byte[] bloque = new byte[4096];
            int bytesLeidos;

            while ((bytesLeidos = entrada.read(bloque)) != -1) {
                salida.write(bloque, 0, bytesLeidos);
            }

        } catch (IOException error) {
            temporal.delete();
            System.out.println("Error al transferir el archivo.");
            return false;
        }

        // Reemplazar el archivo solamente si la copia termino
        try {
            java.nio.file.Files.move(
                    temporal.toPath(),
                    destino.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Archivo transferido correctamente.");
            return true;

        } catch (IOException error) {
            temporal.delete();
            System.out.println("No se pudo guardar el archivo.");
            return false;
        }
    }

}

