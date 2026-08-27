package sistemalibros;

/**
 * Árbol B+ adaptado del ejercicio base (ArbolBMasClase.java) para almacenar
 * registros completos de Libro, usando el ISBN (String) como clave de
 * ordenamiento. Implementado completamente con arreglos fijos y nodos
 * enlazados (sin ArrayList ni Collections), según lo exige el proyecto.
 *
 * Reglas del árbol (grado = 2):
 *  - Toda clave "estable" en un nodo cumple: máximo 3 claves, máximo 4 hijos.
 *  - Solo las HOJAS almacenan el objeto Libro completo; los nodos internos
 *    solo replican claves como índices de enrutamiento.
 *  - Las hojas están enlazadas entre sí (nodo.siguiente) para permitir
 *    recorridos secuenciales típicos de un Árbol B+.
 *  - Ignora la reinserción de un ISBN que ya existe (esto se controla
 *    principalmente desde GestorArchivos, que consulta la Tabla Hash antes
 *    de decidir si inserta o actualiza).
 */
public class ArbolBMasLibros {

    private NodoArbol raiz;
    private final int grado = NodoArbol.GRADO;

    public ArbolBMasLibros() {
        raiz = new NodoArbol(true);
    }

    // =====================================================================
    // BÚSQUEDA
    // =====================================================================

    /** Busca un libro por ISBN recorriendo el árbol desde la raíz. */
    public Libro buscar(String isbn) {
        return buscarEnNodo(raiz, isbn);
    }

    private Libro buscarEnNodo(NodoArbol nodo, String isbn) {
        if (nodo.esHoja) {
            // En la hoja se hace comparación exacta (no solo de rango)
            for (int i = 0; i < nodo.numClaves; i++) {
                if (nodo.claves[i].equals(isbn)) {
                    return nodo.libros[i];
                }
            }
            return null;
        } else {
            // Se determina el subárbol (hijo) donde podría estar la clave:
            // se avanza mientras la clave buscada sea >= a la clave índice.
            int i = 0;
            while (i < nodo.numClaves && isbn.compareTo(nodo.claves[i]) >= 0) {
                i++;
            }
            return buscarEnNodo(nodo.hijos[i], isbn);
        }
    }

    // =====================================================================
    // INSERCIÓN
    // =====================================================================

    /**
     * Inserta un nuevo libro en el árbol. Si el ISBN ya existe, la
     * inserción se ignora (la actualización de datos existentes se maneja
     * en la capa de negocio / GestorArchivos, que ya comparte la misma
     * referencia de objeto Libro con la Tabla Hash).
     */
    public void insertar(Libro libro) {
        if (buscar(libro.getIsbn()) != null) {
            return; // Ya existe: no se duplica
        }

        // División preventiva de la raíz si ya está llena (3 claves)
        if (raiz.numClaves == NodoArbol.MAX_CLAVES_ESTABLE) {
            NodoArbol nuevaRaiz = new NodoArbol(false);
            nuevaRaiz.hijos[0] = raiz;
            nuevaRaiz.numHijos = 1;
            dividirHijo(nuevaRaiz, 0);
            raiz = nuevaRaiz;
        }
        insertarNoLleno(raiz, libro);
    }

    /**
     * Inserta 'libro' en el subárbol de 'nodo', asumiendo que 'nodo' NO
     * está lleno (invariante garantizada por las divisiones preventivas
     * realizadas antes de descender).
     */
    private void insertarNoLleno(NodoArbol nodo, Libro libro) {
        String isbn = libro.getIsbn();

        if (nodo.esHoja) {
            // Se busca, de derecha a izquierda, la posición ordenada donde
            // debe insertarse la nueva clave dentro del arreglo de la hoja.
            int i = nodo.numClaves - 1;
            while (i >= 0 && isbn.compareTo(nodo.claves[i]) < 0) {
                i--;
            }
            insertarEnHoja(nodo, i + 1, isbn, libro);
        } else {
            // Se determina el hijo por el que se debe descender
            int i = 0;
            while (i < nodo.numClaves && isbn.compareTo(nodo.claves[i]) >= 0) {
                i++;
            }
            NodoArbol hijo = nodo.hijos[i];

            // Si el hijo está lleno, se divide ANTES de descender (preventivo)
            if (hijo.numClaves == NodoArbol.MAX_CLAVES_ESTABLE) {
                dividirHijo(nodo, i);
                // Tras la división, puede que la clave deba ir al nuevo hermano
                if (isbn.compareTo(nodo.claves[i]) >= 0) {
                    i++;
                }
            }
            insertarNoLleno(nodo.hijos[i], libro);
        }
    }

    /**
     * Divide el hijo lleno padre.hijos[indice] en dos nodos, promoviendo
     * una clave separadora hacia 'padre'. Es el corazón del balanceo del
     * Árbol B+: garantiza que ningún nodo exceda su capacidad máxima.
     *
     * Caso HOJA: las claves [medio..fin] pasan al nuevo nodo; la clave
     * separadora que sube al padre es una COPIA de la primera clave del
     * nuevo nodo (característica propia de B+: las hojas conservan todos
     * los datos, los índices internos son solo copias de enrutamiento).
     * Además se mantiene la lista enlazada entre hojas.
     *
     * Caso INTERNO: la clave del medio SUBE al padre (no se duplica) y
     * los hijos se reparten entre el nodo original y el nuevo.
     */
    private void dividirHijo(NodoArbol padre, int indice) {
        NodoArbol hijo = padre.hijos[indice];
        int medio = grado - 1; // Para grado = 2 -> medio = 1
        NodoArbol nuevo = new NodoArbol(hijo.esHoja);

        if (hijo.esHoja) {
            // Copiar claves/libros desde 'medio' hasta el final al nuevo nodo
            int cont = 0;
            for (int i = medio; i < hijo.numClaves; i++) {
                nuevo.claves[cont] = hijo.claves[i];
                nuevo.libros[cont] = hijo.libros[i];
                cont++;
            }
            nuevo.numClaves = cont;
            hijo.numClaves = medio; // El hijo original conserva solo [0, medio)

            // Mantener la lista enlazada de hojas (propiedad clave de B+)
            nuevo.siguiente = hijo.siguiente;
            hijo.siguiente = nuevo;

            String claveSeparadora = nuevo.claves[0];
            insertarClaveIndice(padre, indice, claveSeparadora);
            insertarHijoEnPosicion(padre, indice + 1, nuevo);

        } else {
            String claveSeparadora = hijo.claves[medio];

            // Copiar claves posteriores al medio al nuevo nodo
            int cont = 0;
            for (int i = medio + 1; i < hijo.numClaves; i++) {
                nuevo.claves[cont] = hijo.claves[i];
                cont++;
            }
            nuevo.numClaves = cont;

            // Copiar los hijos correspondientes al nuevo nodo
            int contHijos = 0;
            for (int i = medio + 1; i < hijo.numHijos; i++) {
                nuevo.hijos[contHijos] = hijo.hijos[i];
                contHijos++;
            }
            nuevo.numHijos = contHijos;

            hijo.numClaves = medio;
            hijo.numHijos = medio + 1;

            insertarClaveIndice(padre, indice, claveSeparadora);
            insertarHijoEnPosicion(padre, indice + 1, nuevo);
        }
    }

    // =====================================================================
    // UTILIDADES DE MANIPULACIÓN DE ARREGLOS (reemplazan a ArrayList)
    // =====================================================================

    /** Inserta una clave (con su libro) en la posición 'pos' de una HOJA,
     *  recorriendo los elementos posteriores una posición a la derecha. */
    private void insertarEnHoja(NodoArbol nodo, int pos, String clave, Libro libro) {
        for (int j = nodo.numClaves; j > pos; j--) {
            nodo.claves[j] = nodo.claves[j - 1];
            nodo.libros[j] = nodo.libros[j - 1];
        }
        nodo.claves[pos] = clave;
        nodo.libros[pos] = libro;
        nodo.numClaves++;
    }

    /** Inserta una clave índice (sin libro asociado) en un nodo INTERNO. */
    private void insertarClaveIndice(NodoArbol nodo, int pos, String clave) {
        for (int j = nodo.numClaves; j > pos; j--) {
            nodo.claves[j] = nodo.claves[j - 1];
        }
        nodo.claves[pos] = clave;
        nodo.numClaves++;
    }

    /** Inserta un puntero a hijo en la posición 'pos', desplazando el resto. */
    private void insertarHijoEnPosicion(NodoArbol nodo, int pos, NodoArbol hijo) {
        for (int j = nodo.numHijos; j > pos; j--) {
            nodo.hijos[j] = nodo.hijos[j - 1];
        }
        nodo.hijos[pos] = hijo;
        nodo.numHijos++;
    }

    // =====================================================================
    // VISUALIZACIÓN
    // =====================================================================

    /** Imprime el árbol completo, nivel por nivel, indicando hojas. */
    public void imprimir() {
        System.out.println("========== ÁRBOL B+ (por niveles) ==========");
        imprimirNodo(raiz, 0);
        System.out.println("==============================================");
    }

    private void imprimirNodo(NodoArbol nodo, int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append("Nivel ").append(nivel).append(": [");
        for (int i = 0; i < nodo.numClaves; i++) {
            sb.append(nodo.claves[i]);
            if (i < nodo.numClaves - 1) sb.append(", ");
        }
        sb.append("]");
        if (nodo.esHoja) sb.append(" [HOJA]");
        System.out.println(sb.toString());

        if (!nodo.esHoja) {
            for (int i = 0; i < nodo.numHijos; i++) {
                imprimirNodo(nodo.hijos[i], nivel + 1);
            }
        }
    }

    /** Recorre las hojas de izquierda a derecha usando la lista enlazada,
     *  imprimiendo todos los libros en orden ascendente de ISBN. */
    public void imprimirSecuencial() {
        NodoArbol hoja = raiz;
        while (!hoja.esHoja) {
            hoja = hoja.hijos[0];
        }
        System.out.println("===== RECORRIDO SECUENCIAL DE HOJAS (B+) =====");
        while (hoja != null) {
            for (int i = 0; i < hoja.numClaves; i++) {
                System.out.println(hoja.libros[i]);
            }
            hoja = hoja.siguiente;
        }
        System.out.println("===============================================");
    }
}