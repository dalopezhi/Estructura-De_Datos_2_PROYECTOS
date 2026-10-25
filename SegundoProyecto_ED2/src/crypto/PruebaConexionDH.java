package crypto;

public class PruebaConexionDH {

    public static void main(String[] args) {

        ClienteDH cliente = new ClienteDH();

        boolean correcto = cliente.conectar();

        if (correcto) {

            System.out.println(
                    "Prueba DH + Triple DES exitosa.");

        } else {

            System.out.println(
                    "Prueba fallida.");
        }
    }
}

