
package servidor;

import crypto.CanalCifrado;
import crypto.DiffieHellmanSeguro;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServidorDH {

    private static final int PUERTO = 5050;

    public static void main(String[] args) {

        System.out.println(
                "=== SERVIDOR DH + TRIPLE DES ===");

        try (ServerSocket servidor =
                new ServerSocket(PUERTO)) {

            System.out.println(
                    "Escuchando en puerto " + PUERTO);

            while (true) {

                try (Socket socket =
                        servidor.accept()) {

                    atenderCliente(socket);

                } catch (Exception error) {

                    System.out.println(
                            "Error: " + error.getMessage());
                }
            }

        } catch (Exception error) {

            System.out.println(
                    "Error del servidor: "
                    + error.getMessage());
        }
    }

    private static void atenderCliente(Socket socket)
            throws Exception {

        socket.setSoTimeout(10000);

        DataInputStream entrada =
                new DataInputStream(
                        socket.getInputStream());

        DataOutputStream salida =
                new DataOutputStream(
                        socket.getOutputStream());

        System.out.println(
                "\nCliente conectado: "
                + socket.getInetAddress());

        // Recibir clave publica del cliente.
        int longitud = entrada.readInt();

        if (longitud <= 0 || longitud > 8192) {
            throw new IllegalArgumentException(
                    "Longitud de clave invalida.");
        }

        byte[] datosCliente =
                new byte[longitud];

        entrada.readFully(datosCliente);

        PublicKey publicaCliente =
                DiffieHellmanSeguro.reconstruirPublica(
                        datosCliente);

        // Generar claves compatibles.
        KeyPair clavesServidor =
                DiffieHellmanSeguro.generarClaves(
                        publicaCliente);

        byte[] publicaServidor =
                clavesServidor.getPublic().getEncoded();

        salida.writeInt(publicaServidor.length);
        salida.write(publicaServidor);
        salida.flush();

        // Obtener secreto compartido.
        byte[] secreto =
                DiffieHellmanSeguro.calcularSecreto(
                        clavesServidor,
                        publicaCliente);

        CanalCifrado canal =
                new CanalCifrado(secreto);

        // Recibir y descifrar mensaje.
        String mensajeCifrado =
                entrada.readUTF();

        String mensajeOriginal =
                canal.descifrar(mensajeCifrado);

        System.out.println(
        "Solicitud recibida: " + mensajeOriginal);

        String textoRespuesta;

        if (mensajeOriginal.equals("ESTADO")) {

            Path rutaCatalogo = Paths.get(
                    "C:\\BibliotecaServidor\\catalogo.txt");

            File archivoCatalogo = rutaCatalogo.toFile();

            if (archivoCatalogo.isFile()) {

                long registros = Files.lines(rutaCatalogo)
                        .filter(linea -> !linea.trim().isEmpty())
                        .count();

                textoRespuesta =
                        "Servidor activo. Registros del catalogo: "
                        + registros;

            } else {

                textoRespuesta =
                        "Servidor activo, pero no se encontro el catalogo.";
            }

        } else {

            textoRespuesta = "Solicitud no reconocida.";
        }

        // Enviar respuesta cifrada.
        String respuesta = canal.cifrar(textoRespuesta);

        salida.writeUTF(respuesta);
        salida.flush();

        System.out.println(
                "Respuesta cifrada enviada al cliente.");
    }
}
