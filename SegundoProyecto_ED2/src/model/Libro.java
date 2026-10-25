package model;

public class Libro {

    private String isbn;
    private String titulo;

    public Libro(String isbn, String titulo) {
        this.isbn = isbn;
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    @Override
    public String toString() {
        return isbn + " - " + titulo;
    }
}

