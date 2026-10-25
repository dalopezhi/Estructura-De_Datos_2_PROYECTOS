package model;

public class Usuario {

    private String nombreUsuario;
    private String hash;
    private boolean premium;

    public Usuario(String nombreUsuario, String hash, boolean premium) {
        this.nombreUsuario = nombreUsuario;
        this.hash = hash;
        this.premium = premium;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getHash() {
        return hash;
    }

    public boolean isPremium() {
        return premium;
    }
}

