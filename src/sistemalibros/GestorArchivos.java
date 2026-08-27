package sistemalibros;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase de lógica de negocio encargada de leer los archivos de texto
 * (separador '|') y sincronizar las cuatro estructuras de datos del
 * sistema: Árbol B+, Tabla Hash, Montículo Máximo (préstamos) y
 * Montículo Mínimo (existencias).
 *
 * Se apoya en la Tabla Hash como fuente de verdad para saber si un ISBN
 * ya existe (búsqueda O(1) promedio), evitando así una segunda búsqueda
 * costosa en el Árbol B+. Como todas las estructuras comparten la misma
 * referencia de objeto Libro, actualizar sus atributos aquí se refleja
 * automáticamente en el Árbol B+ y en los montículos; estos últimos solo
 * necesitan reordenar su posición interna (reordenar) tras el cambio.
 */
public class GestorArchivos {

    /**
     * Carga (o actualiza) libros desde libros.txt.
     * Formato esperado por línea:
     * ISBN|Titulo|Autor|Editorial|Anio|Categoria|CantidadPrestada|CantidadDisponible
     */
    public static void cargarLibros(String rutaArchivo, ArbolBMasLibros arbol, TablaHashLibros hash, MonticuloMaximoPrestamos maxHeap, MonticuloMinimoExistencias minHeap) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numeroLinea = 0;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                if (linea.trim().isEmpty()) continue;

                String[] campos = linea.split("\\|");
                if (campos.length < 8) {
                    System.out.println("Línea " + numeroLinea + " inválida (se esperaban 8 campos), se omite: " + linea);
                    continue;
                }

                try {
                    String isbn = campos[0].trim();
                    String titulo = campos[1].trim();
                    String autor = campos[2].trim();
                    String editorial = campos[3].trim();
                    int anio = Integer.parseInt(campos[4].trim());
                    String categoria = campos[5].trim();
                    int cantidadPrestada = Integer.parseInt(campos[6].trim());
                    int cantidadDisponible = Integer.parseInt(campos[7].trim());

                    Libro existente = hash.buscar(isbn);
                    if (existente != null) {
                        // El ISBN ya existe: se actualizan sus datos in-place.
                        // Como árbol/hash/heaps comparten la misma referencia,
                        // basta con reordenar ambos montículos por si las
                        // cantidades cambiaron.
                        existente.actualizarDatos(titulo, autor, editorial, anio,
                                categoria, cantidadPrestada, cantidadDisponible);
                        maxHeap.reordenar(isbn);
                        minHeap.reordenar(isbn);
                        System.out.println("[Actualizado] " + isbn + " - " + titulo);
                    } else {
                        Libro nuevo = new Libro(isbn, titulo, autor, editorial, anio,
                                categoria, cantidadPrestada, cantidadDisponible);
                        arbol.insertar(nuevo);
                        hash.insertar(nuevo);
                        maxHeap.insertar(nuevo);
                        minHeap.insertar(nuevo);
                        System.out.println("[Insertado]  " + isbn + " - " + titulo);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Línea " + numeroLinea + " con dato numérico inválido, se omite: " + linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer '" + rutaArchivo + "': " + e.getMessage());
        }
    }

    /**
     * Carga movimientos de préstamo desde prestamos.txt.
     * Formato esperado por línea: ISBN|fecha|cantidad
     * Suma 'cantidad' a la cantidadPrestada del libro y reordena el
     * Montículo Máximo para reflejar el nuevo valor.
     */
    public static void cargarPrestamos(String rutaArchivo, TablaHashLibros hash,
                                        MonticuloMaximoPrestamos maxHeap) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numeroLinea = 0;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                if (linea.trim().isEmpty()) continue;

                String[] campos = linea.split("\\|");
                if (campos.length < 3) {
                    System.out.println("Línea " + numeroLinea + " inválida (se esperaban 3 campos), se omite: " + linea);
                    continue;
                }

                try {
                    String isbn = campos[0].trim();
                    // campos[1] = fecha (no requiere procesamiento numérico)
                    int cantidad = Integer.parseInt(campos[2].trim());

                    Libro libro = hash.buscar(isbn);
                    if (libro == null) {
                        System.out.println("[Omitido] Préstamo para ISBN no registrado: " + isbn);
                        continue;
                    }
                    libro.setCantidadPrestada(libro.getCantidadPrestada() + cantidad);
                    maxHeap.reordenar(isbn);
                    System.out.println("[Préstamo] " + isbn + " -> total prestados: " + libro.getCantidadPrestada());
                } catch (NumberFormatException e) {
                    System.out.println("Línea " + numeroLinea + " con dato numérico inválido, se omite: " + linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer '" + rutaArchivo + "': " + e.getMessage());
        }
    }

    /**
     * Carga movimientos de existencias desde existencias.txt.
     * Formato esperado por línea: ISBN|fecha|cantidad
     * Suma 'cantidad' a la cantidadDisponible del libro y reordena el
     * Montículo Mínimo para reflejar el nuevo valor.
     */
    public static void cargarExistencias(String rutaArchivo, TablaHashLibros hash,
                                          MonticuloMinimoExistencias minHeap) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numeroLinea = 0;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                if (linea.trim().isEmpty()) continue;

                String[] campos = linea.split("\\|");
                if (campos.length < 3) {
                    System.out.println("Línea " + numeroLinea + " inválida (se esperaban 3 campos), se omite: " + linea);
                    continue;
                }

                try {
                    String isbn = campos[0].trim();
                    int cantidad = Integer.parseInt(campos[2].trim());

                    Libro libro = hash.buscar(isbn);
                    if (libro == null) {
                        System.out.println("[Omitido] Existencia para ISBN no registrado: " + isbn);
                        continue;
                    }
                    libro.setCantidadDisponible(libro.getCantidadDisponible() + cantidad);
                    minHeap.reordenar(isbn);
                    System.out.println("[Existencia] " + isbn + " -> total disponibles: " + libro.getCantidadDisponible());
                } catch (NumberFormatException e) {
                    System.out.println("Línea " + numeroLinea + " con dato numérico inválido, se omite: " + linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer '" + rutaArchivo + "': " + e.getMessage());
        }
    }
}