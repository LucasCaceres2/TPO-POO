package main.modelo;

import java.util.ArrayList;
import java.util.List;

public class Docente extends Usuario {
    private String matricula;
    private List<Curso> cursosDictados;

    public Docente(String idUsuario, String nombre, String apellido, String email, String contraseña, String matricula) {
        super(idUsuario, nombre, apellido, email, contraseña, TipoUsuario.DOCENTE);
        this.matricula = matricula;
        this.cursosDictados = new ArrayList<>();
    }

    public void crearCurso(Curso curso) {
        cursosDictados.add(curso);
        curso.setDocente(this);
        System.out.println("Curso creado por " + nombre + ": " + curso.getTitulo());
    }

    public void agregarContenido(Curso curso, Contenido contenido) {
        curso.agregarContenido(contenido);
        System.out.println("Contenido agregado al curso: " + curso.getTitulo());
    }

    public void modificarContenido(Curso curso, Contenido contenido) {
        curso.modificarContenido(contenido);
    }

    public void verAlumnos(Curso curso) {
        curso.listarAlumnos();
    }

    @Override
    public void iniciarSesion() {
        System.out.println("Docente " + nombre + " inició sesión.");
    }

    @Override
    public void cerrarSesion() {
        System.out.println("Docente " + nombre + " cerró sesión.");
    }

    @Override
    public void actualizarPerfil() {
        System.out.println("Perfil de docente actualizado.");
    }

    public String getMatricula() { return matricula; }
}
