package compression;

public class NodoHuffman {

    public char caracter;
    public int frecuencia;
    public NodoHuffman izquierda;
    public NodoHuffman derecha;

    public NodoHuffman(char caracter, int frecuencia) {
        this.caracter = caracter;
        this.frecuencia = frecuencia;
    }

    public boolean esHoja() {
        return izquierda == null && derecha == null;
    }
}
