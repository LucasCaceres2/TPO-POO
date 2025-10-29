package main.modelo;

import java.util.ArrayList;
import java.util.List;

public class Alumno extends Usuario {

    protected String legajo;
    private List<Inscripcion> inscripciones;

    // Constructor SIN idUsuario (el id se asigna recién al registrarse())
    public Alumno(String nombre,
                  String apellido,
                  String email,
                  String contrasena,
                  String legajo) {

        super(nombre, apellido, email, contrasena, TipoUsuario.ALUMNO);
        this.legajo = null;
        this.inscripciones = new ArrayList<>();
    }

    // -------- Getters ----------
    public String getLegajo() {
        return legajo;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    // --------- Lógica de negocio opcional ---------

    // Inscribirse a un curso
    public void inscribirse(Curso curso) {
        if (curso.tieneCupo()) {

            // Creamos la inscripción
            Inscripcion inscripcion = new Inscripcion(this, curso);

            // Agregamos la inscripción al curso,
            // y el curso ya se encarga de linkear también al alumno
            curso.agregarInscripcion(inscripcion);

            System.out.println(nombre + " se inscribió al curso: " + curso.getTitulo());
        } else {
            System.out.println("No hay cupo disponible para el curso: " + curso.getTitulo());
        }
    }

    // Ver todos los cursos donde estoy inscripto
    public void verCursosInscritos() {
        if (inscripciones.isEmpty()) {
            System.out.println(nombre + " no tiene cursos inscritos.");
        } else {
            System.out.println("Cursos de " + nombre + ":");
            for (Inscripcion i : inscripciones) {
                System.out.println("- " + i.getCurso().getTitulo());
            }
        }
    }

    // Pagar algo asociado a una inscripción
    public void realizarPago(Pago pago) {
        if (pago.validarPago()) {
            System.out.println("Pago realizado correctamente por " + nombre);
        } else {
            System.out.println("Error al procesar el pago.");
        }
    }

    // --------- Métodos abstractos del padre ---------

    @Override
    public void iniciarSesion() {
        System.out.println("Alumno " + nombre + " inició sesión.");
    }

    @Override
    public void cerrarSesion() {
        System.out.println("Alumno " + nombre + " cerró sesión.");
    }

    @Override
    public void actualizarPerfil() {
        System.out.println("Perfil de alumno actualizado.");
    }
}
