package main.interfaces;

public interface IUsuariosAcciones {
    void registrarse();
    boolean iniciarSesion(String email, String contrasena);
    void cerrarSesion();
    void actualizarPerfil(String nombre, String apellido, String email);
}

