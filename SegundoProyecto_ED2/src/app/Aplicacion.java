package app;

import java.util.Scanner;
import service.LibroService;
import model.Libro;
import model.Sesion;
import model.Usuario;
import service.AuthService;
import service.CatalogoService;
import storage.Configuracion;
import java.io.File;

public class Aplicacion {

    private Scanner entrada = new Scanner(System.in);
    private Sesion sesion = new Sesion();
    private LibroService libroService = new LibroService();
    private String rutaServidor;

    public void iniciar() {

        Configuracion configuracion = new Configuracion();

        if (!configuracion.cargar()) {
            return;
        }

        rutaServidor = configuracion.getRutaServidor();

        CatalogoService catalogo = new CatalogoService();
        AuthService autenticacion = new AuthService();

        boolean programaActivo = true;

        while (programaActivo) {

            System.out.println("\n=== INICIO DE SESION ===");

            System.out.print("Usuario: ");
            String nombreUsuario = entrada.nextLine().trim();

            System.out.print("Contraseña: ");
            String contraseña = entrada.nextLine();

            Usuario usuario = autenticacion.iniciarSesion(
                    nombreUsuario, contraseña, rutaServidor);

            if (usuario == null) {
                continue;
            }

            sesion.iniciarSesion(usuario);

            System.out.println("\nBienvenido, "
                    + usuario.getNombreUsuario());

            System.out.println("Cuenta: "
                    + (usuario.isPremium() ? "Premium" : "Estandar"));

            int opcion = -1;

            while (sesion.estaActiva() && programaActivo) {

                System.out.println("\n=== MENU PRINCIPAL ===");
                System.out.println("1. Explorar catalogo");

                if (usuario.isPremium()) {
                    System.out.println("2. Lectura local");
                }

                System.out.println("3. Cerrar sesion");
                System.out.println("4. Salir");
                System.out.print("Opcion: ");

                try {
                    opcion = Integer.parseInt(
                            entrada.nextLine().trim());
                } catch (NumberFormatException error) {
                    System.out.println("Ingrese un numero valido.");
                    continue;
                }

                switch (opcion) {

                    case 1:
                        if (catalogo.cargar(rutaServidor)) {
                            explorarCatalogo(catalogo);
                        }
                        break;

                    case 2:
                    if (usuario.isPremium()) {
                        mostrarLecturaLocal();
                    } else {
                        System.out.println("Opcion no disponible.");
                    }
                    break;

                    case 3:
                        sesion.cerrarSesion();
                        System.out.println("Sesion cerrada.");
                        break;

                    case 4:
                        sesion.cerrarSesion();
                        programaActivo = false;
                        System.out.println("Hasta pronto.");
                        break;

                    default:
                        System.out.println("Opcion no valida.");
                }
            }
        }
    }

    private void explorarCatalogo(CatalogoService catalogo) {

        while (true) {

            catalogo.mostrarLibros();

            System.out.println("\n0. Regresar");
            System.out.print("Seleccione un libro: ");

            int opcion;

            try {
                opcion = Integer.parseInt(
                        entrada.nextLine().trim());
            } catch (NumberFormatException error) {
                System.out.println("Ingrese un numero valido.");
                continue;
            }

            if (opcion == 0) {
                return;
            }
            
            Libro libro = catalogo.obtenerLibro(opcion - 1);
            
            // Verificar que el libro exista
            if (libro == null) {
                System.out.println("Opcion invalida. Seleccione un libro del catalogo.");
                continue;
            }

            // Mostrar el libro seleccionado
            System.out.println("\nLibro seleccionado:");
            System.out.println("ISBN: " + libro.getIsbn());
            System.out.println("Titulo: " + libro.getTitulo());

            // Transferir solamente si el libro existe
            String rutaLibro = libroService.obtenerLibroCifrado(
                    libro, rutaServidor);

            if (rutaLibro == null) {
                System.out.println("No fue posible obtener el libro.");
                continue;
            }
            
            System.out.println("Copia cifrada guardada en: " + rutaLibro);

            
            if (sesion.getUsuarioActual().isPremium()) {

                System.out.println("\n1. Leer en linea");
                System.out.println("2. Descargar");
                System.out.print("Opcion: ");

                String opcionLibro = entrada.nextLine().trim();

                switch (opcionLibro) {

                    case "1":
                        libroService.leerLibro(rutaLibro);
                        break;

                    case "2":
                        libroService.descargarLibro(libro, rutaLibro);
                        break;

                    default:
                        System.out.println("Opcion no valida.");
                }

            } else {

                libroService.leerLibro(rutaLibro);
            }
        }
    }
    
    private void mostrarLecturaLocal() {

        File[] librosDescargados = libroService.obtenerLibrosDescargados();

        if (librosDescargados.length == 0) {
            System.out.println("No hay libros descargados.");
            return;
        }

        while (true) {

            System.out.println("\n=== LIBROS DESCARGADOS ===");

            for (int i = 0; i < librosDescargados.length; i++) {
                System.out.println((i + 1) + ". "
                        + librosDescargados[i].getName());
            }

            System.out.println("0. Regresar");
            System.out.print("Seleccione un libro: ");

            int opcion;

            try {
                opcion = Integer.parseInt(entrada.nextLine().trim());
            } catch (NumberFormatException error) {
                System.out.println("Ingrese un numero valido.");
                continue;
            }

            if (opcion == 0) {
                return;
            }

            if (opcion < 1 || opcion > librosDescargados.length) {
                System.out.println("Opcion invalida.");
                continue;
            }

            File archivo = librosDescargados[opcion - 1];

            String nombreArchivo = archivo.getName();
            String isbn = nombreArchivo.substring(
                    0, nombreArchivo.length() - 4);

            Libro libro = new Libro(isbn, isbn);

            libroService.leerLibroLocal(libro);
        }
    }

}

