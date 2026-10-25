
package crypto;

import java.math.BigInteger;

public class PruebaDiffieHellman {

    public static void main(String[] args) {

        DiffieHellman dh = new DiffieHellman();

        // Simular dos participantes independientes.
        BigInteger privadoCliente = dh.generarPrivado();
        BigInteger privadoServidor = dh.generarPrivado();

        BigInteger publicoCliente =
                dh.calcularPublico(privadoCliente);

        BigInteger publicoServidor =
                dh.calcularPublico(privadoServidor);

        // El cliente recibe el publico del servidor.
        BigInteger secretoCliente = dh.calcularSecreto(
                publicoServidor, privadoCliente);

        // El servidor recibe el publico del cliente.
        BigInteger secretoServidor = dh.calcularSecreto(
                publicoCliente, privadoServidor);

        System.out.println("Publico cliente: "
                + publicoCliente);

        System.out.println("Publico servidor: "
                + publicoServidor);

        System.out.println("Secreto cliente: "
                + secretoCliente);

        System.out.println("Secreto servidor: "
                + secretoServidor);

        if (secretoCliente.equals(secretoServidor)) {

            System.out.println(
                    "Intercambio Diffie-Hellman correcto.");

        } else {

            System.out.println(
                    "Error: los secretos no coinciden.");
        }
    }
}
