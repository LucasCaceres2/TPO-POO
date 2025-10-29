package main.modelo;

import main.persistencia.GestorDePersistencia;

import java.util.ArrayList;
import java.util.List;

public class Docente extends Usuario {
    protected String matricula;
    private List<Curso> cursosDictados;

    public Docente(String nombre,
                   String apellido,
                   String email,
                   String contrasena) {

        super(nombre, apellido, email, contrasena, TipoUsuario.DOCENTE);
        this.matricula = null; // se completa en registrarse()
        this.cursosDictados = new ArrayList<>();
    }

    public void crearCurso(Curso curso, GestorDePersistencia gestor) {
        cursosDictados.add(curso);
        curso.setDocente(this);
        System.out.println("Curso creado por " + nombre + ": " + curso.getTitulo());

        gestor.guardarCurso(curso);
    }

/*    public void modificarContenido(Curso curso, Contenido contenido) {
        curso.modificarContenido(contenido);
        System.out.println("Contenido modificado en curso:"  + curso.getTitulo());
    }*/

    public void modificarContenido(Curso curso, String nuevoContenido) {
        curso.setContenido(nuevoContenido);
        System.out.println("Contenido actualizado en el curso: " + curso.getTitulo());
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
