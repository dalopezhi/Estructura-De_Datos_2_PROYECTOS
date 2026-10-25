package crypto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import storage.Configuracion;

public class ClienteDH {

    private static final int PUERTO = 5050;
    private static final int TIMEOUT = 10000;

    public boolean conectar() {

        Configuracion configuracion =
                new Configuracion();

        if (!configuracion.cargar()) {
            return false;
        }

        String servidor = extraerServidor(
                configuracion.getRutaServidor());

        if (servidor == null) {

            System.out.println(
                    "Ruta del servidor invalida.");

            return false;
        }

        return conectar(servidor);
    }

    public boolean conectar(String servidor) {

        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(
                            servidor, PUERTO),
                    TIMEOUT);

            socket.setSoTimeout(TIMEOUT);

            DataInputStream entrada =
                    new DataInputStream(
                            socket.getInputStream());

            DataOutputStream salida =
                    new DataOutputStream(
                            socket.getOutputStream());

            // Generar claves DH del cliente.
            KeyPair clavesCliente =
                    DiffieHellmanSeguro.generarClaves();

            byte[] publicaCliente =
                    clavesCliente.getPublic().getEncoded();

            salida.writeInt(publicaCliente.length);
            salida.write(publicaCliente);
            salida.flush();

            // Recibir clave publica del servidor.
            int longitud = entrada.readInt();

            if (longitud <= 0 || longitud > 8192) {
                throw new IllegalArgumentException(
                        "Longitud de clave invalida.");
            }

            byte[] datosServidor =
                    new byte[longitud];

            entrada.readFully(datosServidor);

            PublicKey publicaServidor =
                    DiffieHellmanSeguro.reconstruirPublica(
                            datosServidor);

            // Calcular secreto compartido.
            byte[] secreto =
                    DiffieHellmanSeguro.calcularSecreto(
                            clavesCliente,
                            publicaServidor);

            CanalCifrado canal =
                    new CanalCifrado(secreto);

            // Enviar mensaje cifrado.
            String mensaje =
                    "Hola servidor, soy Biblioteca Digital.";

            String cifrado =
                    canal.cifrar(mensaje);

            salida.writeUTF(cifrado);
            salida.flush();

            System.out.println(
                    "Mensaje enviado cifrado.");

            // Recibir respuesta cifrada.
            String respuestaCifrada =
                    entrada.readUTF();

            String respuesta =
                    canal.descifrar(
                            respuestaCifrada);

            System.out.println(
                    "Respuesta del servidor: "
                    + respuesta);

            return true;

        } catch (Exception error) {

            System.out.println(
                    "Error de conexion cifrada: "
                    + error.getMessage());

            return false;
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

        String servidor =
                restante.substring(0, separador).trim();

        return servidor.isEmpty()
                ? null : servidor;
    }
}
