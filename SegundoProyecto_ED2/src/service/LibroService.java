package service;

import java.io.File;
import model.Libro;
import storage.ArchivoService;
import crypto.DiffieHellman;
import crypto.TripleDES;
import java.math.BigInteger;
import compression.Huffman;
import java.io.File;

public class LibroService {

    private ArchivoService archivos = new ArchivoService();

    public String obtenerLibroCifrado(Libro libro, String rutaServidor) {

        // Ubicacion del libro en el servidor
        File origen = new File(
                new File(rutaServidor, "libros"),
                libro.getIsbn() + ".txt");

        // Ubicacion de la copia en el cliente
        File destino = new File(
                "data/cache_cifrada",
                libro.getIsbn() + ".txt");

        boolean transferido = archivos.copiarArchivo(
                origen.getPath(), destino.getPath());

        if (!transferido) {
            return null;
        }

        return destino.getPath();
    }
    
    
    public void leerLibro(String rutaLibro) {

        try {
            DiffieHellman intercambio = new DiffieHellman();

            BigInteger secreto = intercambio.obtenerClaveCompartida();

            TripleDES cifrado = new TripleDES();

            String contenido = cifrado.descifrarArchivo(
                    rutaLibro, secreto);

            System.out.println("\n========== LIBRO ==========");
            System.out.println(contenido);
            System.out.println("===========================");

        } catch (Exception error) {
            System.out.println("No se pudo descifrar el libro.");
            System.out.println("Detalle: " + error.getMessage());
        }
    }
    
    
    public boolean descargarLibro(Libro libro, String rutaLibro) {

        File temporal = null;

        try {
            DiffieHellman intercambio = new DiffieHellman();
            TripleDES cifrado = new TripleDES();
            Huffman huffman = new Huffman();

            BigInteger secreto = intercambio.obtenerClaveCompartida();

            // Obtener el contenido original del libro
            String contenido = cifrado.descifrarArchivo(
                    rutaLibro, secreto);

            File carpeta = new File("data/descargas");

            if (!carpeta.exists() && !carpeta.mkdirs()) {
                System.out.println("No se pudo crear la carpeta.");
                return false;
            }

            File destino = new File(
                    carpeta, libro.getIsbn() + ".txt");

            // Crear archivo temporal en la misma carpeta
            temporal = File.createTempFile(
                    "libro_", ".tmp", carpeta);

            // Comprimir sin modificar la descarga anterior
            huffman.comprimir(contenido, temporal.getPath());

            // Verificar que se pueda recuperar el texto
            String recuperado = huffman.descomprimir(
                    temporal.getPath());

            if (!contenido.equals(recuperado)) {
                System.out.println("Error: la compresion altero el contenido.");
                return false;
            }

            // Reemplazar la descarga solo despues de verificarla
            java.nio.file.Files.move(
                    temporal.toPath(),
                    destino.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Libro descargado correctamente.");
            System.out.println("Integridad Huffman verificada.");
            
            
            int tamañoOriginal = contenido.getBytes(
                    java.nio.charset.StandardCharsets.UTF_8).length;

            long tamañoComprimido = destino.length();

            System.out.println("Tamaño original: "
                    + tamañoOriginal + " bytes");

            System.out.println("Tamaño guardado: "
                    + tamañoComprimido + " bytes");

            if (tamañoOriginal > 0) {

                double porcentaje = 100.0 *
                        (1.0 - (double) tamañoComprimido / tamañoOriginal);

                System.out.printf(
                        "Reduccion de tamaño: %.2f%%%n", porcentaje);
            }

            return true;
           
        } catch (javax.crypto.BadPaddingException error) {
            System.out.println("No se pudo descifrar el libro.");
            System.out.println("Verifique que el archivo este cifrado con la clave correcta.");
            return false;

        } catch (Exception error) {
            System.out.println("Error al descargar: " + error.getMessage());
            return false;

        } finally {
            if (temporal != null && temporal.exists()) {
                temporal.delete();
            }
        }
    }

    public void leerLibroLocal(Libro libro) {

        try {
            String ruta = new File(
                    "data/descargas",
                    libro.getIsbn() + ".txt").getPath();

            if (!new File(ruta).isFile()) {
                System.out.println("El libro no esta descargado.");
                return;
            }

            Huffman huffman = new Huffman();
            String contenido = huffman.descomprimir(ruta);

            System.out.println("\n========== LIBRO ==========");
            System.out.println(contenido);
            System.out.println("===========================");

        } catch (Exception error) {
            System.out.println("Error al leer: " + error.getMessage());
        }
    }
        
    public File[] obtenerLibrosDescargados() {

        File carpeta = new File("data/descargas");

        if (!carpeta.exists() || !carpeta.isDirectory()) {
            return new File[0];
        }

        File[] archivos = carpeta.listFiles(
                (directorio, nombre) -> nombre.endsWith(".txt"));

        if (archivos == null) {
            return new File[0];
        }

        return archivos;
    }
}

