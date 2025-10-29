package main.dto;

import java.util.Date;

public class InscripcionDTO {
    private String idInscripcion;
    private Date fecha;
    private String idAlumno;
    private String idCurso;
    private String estado; // Ej: "CURSANDO", "DADA_DE_BAJA"
    private PagoDTO pago;  // Contiene toda la info del pago

    public InscripcionDTO() {}

    public InscripcionDTO(String idInscripcion, Date fecha, String idAlumno, String idCurso, String estado, PagoDTO pago) {
        this.idInscripcion = idInscripcion;
        this.fecha = fecha;
        this.idAlumno = idAlumno;
        this.idCurso = idCurso;
        this.estado = estado;
        this.pago = pago;
    }

    // Getters y Setters
    public String getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(String idInscripcion) { this.idInscripcion = idInscripcion; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getIdAlumno() { return idAlumno; }
    public void setIdAlumno(String idAlumno) { this.idAlumno = idAlumno; }

    public String getIdCurso() { return idCurso; }
    public void setIdCurso(String idCurso) { this.idCurso = idCurso; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public PagoDTO getPago() { return pago; }
    public void setPago(PagoDTO pago) { this.pago = pago; }
}
