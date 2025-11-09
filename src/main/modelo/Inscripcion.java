package main.modelo;

import java.util.Date;
import java.util.Objects;

public class Inscripcion {
    private int idInscripcion;
    private Date fecha;
    private Alumno alumno;
    private transient Curso curso;
    private Pago pago;
    private EstadoInscripcion estadoPago;
    private EstadoCurso estadoCurso;

    // ==================== CONSTRUCTORES ====================
    /**
     * Constructor completo para crear una inscripción con todos los datos.
     * Se usa cuando se recupera desde la BD.
     */
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

    /**
     * Constructor con estados de pago y curso personalizados.
     * Se usa para crear una inscripción nueva con valores específicos.
     */
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

    /**
     * Constructor simplificado para crear una inscripción nueva.
     * Por defecto: PENDIENTE_PAGO y CURSANDO
     */
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

    /**
     * Valida que los datos básicos (alumno y curso) no sean nulos.
     */
    private void validarDatosBasicos(Alumno alumno, Curso curso) {
        if (alumno == null) {
            throw new IllegalArgumentException("❌ El alumno no puede ser nulo");
        }
        if (curso == null) {
            throw new IllegalArgumentException("❌ El curso no puede ser nulo");
        }
    }

    /**
     * Verifica si la inscripción es válida (tiene todos los datos necesarios).
     */
    public boolean esValida() {
        return this.alumno != null && 
               this.curso != null && 
               this.fecha != null &&
               this.estadoPago != null &&
               this.estadoCurso != null;
    }

    // ==================== MÉTODOS DE NEGOCIO ====================

    /**
     * Verifica si la inscripción está pagada.
     */
    public boolean estaPagada() {
        return this.estadoPago == EstadoInscripcion.PAGO;
    }

    /**
     * Verifica si el pago está pendiente.
     */
    public boolean tienePagoPendiente() {
        return this.estadoPago == EstadoInscripcion.PENDIENTE_PAGO;
    }

    /**
     * Verifica si el curso está activo (el alumno está cursando).
     */
    public boolean estaCursando() {
        return this.estadoCurso == EstadoCurso.CURSANDO;
    }

    /**
     * Verifica si el curso fue aprobado.
     */
    public boolean estaAprobado() {
        return this.estadoCurso == EstadoCurso.APROBADO;
    }

    /**
     * Verifica si el curso fue desaprobado.
     */
    public boolean estaDesaprobado() {
        return this.estadoCurso == EstadoCurso.DESAPROBADO;
    }

    /**
     * Verifica si la inscripción está cancelada.
     */
    public boolean estaCancelada() {
        return this.estadoCurso == EstadoCurso.DADO_DE_BAJA;
    }

    /**
     * Registra un pago y actualiza el estado a PAGO.
     * @param pago El pago realizado
     * @throws IllegalArgumentException si el pago es nulo
     * @throws IllegalStateException si ya hay un pago registrado
     */
    public void registrarPago(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException("❌ El pago no puede ser nulo");
        }
        if (this.pago != null && this.estaPagada()) {
            throw new IllegalStateException("⚠️ Ya existe un pago registrado para esta inscripción");
        }
        this.pago = pago;
        this.estadoPago = EstadoInscripcion.PAGO;
    }

    /**
     * Marca el curso como aprobado.
     * @throws IllegalStateException si el curso no está pagado o ya está finalizado
     */
    public void aprobarCurso() {
        if (!estaPagada()) {
            throw new IllegalStateException("❌ No se puede aprobar un curso sin haber pagado");
        }
        if (this.estadoCurso == EstadoCurso.APROBADO) {
            throw new IllegalStateException("❌ No se puede aprobar un curso cancelado");
        }
        this.estadoCurso = EstadoCurso.APROBADO;
    }

    /**
     * Marca el curso como desaprobado.
     * @throws IllegalStateException si el curso no está pagado o ya está finalizado
     */
    public void desaprobarCurso() {
        if (!estaPagada()) {
            throw new IllegalStateException("❌ No se puede desaprobar un curso sin haber pagado");
        }
        if (this.estadoCurso == EstadoCurso.DESAPROBADO) {
            throw new IllegalStateException("❌ No se puede desaprobar un curso cancelado");
        }
        this.estadoCurso = EstadoCurso.DESAPROBADO;
    }

    /**
     * Cancela la inscripción.
     * No se puede cancelar si ya está aprobado o desaprobado.
     */
    public void cancelarInscripcion() {
        if (this.estadoCurso == EstadoCurso.APROBADO || 
            this.estadoCurso == EstadoCurso.DADO_DE_BAJA) {
            throw new IllegalStateException("❌ No se puede cancelar un curso ya finalizado");
        }
        this.estadoCurso = EstadoCurso.DADO_DE_BAJA;
    }

    /**
     * Obtiene el monto del pago (si existe).
     * @return El monto del pago o 0.0 si no hay pago registrado
     */
    public double obtenerMontoPagado() {
        return (pago != null) ? pago.getMonto() : 0.0;
    }

    /**
     * Verifica si el alumno puede ser evaluado (debe estar cursando y con pago realizado).
     */
    public boolean puedeSerEvaluado() {
        return estaPagada() && estaCursando();
    }

    // ==================== GETTERS Y SETTERS ====================

    public int getIdInscripcion() { 
        return idInscripcion; 
    }

    public void setIdInscripcion(int idInscripcion) { 
        this.idInscripcion = idInscripcion; 
    }

    public Date getFecha() { 
        return fecha; 
    }

    public void setFecha(Date fecha) { 
        this.fecha = fecha; 
    }

    public Alumno getAlumno() { 
        return alumno; 
    }

    public Curso getCurso() { 
        return curso; 
    }

    public void setCurso(Curso curso) { 
        if (curso == null) {
            throw new IllegalArgumentException("❌ El curso no puede ser nulo");
        }
        this.curso = curso; 
    }

    public Pago getPago() { 
        return pago; 
    }

    public void setPago(Pago pago) { 
        this.pago = pago; 
    }

    public EstadoInscripcion getEstadoPago() { 
        return estadoPago; 
    }

    public void setEstadoPago(EstadoInscripcion estadoPago) { 
        this.estadoPago = estadoPago; 
    }

    public EstadoCurso getEstadoCurso() { 
        return estadoCurso; 
    }

    public void setEstadoCurso(EstadoCurso estadoCurso) { 
        this.estadoCurso = estadoCurso; 
    }

    // ==================== MÉTODOS OBJECT ====================

    @Override
    public String toString() {
        return String.format(
            "Inscripcion{id=%d, fecha=%s, alumno='%s', curso='%s', estadoPago=%s, estadoCurso=%s, monto=%.2f}",
            idInscripcion,
            fecha != null ? fecha.toString() : "N/A",
            alumno != null ? alumno.getNombre() + " " + alumno.getApellido() : "N/A",
            curso != null ? curso.getTitulo() : "N/A",
            estadoPago,
            estadoCurso,
            obtenerMontoPagado()
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
