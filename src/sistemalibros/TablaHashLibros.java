package sistemalibros;

/**
 * Tabla Hash adaptada del ejercicio base (TablaHash.java) para almacenar
 * libros usando el ISBN (String) como clave. A diferencia del ejercicio
 * original (que usaba direccionamiento abierto sobre un arreglo de
 * Producto), este sistema exige resolución de colisiones mediante
 * ENCADENAMIENTO SEPARADO (Separate Chaining): cada posición del arreglo
 * es la cabeza de una lista enlazada (NodoHash) de libros que colisionaron
 * en ese índice.
 */
public class TablaHashLibros {

    private static final int TAMANO = 13; // Número primo para reducir colisiones
    private NodoHash[] tabla;

    public TablaHashLibros() {
        tabla = new NodoHash[TAMANO];
    }

    /**
     * Función hash para claves de tipo String (ISBN). Se calcula un
     * "polynomial rolling hash" (base 31, técnica estándar para strings)
     * y se reduce al rango [0, TAMANO) con módulo.
     */
    private int hash(String isbn) {
        int suma = 0;
        for (int i = 0; i < isbn.length(); i++) {
            suma = suma * 31 + isbn.charAt(i);
        }
        return Math.abs(suma) % TAMANO;
    }

    /**
     * Inserta un libro en la tabla. Si el ISBN ya existe, no se duplica
     * (la actualización de datos se maneja en la capa de negocio, ya que
     * árbol/hash/montículos comparten la misma referencia del objeto Libro).
     * En caso de colisión, el nuevo nodo se inserta al INICIO de la cadena
     * del bucket correspondiente (inserción O(1)).
     */
    public void insertar(Libro libro) {
        if (buscar(libro.getIsbn()) != null) {
            return;
        }
        int indice = hash(libro.getIsbn());
        NodoHash nuevo = new NodoHash(libro);
        nuevo.siguiente = tabla[indice];
        tabla[indice] = nuevo;
    }

    /** Busca un libro por ISBN recorriendo la cadena del bucket correspondiente. */
    public Libro buscar(String isbn) {
        int indice = hash(isbn);
        NodoHash actual = tabla[indice];
        while (actual != null) {
            if (actual.libro.getIsbn().equals(isbn)) {
                return actual.libro;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    /** Imprime cada bucket de la tabla junto con su cadena de colisiones. */
    public void mostrarTabla() {
        System.out.println("=========== TABLA HASH (Encadenamiento) ===========");
        for (int i = 0; i < TAMANO; i++) {
            System.out.print("[" + i + "] ");
            NodoHash actual = tabla[i];
            if (actual == null) {
                System.out.println("VACÍA");
                continue;
            }
            StringBuilder cadena = new StringBuilder();
            while (actual != null) {
                cadena.append(actual.libro.getIsbn());
                cadena.append(" (").append(actual.libro.getTitulo()).append(")");
                if (actual.siguiente != null) cadena.append(" -> ");
                actual = actual.siguiente;
            }
            System.out.println(cadena.toString());
        }
        System.out.println("=====================================================");
    }
}