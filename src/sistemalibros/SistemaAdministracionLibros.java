package sistemalibros;

import java.util.Scanner;

/**
 * Punto de entrada del Sistema de Administración de Libros.
 * Presenta un menú de consola que permite:
 *  1) Cargar masivamente libros.txt
 *  2) Cargar masivamente prestamos.txt
 *  3) Cargar masivamente existencias.txt
 *  4) Visualizar el Árbol B+ por niveles
 *  5) Visualizar el Montículo Máximo de préstamos
 *  6) Visualizar el Montículo Mínimo de existencias
 *  7) Visualizar la Tabla Hash (buckets y cadenas de colisión)
 *  8) Buscar un libro por ISBN
 *  0) Salir
 *
 * NOTA (NetBeans): al crear el proyecto "Java with Ant -> Java
 * Application" con nombre de paquete "sistemalibros", coloca los archivos
 * libros.txt, prestamos.txt y existencias.txt en la raíz del proyecto
 * (mismo nivel que build.xml) para poder referenciarlos con rutas
 * relativas simples como "libros.txt".
 */
public class SistemaAdministracionLibros {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        // Estructuras centrales del sistema, compartidas por todo el menú
        ArbolBMasLibros arbol = new ArbolBMasLibros();
        TablaHashLibros hash = new TablaHashLibros();
        MonticuloMaximoPrestamos maxHeap = new MonticuloMaximoPrestamos();
        MonticuloMinimoExistencias minHeap = new MonticuloMinimoExistencias();

        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerOpcion(teclado);

            switch (opcion) {
                case 1:
                    System.out.print("Ruta del archivo de libros (ej. libros.txt): ");
                    String rutaLibros = teclado.nextLine();
                    GestorArchivos.cargarLibros(rutaLibros, arbol, hash, maxHeap, minHeap);
                    break;

                case 2:
                    System.out.print("Ruta del archivo de préstamos (ej. prestamos.txt): ");
                    String rutaPrestamos = teclado.nextLine();
                    GestorArchivos.cargarPrestamos(rutaPrestamos, hash, maxHeap);
                    break;

                case 3:
                    System.out.print("Ruta del archivo de existencias (ej. existencias.txt): ");
                    String rutaExistencias = teclado.nextLine();
                    GestorArchivos.cargarExistencias(rutaExistencias, hash, minHeap);
                    break;

                case 4:
                    arbol.imprimir();
                    break;

                case 5:
                    maxHeap.imprimir();
                    break;

                case 6:
                    minHeap.imprimir();
                    break;

                case 7:
                    hash.mostrarTabla();
                    break;

                case 8:
                    System.out.print("ISBN a buscar: ");
                    String isbnBuscado = teclado.nextLine().trim();
                    Libro encontrado = hash.buscar(isbnBuscado);
                    if (encontrado != null) {
                        System.out.println("Libro encontrado:");
                        System.out.println(encontrado);
                    } else {
                        System.out.println("No existe ningún libro con ISBN: " + isbnBuscado);
                    }
                    break;

                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
            }
        }

        teclado.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n============ SISTEMA DE ADMINISTRACIÓN DE LIBROS ============");
        System.out.println("1. Cargar libros (libros.txt)");
        System.out.println("2. Cargar préstamos (prestamos.txt)");
        System.out.println("3. Cargar existencias (existencias.txt)");
        System.out.println("4. Mostrar Árbol B+ (por niveles)");
        System.out.println("5. Mostrar Montículo Máximo de préstamos");
        System.out.println("6. Mostrar Montículo Mínimo de existencias");
        System.out.println("7. Mostrar Tabla Hash (buckets y colisiones)");
        System.out.println("8. Buscar libro por ISBN");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    /** Lee de forma segura la opción numérica del menú, validando la entrada. */
    private static int leerOpcion(Scanner teclado) {
        String entrada = teclado.nextLine();
        try {
            return Integer.parseInt(entrada.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}