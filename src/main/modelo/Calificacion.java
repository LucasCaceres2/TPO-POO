package main.modelo;

import java.util.Date;

public class Calificacion {
    private int idCalificacion;
    private Inscripcion inscripcion;
    private String tipo;   // Ej: "Parcial 1", "TP", "Final"
    private double nota;
    private Date fecha;

    // Desde BD
    public Calificacion(int idCalificacion, Inscripcion inscripcion, String tipo, double nota, Date fecha) {
        if (inscripcion == null) throw new IllegalArgumentException("La calificación debe estar asociada a una inscripción.");
        if (tipo == null || tipo.trim().isEmpty()) throw new IllegalArgumentException("El tipo de calificación es obligatorio.");
        if (nota < 0 || nota > 10) throw new IllegalArgumentException("La nota debe estar entre 0 y 10.");
        this.idCalificacion = idCalificacion;
        this.inscripcion = inscripcion;
        this.tipo = tipo.trim();
        this.nota = nota;
        this.fecha = (fecha != null) ? fecha : new Date();
    }

    // Nuevas calificaciones
    public Calificacion(Inscripcion inscripcion, String tipo, double nota) {
        this(0, inscripcion, tipo, nota, new Date());
    }

    public int getIdCalificacion() { return idCalificacion; }
    public void setIdCalificacion(int idCalificacion) { this.idCalificacion = idCalificacion; }

    public Inscripcion getInscripcion() { return inscripcion; }
    public String getTipo() { return tipo; }
    public double getNota() { return nota; }
    public Date getFecha() { return fecha; }

    @Override
    public String toString() {
        return "Calificacion{" +
                "idCalificacion=" + idCalificacion +
                ", inscripcion=" + inscripcion.getIdInscripcion() +
                ", tipo='" + tipo + '\'' +
                ", nota=" + nota +
                ", fecha=" + fecha +
                '}';
    }
}
