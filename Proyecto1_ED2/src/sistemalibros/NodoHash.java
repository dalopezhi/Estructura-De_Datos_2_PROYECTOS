package sistemalibros;

/**
 * Nodo de una lista simplemente enlazada usada para resolver colisiones
 * de la Tabla Hash mediante encadenamiento separado (Separate Chaining).
 * Cada posición (bucket) de la tabla apunta al primer NodoHash de su
 * cadena; cada nodo apunta al siguiente en caso de colisión.
 */
public class NodoHash {
    public Libro libro;
    public NodoHash siguiente;

    public NodoHash(Libro libro) {
        this.libro = libro;
        this.siguiente = null;
    }
}
