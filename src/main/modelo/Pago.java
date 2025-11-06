package main.modelo;

import java.sql.Date;

public class Pago {
    private int idPago;
    private Date fecha;
    private double monto;
    private Alumno alumno;

    // Constructor para pagos ya existentes
    public Pago(int idPago, Date fecha, double monto, Alumno alumno) {
        this.idPago = idPago;
        this.fecha = fecha;
        this.monto = monto;
        this.alumno = alumno;
    }

    // Constructor para nuevos pagos
    public Pago(double monto, Alumno alumno) {
        this.fecha = new Date(System.currentTimeMillis());
        this.monto = monto;
        this.alumno = alumno;
    }

    public boolean pagar(double monto) {
        if (monto <= 0) return false;
        this.monto = monto;
        this.fecha = new Date(System.currentTimeMillis());
        return true;
    }

    // Getters y Setters
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public double getMonto() { return monto; }
    public Alumno getAlumno() { return alumno; }

    @Override
    public String toString() {
        return "Pago{" +
                "idPago=" + idPago +
                ", fecha=" + fecha +
                ", monto=" + monto +
                ", alumno=" + (alumno != null ? alumno.getNombre() : "N/A") +
                '}';
    }
}
