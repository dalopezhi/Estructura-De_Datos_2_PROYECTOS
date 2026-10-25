package service;

import model.Libro;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CatalogoService {

    private Libro[] libros = new Libro[100];
    private int cantidadLibros = 0;

    public boolean cargar(String rutaServidor) {

        cantidadLibros = 0;

        File archivo = new File(rutaServidor, "catalogo.txt");

        try (BufferedReader lector = new BufferedReader(
                new FileReader(archivo))) {

            String linea;

            while ((linea = lector.readLine()) != null) {

                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.split("\\|", -1);

                if (datos.length != 2) {
                    continue;
                }

                String isbn = datos[0].trim();
                String titulo = datos[1].trim();

                if (isbn.isEmpty() || titulo.isEmpty()) {
                    continue;
                }

                if (cantidadLibros >= libros.length) {
                    System.out.println("Limite de libros alcanzado.");
                    break;
                }

                libros[cantidadLibros] = new Libro(isbn, titulo);
                cantidadLibros++;
            }

            return true;

        } catch (IOException error) {
            System.out.println("Error al cargar el catalogo.");
            return false;
        }
    }

    public void mostrarLibros() {

        if (cantidadLibros == 0) {
            System.out.println("No hay libros disponibles.");
            return;
        }

        System.out.println("\n--- CATALOGO ---");

        for (int i = 0; i < cantidadLibros; i++) {
            System.out.println((i + 1) + ". " + libros[i]);
        }
    }

    public Libro obtenerLibro(int posicion) {

        if (posicion < 0 || posicion >= cantidadLibros) {
            return null;
        }

        return libros[posicion];
    }

    public int getCantidadLibros() {
        return cantidadLibros;
    }
}
