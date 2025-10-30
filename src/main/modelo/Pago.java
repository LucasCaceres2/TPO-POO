package main.modelo;

import main.util.IdGenerator;

import java.util.Date;

public class Pago  {
    private String idPago;
    private Date fecha;
    private double monto;
    private Alumno alumno;

    public Pago(String idPago, Date fecha, double monto, Alumno alumno) {
        this.idPago = idPago;
        this.fecha = fecha;
        this.monto = monto;
        this.alumno = alumno;
    }

    public Pago(Date fecha, double monto, Alumno alumno) {
        this(IdGenerator.newPagoId(), fecha, monto, alumno);
    }

    public boolean pagar(double monto) {
        if (monto <= 0) return false;
        this.monto = monto;
        this.fecha = new java.util.Date();
        return true;
    }


    // Getters
    public String getIdPago() { return idPago; }
    public Date getFecha() { return fecha; }
    public double getMonto() { return monto; }
    public Alumno getAlumno() { return alumno; }
}
