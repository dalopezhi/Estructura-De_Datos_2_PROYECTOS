package compression;

public class ColaPrioridad {

    private NodoHuffman[] nodos = new NodoHuffman[65536];
    private int cantidad = 0;

    public void insertar(NodoHuffman nuevo) {

        if (cantidad >= nodos.length) {
            throw new IllegalStateException("Cola llena.");
        }

        int posicion = cantidad;

        while (posicion > 0 &&
                nodos[posicion - 1].frecuencia > nuevo.frecuencia) {

            nodos[posicion] = nodos[posicion - 1];
            posicion--;
        }

        nodos[posicion] = nuevo;
        cantidad++;
    }

    public NodoHuffman extraerMenor() {

        if (cantidad == 0) {
            return null;
        }

        NodoHuffman menor = nodos[0];

        for (int i = 0; i < cantidad - 1; i++) {
            nodos[i] = nodos[i + 1];
        }

        cantidad--;
        nodos[cantidad] = null;

        return menor;
    }

    public int getCantidad() {
        return cantidad;
    }
}
