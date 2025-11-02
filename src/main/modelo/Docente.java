package main.modelo;

import main.interfaces.IUsuariosAcciones;
import main.persistencia.GestorDePersistencia;

import java.util.ArrayList;
import java.util.List;

public class Docente extends Usuario implements IUsuariosAcciones {
    private String matricula;
    private List<Curso> cursosDictados;

    public Docente(int idUsuario, String nombre, String apellido, String email, String contraseña, String matricula) {
        super(idUsuario, nombre, apellido, email, contraseña, TipoUsuario.DOCENTE);
        this.matricula = matricula;
        this.cursosDictados = new ArrayList<>();
    }

    public void crearCurso(Curso curso, GestorDePersistencia gestor) {
        cursosDictados.add(curso);
        curso.setDocente(this);
        System.out.println("Curso creado por " + nombre + ": " + curso.getTitulo());

        gestor.guardarCurso(curso);
    }
/*
    public void modificarContenido(Curso curso, Contenido contenido) {
        curso.modificarContenido(contenido);
        System.out.println("Contenido modificado en curso:"  + curso.getTitulo());
    }
*/
    public void verAlumnos(Curso curso) {
        curso.listarAlumnos();
    }

    public String getMatricula() { return matricula; }

    @Override
    public void registrarse() {

    }

    @Override
    public boolean iniciarSesion(String email, String contrasena) {
        return false;
    }

    @Override
    public void cerrarSesion() {

    }

    @Override
    public void actualizarPerfil(String nombre, String apellido, String email) {

    }
}
