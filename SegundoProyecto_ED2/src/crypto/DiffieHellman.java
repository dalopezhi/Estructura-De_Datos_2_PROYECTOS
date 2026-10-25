
package crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellman {

    private static final BigInteger PRIMO =
            BigInteger.valueOf(23);

    private static final BigInteger GENERADOR =
            BigInteger.valueOf(5);

    private final SecureRandom aleatorio = new SecureRandom();

    // Genera un valor privado aleatorio.
    public BigInteger generarPrivado() {

        BigInteger privado;

        do {
            privado = new BigInteger(
                    PRIMO.bitLength(), aleatorio);

        } while (privado.compareTo(BigInteger.TWO) < 0
                || privado.compareTo(
                        PRIMO.subtract(BigInteger.TWO)) > 0);

        return privado;
    }

    // Calcula el valor publico de un participante.
    public BigInteger calcularPublico(BigInteger privado) {

        return GENERADOR.modPow(privado, PRIMO);
    }

    // Calcula el secreto compartido usando
    // el valor publico recibido del otro participante.
    public BigInteger calcularSecreto(
            BigInteger publicoRecibido,
            BigInteger privadoPropio) {

        if (publicoRecibido.compareTo(BigInteger.TWO) < 0
                || publicoRecibido.compareTo(
                        PRIMO.subtract(BigInteger.TWO)) > 0) {

            throw new IllegalArgumentException(
                    "Valor publico invalido.");
        }

        return publicoRecibido.modPow(
                privadoPropio, PRIMO);
    }

    // Metodo de compatibilidad con los libros
    // cifrados anteriormente.
    public BigInteger obtenerClaveCompartida() {

        BigInteger privadoCliente = BigInteger.valueOf(6);
        BigInteger privadoServidor = BigInteger.valueOf(15);

        BigInteger publicoCliente =
                calcularPublico(privadoCliente);

        BigInteger publicoServidor =
                calcularPublico(privadoServidor);

        BigInteger secretoCliente =
                calcularSecreto(
                        publicoServidor, privadoCliente);

        BigInteger secretoServidor =
                calcularSecreto(
                        publicoCliente, privadoServidor);

        if (!secretoCliente.equals(secretoServidor)) {
            throw new IllegalStateException(
                    "Las claves compartidas no coinciden.");
        }

        return secretoCliente;
    }
}
