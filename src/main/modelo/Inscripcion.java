package main.modelo;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class Inscripcion {
    private int idInscripcion;
    private Date fecha;
    private Alumno alumno;
    private transient Curso curso;
    private Pago pago;
    private EstadoInscripcion estadoPago;
    private EstadoCurso estadoCurso;

    // Map para guardar notas por tipo de evaluación
    private Map<TipoEvaluacion, Double> calificaciones = new HashMap<>();
    private Double porcentajeAsistencia = null; // se completa desde DAO/Plataforma

    // ==================== CONSTRUCTORES ====================
    public Inscripcion(int idInscripcion, Date fecha, Alumno alumno, Curso curso,
                       Pago pago, EstadoInscripcion estadoPago, EstadoCurso estadoCurso) {
        validarDatosBasicos(alumno, curso);
        this.idInscripcion = idInscripcion;
        this.fecha = fecha != null ? fecha : new Date();
        this.alumno = alumno;
        this.curso = curso;
        this.pago = pago;
        this.estadoPago = estadoPago != null ? estadoPago : EstadoInscripcion.PENDIENTE_PAGO;
        this.estadoCurso = estadoCurso != null ? estadoCurso : EstadoCurso.CURSANDO;
    }

    public Inscripcion(Alumno alumno, Curso curso, Pago pago,
                       EstadoInscripcion estadoPago, EstadoCurso estadoCurso) {
        validarDatosBasicos(alumno, curso);
        this.fecha = new Date();
        this.alumno = alumno;
        this.curso = curso;
        this.pago = pago;
        this.estadoPago = estadoPago != null ? estadoPago : EstadoInscripcion.PENDIENTE_PAGO;
        this.estadoCurso = estadoCurso != null ? estadoCurso : EstadoCurso.CURSANDO;
    }

    public Inscripcion(Alumno alumno, Curso curso) {
        validarDatosBasicos(alumno, curso);
        this.alumno = alumno;
        this.curso = curso;
        this.fecha = new Date();
        this.estadoPago = EstadoInscripcion.PENDIENTE_PAGO;
        this.estadoCurso = EstadoCurso.CURSANDO;
        this.pago = null;
    }

    // ==================== VALIDACIONES ====================
    private void validarDatosBasicos(Alumno alumno, Curso curso) {
        if (alumno == null) throw new IllegalArgumentException("❌ El alumno no puede ser nulo");
        if (curso == null) throw new IllegalArgumentException("❌ El curso no puede ser nulo");
    }

    public boolean esValida() {
        return alumno != null && curso != null && fecha != null &&
                estadoPago != null && estadoCurso != null;
    }

    // ==================== MÉTODOS DE NEGOCIO ====================
    public boolean estaPagada() {
        return estadoPago == EstadoInscripcion.PAGO;
    }

    public boolean tienePagoPendiente() {
        return estadoPago == EstadoInscripcion.PENDIENTE_PAGO;
    }

    public boolean estaCursando() {
        return estadoCurso == EstadoCurso.CURSANDO;
    }

    public boolean estaAprobado() {
        return estadoCurso == EstadoCurso.APROBADO;
    }

    public boolean estaDesaprobado() {
        return estadoCurso == EstadoCurso.DESAPROBADO;
    }

    public boolean estaCancelada() {
        return estadoCurso == EstadoCurso.DADO_DE_BAJA;
    }

    public void registrarPago(Pago pago) {
        if (pago == null) throw new IllegalArgumentException("❌ El pago no puede ser nulo");
        if (this.pago != null && estaPagada())
            throw new IllegalStateException("⚠️ Ya existe un pago registrado para esta inscripción");
        this.pago = pago;
        this.estadoPago = EstadoInscripcion.PAGO;
    }

    public void aprobarCurso() {
        if (!estaPagada()) throw new IllegalStateException("❌ No se puede aprobar un curso sin haber pagado");
        if (estadoCurso == EstadoCurso.APROBADO)
            throw new IllegalStateException("❌ No se puede aprobar un curso ya finalizado");
        estadoCurso = EstadoCurso.APROBADO;
    }

    public void desaprobarCurso() {
        if (!estaPagada()) throw new IllegalStateException("❌ No se puede desaprobar un curso sin haber pagado");
        if (estadoCurso == EstadoCurso.DESAPROBADO)
            throw new IllegalStateException("❌ No se puede desaprobar un curso ya finalizado");
        estadoCurso = EstadoCurso.DESAPROBADO;
    }

    public void cancelarInscripcion() {
        if (estadoCurso == EstadoCurso.APROBADO || estadoCurso == EstadoCurso.DESAPROBADO)
            throw new IllegalStateException("❌ No se puede cancelar un curso ya finalizado");
        estadoCurso = EstadoCurso.DADO_DE_BAJA;
    }

    public boolean puedeSerEvaluado() {
        return estaPagada() && estaCursando();
    }

    public double obtenerMontoPagado() {
        return pago != null ? pago.getMonto() : 0.0;
    }

    // ==================== MÉTODOS NUEVOS ====================

    // Calificaciones
    public void registrarCalificacion(TipoEvaluacion tipo, double nota) {
        calificaciones.put(tipo, nota);
    }

    public Double getCalificacion(TipoEvaluacion tipo) {
        return calificaciones.get(tipo);
    }

    public Double getPromedio() {
        if (calificaciones.isEmpty()) return null;
        double suma = 0.0;
        for (Double nota : calificaciones.values()) suma += nota;
        return suma / calificaciones.size();
    }

    // Baja del curso
    public boolean puedeDarseDeBaja() {
        return estadoCurso != EstadoCurso.APROBADO && estadoCurso != EstadoCurso.DESAPROBADO;
    }

    public void darseDeBaja() {
        if (!puedeDarseDeBaja())
            throw new IllegalStateException("❌ No se puede dar de baja de un curso ya finalizado");
        cancelarInscripcion();
    }

    // Asistencia
    public void setPorcentajeAsistencia(double porcentaje) {
        if (porcentaje < 0.0 || porcentaje > 100.0)
            throw new IllegalArgumentException("❌ Porcentaje inválido");
        this.porcentajeAsistencia = porcentaje;
    }

    public Double getPorcentajeAsistencia() {
        return porcentajeAsistencia;
    }

    // Resumen de rendimiento
    public String resumenRendimiento() {
        return String.format(
                "Curso: %s | Estado: %s | Asistencia: %s%% | Promedio: %s",
                curso != null ? curso.getTitulo() : "N/A",
                estadoCurso,
                porcentajeAsistencia != null ? String.format("%.2f", porcentajeAsistencia) : "N/A",
                getPromedio() != null ? String.format("%.2f", getPromedio()) : "N/A"
        );
    }

    // ==================== GETTERS Y SETTERS ====================
    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Alumno getAlumno() { return alumno; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) {
        if (curso == null) throw new IllegalArgumentException("❌ El curso no puede ser nulo");
        this.curso = curso;
    }

    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }

    public EstadoInscripcion getEstadoPago() { return estadoPago; }
    public void setEstadoPago(EstadoInscripcion estadoPago) { this.estadoPago = estadoPago; }

    public EstadoCurso getEstadoCurso() { return estadoCurso; }
    public void setEstadoCurso(EstadoCurso estadoCurso) { this.estadoCurso = estadoCurso; }

    // ==================== MÉTODOS OBJECT ====================
    @Override
    public String toString() {
        return String.format(
                "Inscripcion{id=%d, fecha=%s, alumno='%s', curso='%s', estadoPago=%s, estadoCurso=%s, monto=%.2f, promedio=%s}",
                idInscripcion,
                fecha != null ? fecha.toString() : "N/A",
                alumno != null ? alumno.getNombre() + " " + alumno.getApellido() : "N/A",
                curso != null ? curso.getTitulo() : "N/A",
                estadoPago,
                estadoCurso,
                obtenerMontoPagado(),
                getPromedio() != null ? String.format("%.2f", getPromedio()) : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inscripcion)) return false;
        Inscripcion that = (Inscripcion) o;
        return idInscripcion == that.idInscripcion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idInscripcion);
    }
}
