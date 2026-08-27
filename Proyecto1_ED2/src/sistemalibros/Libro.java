package sistemalibros;

/**
 * Modelo de datos que representa el registro completo de un libro
 * dentro del Sistema de Administración de Libros.
 *
 * Se almacena simultáneamente en:
 *  - El Árbol B+ (clave = ISBN)
 *  - La Tabla Hash (clave = ISBN, resolución de colisiones por encadenamiento)
 *  - El Montículo Máximo de préstamos (ordenado por cantidadPrestada)
 *  - El Montículo Mínimo de existencias (ordenado por cantidadDisponible)
 *
 * Para simplificar la sincronización entre estructuras, el
 * sistema NO crea copias del libro en cada estructura; en su lugar,
 * comparte la MISMA referencia de objeto Libro en las cuatro estructuras.
 * De esta forma, cuando se actualiza un atributo el cambio es visible automáticamente en todas las estructuras
 * que apuntan a ese mismo objeto, y solo se necesita reordenar los
 * montículos (que sí dependen del valor para su orden interno).
 */
public class Libro {

    private String isbn;
    private String titulo;
    private String autor;
    private String editorial;
    private int anio;
    private String categoria;
    private int cantidadPrestada;
    private int cantidadDisponible;
    
    /*Constructor de la clase: inicializa todos los atributos del libro*/

    public Libro(String isbn, String titulo, String autor, String editorial, int anio, String categoria, int cantidadPrestada, int cantidadDisponible) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anio = anio;
        this.categoria = categoria;
        this.cantidadPrestada = cantidadPrestada;
        this.cantidadDisponible = cantidadDisponible;
    }

    /**
     * Sobrescribe todos los atributos (excepto el ISBN, que es la clave
     * inmutable del registro) con los valores más recientes leídos del
     * archivo libros.txt. Se usa cuando el ISBN ya existía en el sistema.
     */
    public void actualizarDatos(String titulo, String autor, String editorial, int anio, String categoria, int cantidadPrestada, int cantidadDisponible) {
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anio = anio;
        this.categoria = categoria;
        this.cantidadPrestada = cantidadPrestada;
        this.cantidadDisponible = cantidadDisponible;
    }

    // ===================== Getters y Setters =====================

    public String getIsbn() { return isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getCantidadPrestada() { return cantidadPrestada; }
    public void setCantidadPrestada(int cantidadPrestada) { this.cantidadPrestada = cantidadPrestada; }

    public int getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(int cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }

    @Override
    public String toString() {
        return "ISBN: " + isbn +
               " | Título: " + titulo +
               " | Autor: " + autor +
               " | Editorial: " + editorial +
               " | Año: " + anio +
               " | Categoría: " + categoria +
               " | Prestados: " + cantidadPrestada +
               " | Disponibles: " + cantidadDisponible;
    }
}
