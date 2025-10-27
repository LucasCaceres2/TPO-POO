package main.modelo;

import main.interfaces.IPagable;
import java.util.Date;

public class Pago implements IPagable {
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

    @Override
    public boolean validarPago() {
        // Lógica simple de validación
        return monto > 0;
    }

    @Override
    public void generarComprobante() {
        System.out.println("Comprobante generado:");
        System.out.println("Alumno: " + alumno.getNombre());
        System.out.println("Fecha: " + fecha);
        System.out.println("Monto: $" + monto);
    }

    // Getters
    public String getIdPago() { return idPago; }
    public Date getFecha() { return fecha; }
    public double getMonto() { return monto; }
    public Alumno getAlumno() { return alumno; }
}
