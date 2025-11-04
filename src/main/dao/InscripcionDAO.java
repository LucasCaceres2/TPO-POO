package main.dao;

import main.database.ConexionDB;
import main.modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InscripcionDAO {

    // Agrega inscripcion
    public boolean agregarInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null || inscripcion.getAlumno() == null || inscripcion.getCurso() == null) {
            System.out.println("⚠️ Datos incompletos de la inscripción.");
            return false;
        }

        Integer idAlumno = obtenerIdAlumnoPorIdUsuario(inscripcion.getAlumno().getIdUsuario());
        if (idAlumno == null) {
            System.out.println("⚠️ No se encontró alumno en tabla 'alumnos' para idUsuario: " + inscripcion.getAlumno().getIdUsuario());
            return false;
        }

        String checkSql = "SELECT 1 FROM inscripciones WHERE idAlumno = ? AND idCurso = ?";
        String insertSql = "INSERT INTO inscripciones (fecha, idAlumno, idCurso, idPago, estadoPago, estadoCurso) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar()) {
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, idAlumno);
                check.setInt(2, inscripcion.getCurso().getIdCurso());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    System.out.println("⚠️ El alumno ya está inscripto en este curso.");
                    return false;
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                java.sql.Date fechaSQL = new java.sql.Date(
                        (inscripcion.getFecha() != null ? inscripcion.getFecha() : new Date()).getTime()
                );

                stmt.setDate(1, fechaSQL);
                stmt.setInt(2, idAlumno);
                stmt.setInt(3, inscripcion.getCurso().getIdCurso());
                if (inscripcion.getPago() != null && inscripcion.getPago().getIdPago() > 0) {
                    stmt.setInt(4, inscripcion.getPago().getIdPago());
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }

                stmt.setString(5, inscripcion.getEstadoPago() != null
                        ? inscripcion.getEstadoPago().name()
                        : EstadoInscripcion.PENDIENTE_PAGO.name());
                stmt.setString(6, inscripcion.getEstadoCurso() != null
                        ? inscripcion.getEstadoCurso().name()
                        : EstadoCurso.CURSANDO.name());

                if (stmt.executeUpdate() > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) inscripcion.setIdInscripcion(rs.getInt(1));
                    }
                    System.out.println("✅ Inscripción registrada correctamente.");
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar inscripción: " + e.getMessage());
        }
        return false;
    }

    // Obtener inscripcion por id
    public Inscripcion obtenerInscripcionPorId(int idInscripcion) {
        String sql = """
                SELECT i.idInscripcion, i.fecha, i.estadoPago, i.estadoCurso,
                       a.idAlumno, a.idUsuario, a.legajo,
                       u.nombre AS alumnoNombre, u.apellido AS alumnoApellido, u.email AS alumnoEmail,
                       c.idCurso, c.titulo AS cursoTitulo, c.cupoMax, c.contenido,
                       p.idPago, p.monto, p.fecha AS fechaPago
                FROM inscripciones i
                JOIN alumnos a ON i.idAlumno = a.idAlumno
                JOIN usuarios u ON a.idUsuario = u.idUsuario
                JOIN cursos c ON i.idCurso = c.idCurso
                LEFT JOIN pagos p ON i.idPago = p.idPago
                WHERE i.idInscripcion = ?""";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idInscripcion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Alumno alumno = new Alumno(
                            rs.getInt("idUsuario"),
                            rs.getString("alumnoNombre"),
                            rs.getString("alumnoApellido"),
                            rs.getString("alumnoEmail"),
                            null,
                            rs.getString("legajo")
                    );

                    Curso curso = new Curso(
                            rs.getInt("idCurso"),
                            rs.getString("cursoTitulo"),
                            rs.getInt("cupoMax"),
                            null,
                            null,
                            rs.getString("contenido")
                    );

                    Pago pago = null;
                    int idPago = rs.getInt("idPago");
                    if (!rs.wasNull()) {
                        pago = new Pago(idPago, rs.getDate("fechaPago"), rs.getDouble("monto"), alumno);
                    }

                    return new Inscripcion(
                            rs.getInt("idInscripcion"),
                            rs.getDate("fecha"),
                            alumno,
                            curso,
                            pago,
                            EstadoInscripcion.valueOf(rs.getString("estadoPago")),
                            EstadoCurso.valueOf(rs.getString("estadoCurso"))
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener inscripción: " + e.getMessage());
        }
        System.out.println("⚠️ No se encontró inscripción con id: " + idInscripcion);
        return null;
    }

    // Listar inscripciones por alumno
    public List<Inscripcion> listarInscripcionesPorAlumnoIdUsuario(int idUsuario) {
        List<Inscripcion> lista = new ArrayList<>();
        Integer idAlumno = obtenerIdAlumnoPorIdUsuario(idUsuario);
        if (idAlumno == null) return lista;

        String sql = """
                SELECT i.idInscripcion, i.fecha, i.estadoPago, i.estadoCurso,
                       a.idAlumno, a.idUsuario, a.legajo, u.nombre AS alumnoNombre, u.apellido AS alumnoApellido, u.email AS alumnoEmail,
                       c.idCurso, c.titulo AS cursoTitulo, c.cupoMax, c.contenido,
                       p.idPago, p.monto, p.fecha AS fechaPago
                FROM inscripciones i
                JOIN alumnos a ON i.idAlumno = a.idAlumno
                JOIN usuarios u ON a.idUsuario = u.idUsuario
                JOIN cursos c ON i.idCurso = c.idCurso
                LEFT JOIN pagos p ON i.idPago = p.idPago
                WHERE i.idAlumno = ?""";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAlumno);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Alumno alumno = new Alumno(
                            rs.getInt("idUsuario"),
                            rs.getString("alumnoNombre"),
                            rs.getString("alumnoApellido"),
                            rs.getString("alumnoEmail"),
                            null,
                            rs.getString("legajo")
                    );

                    Curso curso = new Curso(
                            rs.getInt("idCurso"),
                            rs.getString("cursoTitulo"),
                            rs.getInt("cupoMax"),
                            null,
                            null,
                            rs.getString("contenido")
                    );

                    Pago pago = null;
                    int idPago = rs.getInt("idPago");
                    if (!rs.wasNull()) {
                        pago = new Pago(idPago, rs.getDate("fechaPago"), rs.getDouble("monto"), alumno);
                    }

                    lista.add(new Inscripcion(
                            rs.getInt("idInscripcion"),
                            rs.getDate("fecha"),
                            alumno,
                            curso,
                            pago,
                            EstadoInscripcion.valueOf(rs.getString("estadoPago")),
                            EstadoCurso.valueOf(rs.getString("estadoCurso"))
                    ));
                }
            }
            System.out.println("📘 Total inscripciones por alumno: " + lista.size());
        } catch (SQLException e) {
            System.out.println("❌ Error al listar inscripciones: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarEstadoPago(int idInscripcion, EstadoInscripcion nuevoEstado) {
        String sql = "UPDATE inscripciones SET estadoPago = ? WHERE idInscripcion = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado.name());
            stmt.setInt(2, idInscripcion);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar estadoPago: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizarEstadoCurso(int idInscripcion, EstadoCurso nuevoEstado) {
        String sql = "UPDATE inscripciones SET estadoCurso = ? WHERE idInscripcion = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado.name());
            stmt.setInt(2, idInscripcion);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar estadoCurso: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminarInscripcion(int idInscripcion) {
        String sql = "DELETE FROM inscripciones WHERE idInscripcion = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idInscripcion);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar inscripción: " + e.getMessage());
        }
        return false;
    }

    // -----------------------
    // Helper privado
    // -----------------------
    private Integer obtenerIdAlumnoPorIdUsuario(int idUsuario) {
        String sql = "SELECT idAlumno FROM alumnos WHERE idUsuario = ? LIMIT 1";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idAlumno");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al resolver idAlumno: " + e.getMessage());
        }
        return null;
    }
}
