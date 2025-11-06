package main.modelo;

import java.util.Date;

public class Inscripcion {
    private int idInscripcion;
    private Date fecha;
    private Alumno alumno;
    private transient Curso curso; // evita ciclo infinito en JSON
    private Pago pago;
    private EstadoInscripcion estadoPago;
    private EstadoCurso estadoCurso;

    public Inscripcion(Alumno alumno, Curso curso, Pago pago, EstadoInscripcion estadoPago, EstadoCurso estadoCurso) {
        this.fecha = new Date();
        this.alumno = alumno;
        this.curso = curso;
        this.pago = pago;
        this.estadoPago = estadoPago;
        this.estadoCurso = estadoCurso;
    }

    public Inscripcion(int idInscripcion, Date fecha, Alumno alumno, Curso curso, Pago pago, EstadoInscripcion estadoPago, EstadoCurso estadoCurso) {
        this.idInscripcion = idInscripcion;
        this.fecha = fecha;
        this.alumno = alumno;
        this.curso = curso;
        this.pago = pago;
        this.estadoPago = estadoPago;
        this.estadoCurso = estadoCurso;
    }

    public Inscripcion(Alumno alumno, Curso curso) {
        this.alumno = alumno;
        this.curso = curso;
        this.fecha = new Date(); // asigna fecha actual
        this.estadoPago = EstadoInscripcion.PENDIENTE_PAGO;
        this.estadoCurso = EstadoCurso.CURSANDO;
    }

    // Getters y Setters
    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Alumno getAlumno() { return alumno; }
    public Curso getCurso() { return curso; }
    public Pago getPago() { return pago; }
    public EstadoInscripcion getEstadoPago() { return estadoPago; }
    public EstadoCurso getEstadoCurso() { return estadoCurso; }

    public void setCurso(Curso curso) { this.curso = curso; }
    public void setPago(Pago pago) { this.pago = pago; }
    public void setEstadoPago(EstadoInscripcion estado) { this.estadoPago = estado; }
    public void setEstadoCurso(EstadoCurso estado) { this.estadoCurso = estado; }

    @Override
    public String toString() {
        return "Inscripcion{" +
                "idInscripcion=" + idInscripcion +
                ", fecha=" + fecha +
                ", alumno=" + alumno.getNombre() +
                ", curso=" + (curso != null ? curso.getTitulo() : "N/A") +
                ", estadoPago=" + estadoPago +
                ", estadoCurso=" + estadoCurso +
                ", pago=" + (pago != null ? pago.getMonto() + " $" : "N/A") +
                '}';
    }
}
