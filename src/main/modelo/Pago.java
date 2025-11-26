package main.modelo;

import java.util.Date;

public class Pago {
    private int idPago;       // asignado por la BD
    private Date fecha;       // momento en que se registra el pago
    private double monto;
    private Alumno alumno;
    private MedioPago medioPago;

    // Para pagos leídos de BD (con medio de pago)
    public Pago(int idPago, Date fecha, double monto, Alumno alumno, MedioPago medioPago) {
        if (alumno == null) throw new IllegalArgumentException("El pago debe tener un alumno asociado.");
        if (monto <= 0) throw new IllegalArgumentException("El monto debe ser mayor a 0.");
        this.idPago = idPago;
        this.fecha = (fecha != null) ? fecha : new Date();
        this.monto = monto;
        this.alumno = alumno;
        this.medioPago = medioPago;
    }

    // CONSTRUCTOR PUENTE (el que necesita InscripcionDAO)
    public Pago(int idPago, Date fecha, double monto, Alumno alumno) {
        this(idPago, fecha, monto, alumno, MedioPago.EFECTIVO); // medio por defecto
    }

    // Para nuevos pagos (desde la app)
    public Pago(double monto, Alumno alumno, MedioPago medioPago) {
        this(0, new Date(), monto, alumno, medioPago);
    }

    @Deprecated
    public Pago(double monto, Alumno alumno) {
        this(0, new Date(), monto, alumno, MedioPago.EFECTIVO); // por defecto
    }

    // Getters
    public int getIdPago() { return idPago; }
    public Date getFecha() { return fecha; }
    public double getMonto() { return monto; }
    public Alumno getAlumno() { return alumno; }
    public MedioPago getMedioPago() { return medioPago; }

    // Setters
    public void setIdPago(int idPago) { this.idPago = idPago; }
    public void setMedioPago(MedioPago medioPago) { this.medioPago = medioPago; }

    // 👉 NUEVO setter para fecha (te sirve si algún día necesitás cambiarla)
    public void setFecha(Date fecha) {
        this.fecha = (fecha != null) ? fecha : new Date();
    }

    @Override
    public String toString() {
        return "Pago{" +
                "idPago=" + idPago +
                ", fecha=" + fecha +
                ", monto=" + monto +
                ", alumno=" + (alumno != null ? alumno.getNombre() : "N/A") +
                ", medioPago=" + medioPago +
                '}';
    }
}
