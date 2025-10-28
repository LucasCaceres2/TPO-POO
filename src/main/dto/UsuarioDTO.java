// UsuarioDTO.java
package main.dto;

import main.modelo.TipoUsuario;

public class UsuarioDTO {
    private String idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasena;
    private String tipoUsuario; // "ALUMNO" o "DOCENTE"
    private String legajo; // solo para alumnos
    private String matricula; // solo para docentes

    public UsuarioDTO(String idUsuario, String nombre, String apellido, String email, String contrasena, TipoUsuario tipoUsuario) {
    }

    public UsuarioDTO() {}

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getLegajo() { return legajo; }
    public void setLegajo(String legajo) { this.legajo = legajo; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
}
