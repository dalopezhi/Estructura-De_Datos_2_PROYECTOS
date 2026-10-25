package crypto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;

import storage.Configuracion;

public class ClienteDH {

    private static final int PUERTO = 5050;
    private static final int TIMEOUT = 5000;

    public BigInteger conectar() {

        Configuracion configuracion = new Configuracion();

        if (!configuracion.cargar()) {
            System.out.println(
                    "No se pudo cargar la configuracion.");
            return null;
        }

        String ruta = configuracion.getRutaServidor();

        String servidor = extraerServidor(ruta);

        if (servidor == null) {
            System.out.println(
                    "Ruta del servidor invalida: " + ruta);
            return null;
        }

        return conectar(servidor);
    }

    // Se conserva para permitir conexiones directas
    // cuando sea necesario realizar pruebas.

    public BigInteger conectar(String servidor) {

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(servidor, PUERTO),
                    TIMEOUT);

            socket.setSoTimeout(TIMEOUT);

            DataInputStream entrada =
                    new DataInputStream(
                            socket.getInputStream());

            DataOutputStream salida =
                    new DataOutputStream(
                            socket.getOutputStream());

            DiffieHellman dh = new DiffieHellman();

            BigInteger privadoCliente =
                    dh.generarPrivado();

            BigInteger publicoCliente =
                    dh.calcularPublico(privadoCliente);

            BigInteger publicoServidor =
                    new BigInteger(entrada.readUTF());

            salida.writeUTF(publicoCliente.toString());
            salida.flush();

            BigInteger secretoCliente =
                    dh.calcularSecreto(
                            publicoServidor,
                            privadoCliente);

            System.out.println(
                    "Intercambio DH completado con "
                    + servidor);

            System.out.println(
                    "Secreto calculado: "
                    + secretoCliente);

            return secretoCliente;

        } catch (IOException
                | IllegalArgumentException error) {

            System.out.println(
                    "Error en intercambio DH: "
                    + error.getMessage());

            return null;
        }
    }

    private String extraerServidor(String ruta) {

        if (ruta == null) {
            return null;
        }

        ruta = ruta.trim().replace('/', '\\');

        if (!ruta.startsWith("\\\\")) {
            return null;
        }

        String restante = ruta.substring(2);

        int separador = restante.indexOf('\\');

        if (separador <= 0) {
            return null;
        }

        String servidor = restante.substring(
                0, separador).trim();

        return servidor.isEmpty() ? null : servidor;
    }
}


