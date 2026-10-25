package crypto;

public class PruebaHash {

    public static void main(String[] args) {

        HashService hash = new HashService();

        System.out.println(
                hash.generarHash("123"));
    }
}
