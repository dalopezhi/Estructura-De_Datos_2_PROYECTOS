package sistemalibros;

/**
 * Nodo del Árbol B+ implementado con arreglos fijos.
 *
 * Para un árbol B+ de GRADO = 2 (grado mínimo), cada nodo puede almacenar
 * como máximo (2*GRADO - 1) = 3 claves "estables" y (2*GRADO) = 4 hijos.
 * Sin embargo, durante el algoritmo de inserción por división preventiva
 * un nodo puede recibir TEMPORALMENTE una clave/hijo
 * adicional antes de ser dividido; por eso los arreglos se reservan con
 * un espacio extra de "desborde" (overflow).
 *
 * - claves[]: ISBNs almacenados en el nodo, siempre ordenados ascendentemente.
 * - libros[]: SOLO se usa cuando esHoja == true. libros[i] es el registro
 *   completo del Libro asociado a claves[i]. Los nodos internos no
 *   almacenan libros, solo actúan como índice de enrutamiento.
 * - hijos[]: referencias a los nodos hijos (solo para nodos internos).
 * - siguiente: enlace a la hoja siguiente, formando la lista enlazada
 *   característica del Árbol B+ que permite recorridos secuenciales.
 */
public class NodoArbol {

    /** Grado mínimo del árbol B+ (fijo para este sistema). */
    public static final int GRADO = 2;

    /** Máximo número de claves "estables" antes de requerir división. */
    public static final int MAX_CLAVES_ESTABLE = 2 * GRADO - 1; // = 3

    /** Tamaño físico del arreglo de claves (incluye 1 espacio de desborde). */
    public static final int CAPACIDAD_CLAVES = MAX_CLAVES_ESTABLE + 1; // = 4

    /** Tamaño físico del arreglo de hijos (incluye espacio de desborde). */
    public static final int CAPACIDAD_HIJOS = CAPACIDAD_CLAVES + 1; // = 5

    public boolean esHoja;
    public String[] claves;
    public Libro[] libros;
    public NodoArbol[] hijos;
    public int numClaves;
    public int numHijos;
    public NodoArbol siguiente;

    public NodoArbol(boolean esHoja) {
        this.esHoja = esHoja;
        this.claves = new String[CAPACIDAD_CLAVES];
        this.hijos = new NodoArbol[CAPACIDAD_HIJOS];
        this.libros = esHoja ? new Libro[CAPACIDAD_CLAVES] : null;
        this.numClaves = 0;
        this.numHijos = 0;
        this.siguiente = null;
    }
}
