package main.modelo;

import java.sql.Date;

public class Pago  {
    private int idPago;
    private Date fecha;
    private double monto;
    private Alumno alumno;

    public Pago(int idPago, Date fecha, double monto, Alumno alumno) {
        this.idPago = idPago;
        this.fecha = fecha;
        this.monto = monto;
        this.alumno = alumno;
    }

    public boolean pagar(double monto) {
        if (monto <= 0) return false;
        this.monto = monto;
        this.fecha = new Date(System.currentTimeMillis()); // ✅ Fecha actual compatible con MySQL
        return true;
    }


    // Getters
    public int getIdPago() { return idPago; }
    public Date getFecha() { return fecha; }
    public double getMonto() { return monto; }
    public Alumno getAlumno() { return alumno; }
}
