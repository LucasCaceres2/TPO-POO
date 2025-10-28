package main.dto;

import main.modelo.TipoUsuario;

public class DocenteDTO extends UsuarioDTO {
    private String matricula;

    public DocenteDTO(String idUsuario, String nombre, String apellido, String email, String contrasena, String matricula) {
        super(idUsuario, nombre, apellido, email, contrasena, TipoUsuario.DOCENTE);
        this.matricula = matricula;
    }

    public String getMatricula() { return matricula; }
}
