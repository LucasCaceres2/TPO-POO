package main.modelo;

import main.controlador.Plataforma;

public class Administrador extends Usuario {

    private final Plataforma plataforma = new Plataforma();

    public Administrador(int idUsuario,
                         String nombre,
                         String apellido,
                         String email,
                         String contrasena) {
        super(idUsuario, nombre, apellido, email, contrasena, TipoUsuario.ADMIN);
    }


    public boolean crearArea(String nombreArea) {
        return plataforma.crearArea(nombreArea);
    }


    public boolean crearCurso(String titulo,
                              int cupoMax,
                              String matriculaDocente,
                              String nombreArea,
                              String descripcion,
                              int cantidadClases,
                              double precio) {
        return plataforma.crearCurso(
                titulo,
                cupoMax,
                matriculaDocente,
                nombreArea,
                descripcion,
                cantidadClases,
                precio
        );
    }


    public boolean eliminarCurso(int idCurso) {
        return plataforma.eliminarCurso(idCurso);
    }
}
