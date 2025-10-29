package main.modelo;

import java.util.Date;

public class Inscripcion {
    private String idInscripcion;
    private Date fecha;
    private Alumno alumno;
    private transient Curso curso; // evita ciclo infinito en JSON
    private Pago pago;
    private EstadoInscripcion estado;

    public Inscripcion(Alumno alumno, Curso curso, Pago pago) {
        this.idInscripcion = "INS-" + System.currentTimeMillis();
        this.fecha = new Date();
        this.alumno = alumno;
        this.curso = curso;
        this.pago = pago;
        this.estado = EstadoInscripcion.PENDIENTE_PAGO; // al crear la inscripción
    }
    public Inscripcion(Alumno alumno, Curso curso, Pago pago, EstadoInscripcion estado) {
        this(alumno, curso, pago);
        this.estado = (estado != null) ? estado : EstadoInscripcion.PENDIENTE_PAGO;
    }

    public Inscripcion(String idInscripcion, Alumno alumno, Curso curso, Pago pago, EstadoInscripcion estado) {
        this.idInscripcion = idInscripcion;
        this.alumno = alumno;
        this.curso = curso;
        this.pago = pago;
        this.estado = estado;
    }



    public Inscripcion(Alumno alumno, Curso curso) {
        this(alumno, curso, null); // Llama al constructor principal
        this.estado = EstadoInscripcion.PENDIENTE_PAGO; // predeterminado
    }

    // Getters y Setters
    public String getIdInscripcion() { return idInscripcion; }
    public Date getFecha() { return fecha; }
    public Alumno getAlumno() { return alumno; }
    public Curso getCurso() { return curso; }
    public Pago getPago() { return pago; }
    public EstadoInscripcion getEstado() { return estado; }

    public void setCurso(Curso curso) { this.curso = curso; }
    public void setPago(Pago pago) { this.pago = pago; }
    public void setEstado(EstadoInscripcion estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Inscripcion{" +
                "idInscripcion='" + idInscripcion + '\'' +
                ", fecha=" + fecha +
                ", alumno=" + alumno.getNombre() +
                ", curso=" + (curso != null ? curso.getTitulo() : "N/A") +
                ", estado=" + estado +
                ", pago=" + (pago != null ? pago.getMonto() + " $" : "N/A") +
                '}';
    }
}
