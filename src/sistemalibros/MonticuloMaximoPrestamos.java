package sistemalibros;

/**
 * Montículo Máximo (Max-Heap) adaptado del ejercicio base
 * (MonticuloMaximo.java) para mantener los libros ordenados por
 * CANTIDAD PRESTADA (el libro con más préstamos siempre queda en la raíz).
 *
 * A diferencia del ejercicio original (que usaba un árbol enlazado con
 * punteros padre/izquierdo/derecho), aquí se usa la representación
 * clásica de montículo en un ARREGLO: para un nodo en la posición i,
 * su hijo izquierdo está en (2i+1), su hijo derecho en (2i+2) y su padre
 * en ((i-1)/2). Esta representación es la que permite localizar y
 * reordenar eficientemente un libro específico cuando cambia su cantidad
 * prestada (operación exigida por el flujo de prestamos.txt), algo mucho
 * más costoso de hacer sobre un árbol enlazado por punteros.
 *
 * Al igual que en el resto del sistema, este montículo NO copia los
 * libros: guarda la MISMA referencia de objeto Libro usada por el Árbol
 * B+ y la Tabla Hash, por lo que solo necesita reordenar su posición
 * (flotar/hundir) cuando el valor de cantidadPrestada cambia.
 */
public class MonticuloMaximoPrestamos {

    private Libro[] datos;
    private int tamano;
    private static final int CAPACIDAD_INICIAL = 16;

    public MonticuloMaximoPrestamos() {
        datos = new Libro[CAPACIDAD_INICIAL];
        tamano = 0;
    }

    /** Duplica la capacidad del arreglo interno cuando se llena (arreglo dinámico manual). */
    private void asegurarCapacidad() {
        if (tamano == datos.length) {
            Libro[] nuevo = new Libro[datos.length * 2];
            System.arraycopy(datos, 0, nuevo, 0, datos.length);
            datos = nuevo;
        }
    }

    /** Inserta un libro al final del arreglo y lo hace "flotar" hasta su posición correcta. */
    public void insertar(Libro libro) {
        asegurarCapacidad();
        datos[tamano] = libro;
        int posicion = tamano;
        tamano++;
        flotar(posicion);
    }

    /**
     * Intercambia el elemento en 'i' con su padre mientras su cantidad
     * prestada sea mayor que la de este. Devuelve la posición final donde
     * quedó el elemento tras "flotar" (burbujeo hacia arriba).
     */
    private int flotar(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (datos[i].getCantidadPrestada() > datos[padre].getCantidadPrestada()) {
                intercambiar(i, padre);
                i = padre;
            } else {
                break;
            }
        }
        return i;
    }

    /** Hunde el elemento en 'i' comparándolo con sus hijos hasta restaurar la propiedad de montículo. */
    private void hundir(int i) {
        while (true) {
            int izq = 2 * i + 1;
            int der = 2 * i + 2;
            int mayor = i;

            if (izq < tamano && datos[izq].getCantidadPrestada() > datos[mayor].getCantidadPrestada()) {
                mayor = izq;
            }
            if (der < tamano && datos[der].getCantidadPrestada() > datos[mayor].getCantidadPrestada()) {
                mayor = der;
            }
            if (mayor == i) break;

            intercambiar(i, mayor);
            i = mayor;
        }
    }

    private void intercambiar(int a, int b) {
        Libro temp = datos[a];
        datos[a] = datos[b];
        datos[b] = temp;
    }

    /** Búsqueda lineal de la posición actual de un libro dentro del montículo, por ISBN. */
    private int buscarIndice(String isbn) {
        for (int i = 0; i < tamano; i++) {
            if (datos[i].getIsbn().equals(isbn)) return i;
        }
        return -1;
    }

    /**
     * Reubica un libro dentro del montículo después de que su cantidad
     * prestada cambió externamente (por ejemplo al procesar
     * prestamos.txt). Se intenta primero "flotar" (por si el valor
     * aumentó) y luego "hundir" desde la posición resultante (por si el
     * valor disminuyó); solo una de las dos operaciones tendrá efecto real.
     */
    public void reordenar(String isbn) {
        int idx = buscarIndice(isbn);
        if (idx == -1) return;
        idx = flotar(idx);
        hundir(idx);
    }

    /** Devuelve, sin eliminar, el libro con mayor cantidad de préstamos (raíz). */
    public Libro obtenerMaximo() {
        return (tamano == 0) ? null : datos[0];
    }

    /** Imprime el montículo nivel por nivel (recorrido por niveles sobre el arreglo). */
    public void imprimir() {
        System.out.println("========== MONTÍCULO MÁXIMO (Préstamos) ==========");
        if (tamano == 0) {
            System.out.println("(vacío)");
        } else {
            int nivel = 0;
            int cantidadEnNivel = 1;
            int i = 0;
            while (i < tamano) {
                StringBuilder sb = new StringBuilder("Nivel " + nivel + ": ");
                for (int j = 0; j < cantidadEnNivel && i < tamano; j++, i++) {
                    sb.append("[").append(datos[i].getIsbn())
                      .append(" | Prestados: ").append(datos[i].getCantidadPrestada())
                      .append("] ");
                }
                System.out.println(sb.toString());
                nivel++;
                cantidadEnNivel *= 2;
            }
        }
        System.out.println("====================================================");
    }
}