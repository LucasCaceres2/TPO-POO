package main.modelo;

public class Asistencia {
    private int idAsistencia;
    private Inscripcion inscripcion;
    private Clase clase;
    private boolean presente;

    // Para registros desde BD
    public Asistencia(int idAsistencia, Inscripcion inscripcion, Clase clase, boolean presente) {
        if (inscripcion == null) throw new IllegalArgumentException("La asistencia debe estar asociada a una inscripción.");
        this.idAsistencia = idAsistencia;
        this.inscripcion = inscripcion;
        this.clase = clase;
        this.presente = presente;
    }

    // Para nuevas asistencias
    public Asistencia(Inscripcion inscripcion, Clase clase, boolean presente) {
        this(0, inscripcion, clase, presente);
    }

    public int getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(int idAsistencia) { this.idAsistencia = idAsistencia; }

    public Inscripcion getInscripcion() { return inscripcion; }
    public Clase getClase() { return clase; }
    public boolean isPresente() { return presente; }

    @Override
    public String toString() {
        return "Asistencia{" +
                "idAsistencia=" + idAsistencia +
                ", inscripcion=" + inscripcion.getIdInscripcion() +
                ", clase=" + clase +
                ", presente=" + presente +
                '}';
    }
}
