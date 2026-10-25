package crypto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;

public class ClienteDH {

    public BigInteger conectar(String ipServidor) {

        try (Socket socket =
                new Socket(ipServidor, 5050)) {

            socket.setSoTimeout(10000);

            DataInputStream entrada =
                    new DataInputStream(socket.getInputStream());

            DataOutputStream salida =
                    new DataOutputStream(socket.getOutputStream());

            DiffieHellman dh = new DiffieHellman();

            // Generar claves del cliente.
            BigInteger privadoCliente = dh.generarPrivado();

            BigInteger publicoCliente =
                    dh.calcularPublico(privadoCliente);

            // Recibir clave publica del servidor.
            BigInteger publicoServidor =
                    new BigInteger(entrada.readUTF());

            // Enviar clave publica del cliente.
            salida.writeUTF(publicoCliente.toString());
            salida.flush();

            // Calcular secreto compartido.
            BigInteger secretoCliente =
                    dh.calcularSecreto(
                            publicoServidor, privadoCliente);

            System.out.println(
                    "Intercambio DH completado.");

            System.out.println(
                    "Secreto calculado: " + secretoCliente);

            return secretoCliente;

        } catch (IOException | IllegalArgumentException error) {

            System.out.println(
                    "Error en intercambio DH: "
                    + error.getMessage());

            return null;
        }
    }
}

