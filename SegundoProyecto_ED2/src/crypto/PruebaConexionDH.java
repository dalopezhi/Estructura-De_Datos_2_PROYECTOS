package crypto;

import java.math.BigInteger;

public class PruebaConexionDH {

    public static void main(String[] args) {

        ClienteDH cliente = new ClienteDH();

        BigInteger secreto = cliente.conectar();

        if (secreto != null) {

            System.out.println(
                    "Conexion e intercambio exitosos.");

        } else {

            System.out.println(
                    "No se completo el intercambio.");
        }
    }
}

