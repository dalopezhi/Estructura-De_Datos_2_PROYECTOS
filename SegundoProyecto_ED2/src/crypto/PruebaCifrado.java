
package crypto;

import java.math.BigInteger;
import java.io.File;

public class PruebaCifrado {

    public static void main(String[] args) {

        try {
            DiffieHellman intercambio = new DiffieHellman();
            BigInteger secreto = intercambio.obtenerClaveCompartida();

            TripleDES cifrado = new TripleDES();

            String rutaServidor = "C:\\BibliotecaServidor\\libros";

            // Libros de prueba
            String[] isbn = {"978001", "978002", "978003"};

            for (int i = 0; i < isbn.length; i++) {

                String rutaOrigen = "data/libro_prueba.txt";

                String rutaDestino = new File(
                        rutaServidor, isbn[i] + ".txt").getPath();

                cifrado.cifrarArchivo(
                        rutaOrigen, rutaDestino, secreto);

                System.out.println("Libro cifrado: " + isbn[i]);
            }

            System.out.println("\nTodos los libros fueron cifrados.");

        } catch (Exception error) {
            System.out.println("Error: " + error.getMessage());
        }
    }
}
