package main.modelo;

import java.util.Date;

public class Pago {
    private int idPago;       // asignado por la BD
    private Date fecha;       // momento en que se registra el pago
    private double monto;
    private Alumno alumno;

    // Para pagos leídos de BD
    public Pago(int idPago, Date fecha, double monto, Alumno alumno) {
        if (alumno == null) throw new IllegalArgumentException("El pago debe tener un alumno asociado.");
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser mayor a 0.");
        this.idPago = idPago;
        this.fecha = (fecha != null) ? fecha : new Date();
        this.monto = monto;
        this.alumno = alumno;
    }

    // Para nuevos pagos
    public Pago(double monto, Alumno alumno) {
        this(0, new Date(), monto, alumno);
    }

    // Getters
    public int getIdPago() { return idPago; }
    public Date getFecha() { return fecha; }
    public double getMonto() { return monto; }
    public Alumno getAlumno() { return alumno; }

    // Solo BD setea el id
    public void setIdPago(int idPago) { this.idPago = idPago; }

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