package main.modelo;

import main.util.IdGenerator;

public abstract class Usuario {
    protected final String idUsuario;
    protected final String nombre;
    protected final String apellido;
    protected final String email;
    protected final String contrasena;
    protected final TipoUsuario tipoUsuario;

    /** NUEVO: constructor que genera idUsuario automáticamente */
    public Usuario(String nombre, String apellido,
                   String email, String contrasena, TipoUsuario tipoUsuario) {
        this(IdGenerator.newUsuarioId(), nombre, apellido, email, contrasena, tipoUsuario);
    }

    /** EXISTENTE: se mantiene por compatibilidad (por si cargás desde persistencia con ID ya guardado) */
    public Usuario(String idUsuario, String nombre, String apellido,
                   String email, String contrasena, TipoUsuario tipoUsuario) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
    }

    public String getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getContrasena() { return contrasena; }
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario='" + idUsuario + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", tipoUsuario=" + tipoUsuario +
                '}';
    }
}
