package crypto;

import java.math.BigInteger;

public class DiffieHellman {

    private final BigInteger primo = BigInteger.valueOf(23);
    private final BigInteger generador = BigInteger.valueOf(5);

    private final BigInteger privadoCliente = BigInteger.valueOf(6);
    private final BigInteger privadoServidor = BigInteger.valueOf(15);

    public BigInteger obtenerClaveCompartida() {

        // Cada participante calcula su valor publico
        BigInteger publicoCliente =
                generador.modPow(privadoCliente, primo);

        BigInteger publicoServidor =
                generador.modPow(privadoServidor, primo);

        // Ambos calculan el mismo secreto
        BigInteger secretoCliente =
                publicoServidor.modPow(privadoCliente, primo);

        BigInteger secretoServidor =
                publicoCliente.modPow(privadoServidor, primo);

        if (!secretoCliente.equals(secretoServidor)) {
            throw new IllegalStateException(
                    "No coinciden las claves compartidas.");
        }

        return secretoCliente;
    }
}
