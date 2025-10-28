package main.modelo;

import java.util.Date;

public class Inscripcion {
    private String idInscripcion;
    private Date fecha;
    private Alumno alumno;
    private transient Curso curso; // evita ciclo infinito
    private Pago pago;

    public Inscripcion(Alumno alumno, Curso curso) {
        this.idInscripcion = "INS-" + System.currentTimeMillis();
        this.fecha = new Date();
        this.alumno = alumno;
        this.curso = curso;
    }

    public void confirmarInscripcion() {
        System.out.println("Inscripción confirmada para " + alumno.getNombre() + " en " + (curso != null ? curso.getTitulo() : "Curso N/A"));
    }

    public void cancelarInscripcion() {
        System.out.println("Inscripción cancelada de " + alumno.getNombre() + " en " + (curso != null ? curso.getTitulo() : "Curso N/A"));
    }

    // Getters y Setters
    public String getIdInscripcion() { return idInscripcion; }
    public Date getFecha() { return fecha; }
    public Alumno getAlumno() { return alumno; }
    public Curso getCurso() { return curso; }
    public Pago getPago() { return pago; }

    public void setCurso(Curso curso) { this.curso = curso; }
    public void setPago(Pago pago) { this.pago = pago; }
}
