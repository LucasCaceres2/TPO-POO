package main.modelo;

import java.util.Date;

public class Asistencia {
    private int idAsistencia;
    private Inscripcion inscripcion;
    private Date fecha;
    private boolean presente;

    // Para registros desde BD
    public Asistencia(int idAsistencia, Inscripcion inscripcion, Date fecha, boolean presente) {
        if (inscripcion == null) throw new IllegalArgumentException("La asistencia debe estar asociada a una inscripción.");
        this.idAsistencia = idAsistencia;
        this.inscripcion = inscripcion;
        this.fecha = (fecha != null) ? fecha : new Date();
        this.presente = presente;
    }

    // Para nuevas asistencias
    public Asistencia(Inscripcion inscripcion, Date fecha, boolean presente) {
        this(0, inscripcion, fecha, presente);
    }

    public int getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(int idAsistencia) { this.idAsistencia = idAsistencia; }

    public Inscripcion getInscripcion() { return inscripcion; }
    public Date getFecha() { return fecha; }
    public boolean isPresente() { return presente; }

    @Override
    public String toString() {
        return "Asistencia{" +
                "idAsistencia=" + idAsistencia +
                ", inscripcion=" + inscripcion.getIdInscripcion() +
                ", fecha=" + fecha +
                ", presente=" + presente +
                '}';
    }
}
