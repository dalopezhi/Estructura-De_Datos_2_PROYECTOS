package crypto;

import java.math.BigInteger;
import java.util.Scanner;

public class PruebaConexionDH {

    public static void main(String[] args) {

        Scanner entrada = new Scanner(System.in);

        System.out.print("IP del servidor: ");
        String ip = entrada.nextLine().trim();

        ClienteDH cliente = new ClienteDH();

        BigInteger secreto = cliente.conectar(ip);

        if (secreto != null) {

            System.out.println(
                    "Conexion e intercambio exitosos.");

        } else {

            System.out.println(
                    "No se completo el intercambio.");
        }
    }
}
