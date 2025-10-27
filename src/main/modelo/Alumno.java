package main.modelo;

import java.util.ArrayList;
import java.util.List;

public class Alumno extends Usuario {
    private String legajo;
    private List<Inscripcion> inscripciones;

    //Constructor
    public Alumno(String idUsuario, String nombre, String apellido, String email, String contrasena, String legajo) {
        super(idUsuario, nombre, apellido, email, contrasena, TipoUsuario.ALUMNO);
        this.legajo = legajo;
        this.inscripciones = new ArrayList<>();
    }

    //Getters
    public String getLegajo() { return legajo; }
    public List<Inscripcion> getInscripciones() { return inscripciones; }


    public void inscribirse(Curso curso) {
        // Lógica básica de inscripción
        if (curso.tieneCupo()) {
            Inscripcion inscripcion = new Inscripcion(this, curso);
            curso.agregarInscripcion(inscripcion);
            System.out.println(nombre + " se inscribió al curso: " + curso.getTitulo());
        } else {
            System.out.println("No hay cupo disponible para el curso: " + curso.getTitulo());
        }
    }

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

    public void realizarPago(Pago pago) {
        if (pago.validarPago()) {
            System.out.println("Pago realizado correctamente por " + nombre);
        } else {
            System.out.println("Error al procesar el pago.");
        }
    }

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
