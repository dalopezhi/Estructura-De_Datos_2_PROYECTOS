package model;

public class Sesion {

    private Usuario usuarioActual;

    public void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean estaActiva() {
        return usuarioActual != null;
    }
}
