package sistemalibros;

/**
 * Montículo Mínimo (Min-Heap) que mantiene los libros ordenados por
 * CANTIDAD DISPONIBLE (el libro con menor existencia siempre queda en la
 * raíz, útil para detectar rápidamente qué títulos necesitan reabastecerse).
 *
 * Es el complemento simétrico de MonticuloMaximoPrestamos: misma
 * representación en arreglo (hijo izq = 2i+1, hijo der = 2i+2, padre =
 * (i-1)/2), pero invirtiendo las comparaciones para priorizar el valor
 * MENOR. También comparte referencias de Libro con el resto de estructuras
 * y expone reordenar(isbn) para reubicarse tras cambios en existencias.txt.
 */
public class MonticuloMinimoExistencias {

    private Libro[] datos;
    private int tamano;
    private static final int CAPACIDAD_INICIAL = 16;

    public MonticuloMinimoExistencias() {
        datos = new Libro[CAPACIDAD_INICIAL];
        tamano = 0;
    }

    private void asegurarCapacidad() {
        if (tamano == datos.length) {
            Libro[] nuevo = new Libro[datos.length * 2];
            System.arraycopy(datos, 0, nuevo, 0, datos.length);
            datos = nuevo;
        }
    }

    public void insertar(Libro libro) {
        asegurarCapacidad();
        datos[tamano] = libro;
        int posicion = tamano;
        tamano++;
        flotar(posicion);
    }

    /** Flota el elemento mientras su cantidad disponible sea MENOR que la de su padre. */
    private int flotar(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (datos[i].getCantidadDisponible() < datos[padre].getCantidadDisponible()) {
                intercambiar(i, padre);
                i = padre;
            } else {
                break;
            }
        }
        return i;
    }

    /** Hunde el elemento comparándolo con el MENOR de sus hijos. */
    private void hundir(int i) {
        while (true) {
            int izq = 2 * i + 1;
            int der = 2 * i + 2;
            int menor = i;

            if (izq < tamano && datos[izq].getCantidadDisponible() < datos[menor].getCantidadDisponible()) {
                menor = izq;
            }
            if (der < tamano && datos[der].getCantidadDisponible() < datos[menor].getCantidadDisponible()) {
                menor = der;
            }
            if (menor == i) break;

            intercambiar(i, menor);
            i = menor;
        }
    }

    private void intercambiar(int a, int b) {
        Libro temp = datos[a];
        datos[a] = datos[b];
        datos[b] = temp;
    }

    private int buscarIndice(String isbn) {
        for (int i = 0; i < tamano; i++) {
            if (datos[i].getIsbn().equals(isbn)) return i;
        }
        return -1;
    }

    /** Reubica un libro tras un cambio en su cantidad disponible (ver MonticuloMaximoPrestamos.reordenar). */
    public void reordenar(String isbn) {
        int idx = buscarIndice(isbn);
        if (idx == -1) return;
        idx = flotar(idx);
        hundir(idx);
    }

    /** Devuelve, sin eliminar, el libro con menor cantidad disponible (raíz). */
    public Libro obtenerMinimo() {
        return (tamano == 0) ? null : datos[0];
    }

    public void imprimir() {
        System.out.println("========== MONTÍCULO MÍNIMO (Existencias) ==========");
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
                      .append(" | Disponibles: ").append(datos[i].getCantidadDisponible())
                      .append("] ");
                }
                System.out.println(sb.toString());
                nivel++;
                cantidadEnNivel *= 2;
            }
        }
        System.out.println("======================================================");
    }
}