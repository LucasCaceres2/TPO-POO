package main.dto;

import main.modelo.TipoUsuario;

public class AlumnoDTO extends UsuarioDTO {
    private String legajo;

    public AlumnoDTO(String idUsuario, String nombre, String apellido, String email, String contrasena, String legajo) {
        super(idUsuario, nombre, apellido, email, contrasena, TipoUsuario.ALUMNO);
        this.legajo = legajo;
    }

    public String getLegajo() { return legajo; }
}
