package main.dto;

import java.util.Date;

public class PagoDTO {
    private String idPago;
    private Date fecha;
    private double monto;
    private String idAlumno;

    public PagoDTO() {}

    public PagoDTO(String idPago, Date fecha, double monto, String idAlumno) {
        this.idPago = idPago;
        this.fecha = fecha;
        this.monto = monto;
        this.idAlumno = idAlumno;
    }

    // Getters y Setters
    public String getIdPago() { return idPago; }
    public void setIdPago(String idPago) { this.idPago = idPago; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getIdAlumno() { return idAlumno; }
    public void setIdAlumno(String idAlumno) { this.idAlumno = idAlumno; }
}
