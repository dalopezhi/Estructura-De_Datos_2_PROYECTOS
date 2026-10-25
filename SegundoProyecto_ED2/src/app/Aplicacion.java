package app;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import model.Libro;
import model.Sesion;
import model.Usuario;

import service.AuthService;
import service.CatalogoService;
import service.LibroService;

import storage.AccesoOffline;
import storage.Configuracion;

public class Aplicacion {

    private final Scanner entrada = new Scanner(System.in);
    private final Sesion sesion = new Sesion();
    private final LibroService libroService = new LibroService();

    private String rutaServidor;

    // ==========================================
    // INICIO DE LA APLICACION
    // ==========================================

    public void iniciar() {

        while (true) {

            System.out.println("\n=== BIBLIOTECA DIGITAL ===");
            System.out.println("1. Iniciar sesion en linea");
            System.out.println("2. Leer libros descargados (offline)");
            System.out.println("3. Salir");
            System.out.print("Opcion: ");

            int opcion = leerOpcion();

            switch (opcion) {

                case 1:
                    iniciarOnline();
                    break;

                case 2:
                    iniciarOffline();
                    break;

                case 3:
                    System.out.println("Hasta pronto.");
                    return;

                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }

    // ==========================================
    // LECTURA SEGURA DE OPCIONES
    // ==========================================

    private int leerOpcion() {

        try {
            return Integer.parseInt(
                    entrada.nextLine().trim());

        } catch (NumberFormatException error) {
            return -1;
        }
    }

    // ==========================================
    // INICIO DE SESION ONLINE
    // ==========================================

    private void iniciarOnline() {

        Configuracion configuracion = new Configuracion();

        if (!configuracion.cargar()) {
            return;
        }

        rutaServidor = configuracion.getRutaServidor();

        File carpetaServidor = new File(rutaServidor);

        if (!carpetaServidor.isDirectory()) {

            System.out.println(
                    "No se pudo acceder al servidor.");

            return;
        }

        AuthService autenticacion = new AuthService();

        System.out.println("\n=== INICIO DE SESION ===");

        System.out.print("Usuario: ");
        String nombreUsuario = entrada.nextLine().trim();

        System.out.print("Contrasena: ");
        String contrasena = entrada.nextLine();

        Usuario usuario = autenticacion.iniciarSesion(
                nombreUsuario, contrasena, rutaServidor);

        if (usuario == null) {
            return;
        }

        sesion.iniciarSesion(usuario);

        System.out.println("\nBienvenido, "
                + usuario.getNombreUsuario());

        System.out.println("Cuenta: "
                + (usuario.isPremium()
                        ? "Premium" : "Estandar"));

        // Guardar acceso local solo si el servidor
        // autentico correctamente al usuario Premium.

        if (usuario.isPremium()) {

            try {

                AccesoOffline.guardar(
                    usuario.getNombreUsuario(),
                    contrasena);

                System.out.println(
                        "Acceso offline habilitado en esta computadora.");

            } catch (IOException error) {

                System.out.println(
                        "No se pudo guardar el acceso offline.");
            }
        }

        CatalogoService catalogo = new CatalogoService();

        mostrarMenuPrincipal(catalogo, usuario);
    }

    // ==========================================
    // INICIO DE SESION OFFLINE
    // ==========================================

    private void iniciarOffline() {

        System.out.println("\n=== LECTURA OFFLINE PREMIUM ===");

        System.out.print("Usuario: ");
        String nombreUsuario = entrada.nextLine().trim();

        System.out.print("Contrasena: ");
        String contrasena = entrada.nextLine();

        if (!AccesoOffline.validar(
                nombreUsuario, contrasena)) {

            System.out.println(
                    "Acceso offline no autorizado.");

            return;
        }

        System.out.println(
                "Acceso offline Premium autorizado.");

        mostrarLecturaLocal(nombreUsuario);
    }

    // ==========================================
    // MENU PRINCIPAL ONLINE
    // ==========================================

    private void mostrarMenuPrincipal(
            CatalogoService catalogo, Usuario usuario) {

        while (sesion.estaActiva()) {

            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Explorar catalogo");

            if (usuario.isPremium()) {
                System.out.println("2. Lectura local");
            }

            System.out.println("3. Cerrar sesion");
            System.out.println("4. Regresar al inicio");
            System.out.print("Opcion: ");

            int opcion = leerOpcion();

            switch (opcion) {

                case 1:

                    if (catalogo.cargar(rutaServidor)) {
                        explorarCatalogo(catalogo);
                    }

                    break;

                case 2:

                    if (usuario.isPremium()) {
                        mostrarLecturaLocal(usuario.getNombreUsuario());
                    } else {
                        System.out.println(
                                "Opcion no disponible.");
                    }

                    break;

                case 3:

                    sesion.cerrarSesion();
                    System.out.println("Sesion cerrada.");
                    return;

                case 4:

                    sesion.cerrarSesion();
                    return;

                default:

                    System.out.println("Opcion no valida.");
            }
        }
    }

    // ==========================================
    // EXPLORAR CATALOGO
    // ==========================================

    private void explorarCatalogo(
            CatalogoService catalogo) {

        while (true) {

            catalogo.mostrarLibros();

            System.out.println("\n0. Regresar");
            System.out.print("Seleccione un libro: ");

            int opcion = leerOpcion();

            if (opcion == 0) {
                return;
            }

            Libro libro = catalogo.obtenerLibro(
                    opcion - 1);

            if (libro == null) {

                System.out.println(
                        "Opcion invalida. Seleccione un libro del catalogo.");

                continue;
            }

            System.out.println("\nLibro seleccionado:");
            System.out.println("ISBN: " + libro.getIsbn());
            System.out.println("Titulo: " + libro.getTitulo());

            // Transferir el archivo cifrado
            // desde el servidor hacia el cliente.

            String rutaLibro =
                    libroService.obtenerLibroCifrado(
                            libro, rutaServidor);

            if (rutaLibro == null) {

                System.out.println(
                        "No fue posible obtener el libro.");

                continue;
            }

            System.out.println(
                    "Copia cifrada guardada en: "
                    + rutaLibro);

            // Los usuarios Premium pueden leer
            // o descargar el libro comprimido.

            if (sesion.getUsuarioActual().isPremium()) {

                System.out.println("\n1. Leer en linea");
                System.out.println("2. Descargar");
                System.out.println("0. Regresar");
                System.out.print("Opcion: ");

                int opcionLibro = leerOpcion();

                switch (opcionLibro) {

                    case 1:

                        libroService.leerLibro(rutaLibro);
                        break;

                    case 2:

                        libroService.descargarLibro(
                            libro,
                            rutaLibro,
                            sesion.getUsuarioActual().getNombreUsuario());
                        break;

                    case 0:
                        break;

                    default:

                        System.out.println(
                                "Opcion no valida.");
                }

            } else {

                // Los usuarios estandar solamente
                // pueden leer el libro en linea.

                libroService.leerLibro(rutaLibro);
            }
        }
    }

    // ==========================================
    // LECTURA DE LIBROS DESCARGADOS
    // ==========================================

    private void mostrarLecturaLocal(String nombreUsuario) {

        File[] librosDescargados =
                libroService.obtenerLibrosDescargados(nombreUsuario);

        if (librosDescargados == null
                || librosDescargados.length == 0) {

            System.out.println(
                    "No hay libros descargados.");

            return;
        }

        while (true) {

            System.out.println(
                    "\n=== LIBROS DESCARGADOS ===");

            for (int i = 0;
                    i < librosDescargados.length; i++) {

                System.out.println(
                        (i + 1) + ". "
                        + librosDescargados[i].getName());
            }

            System.out.println("0. Regresar");
            System.out.print("Seleccione un libro: ");

            int opcion = leerOpcion();

            if (opcion == 0) {
                return;
            }

            if (opcion < 1
                    || opcion > librosDescargados.length) {

                System.out.println(
                        "Opcion invalida.");

                continue;
            }

            File archivo = librosDescargados[opcion - 1];

            String nombreArchivo = archivo.getName();

            // Eliminar la extension .txt
            // para recuperar el ISBN.

            String isbn = nombreArchivo.substring(
                    0, nombreArchivo.length() - 4);

            Libro libro = new Libro(isbn, isbn);

            libroService.leerLibroLocal(libro, nombreUsuario);
        }
    }
}

