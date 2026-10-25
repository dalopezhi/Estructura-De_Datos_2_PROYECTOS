package servidor;

import crypto.DiffieHellman;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorDH {

    private static final int PUERTO = 5050;

    public static void main(String[] args) {

        System.out.println("=== SERVIDOR DIFFIE-HELLMAN ===");

        try (ServerSocket servidor =
                new ServerSocket(PUERTO)) {

            System.out.println(
                    "Servidor escuchando en puerto " + PUERTO);

            while (true) {

                try (Socket cliente = servidor.accept()) {

                    System.out.println(
                            "\nCliente conectado: "
                            + cliente.getInetAddress());

                    atenderCliente(cliente);

                } catch (Exception error) {

                    System.out.println(
                            "Error con cliente: "
                            + error.getMessage());
                }
            }

        } catch (IOException error) {

            System.out.println(
                    "No se pudo iniciar el servidor: "
                    + error.getMessage());
        }
    }

    private static void atenderCliente(Socket socket)
            throws IOException {

        socket.setSoTimeout(10000);

        DataInputStream entrada =
                new DataInputStream(socket.getInputStream());

        DataOutputStream salida =
                new DataOutputStream(socket.getOutputStream());

        DiffieHellman dh = new DiffieHellman();

        BigInteger privadoServidor = dh.generarPrivado();

        BigInteger publicoServidor =
                dh.calcularPublico(privadoServidor);

        // Enviar la clave publica del servidor.
        salida.writeUTF(publicoServidor.toString());
        salida.flush();

        // Recibir la clave publica del cliente.
        BigInteger publicoCliente =
                new BigInteger(entrada.readUTF());

        // Calcular secreto compartido.
        BigInteger secretoServidor =
                dh.calcularSecreto(
                        publicoCliente, privadoServidor);

        System.out.println(
                "Intercambio DH completado.");

        System.out.println(
                "Secreto calculado: " + secretoServidor);
    }
}

