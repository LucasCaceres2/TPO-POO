package main.dao;

import main.database.ConexionDB;
import main.modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InscripcionDAO {

    // METODO AUXILIAR: obtener idUsuario por legajo (OK)
    private Integer obtenerIdUsuarioPorLegajo(String legajo) {
        String sql = "SELECT idUsuario FROM alumnos WHERE legajo = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, legajo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("idUsuario");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener idUsuario por legajo: " + e.getMessage());
        }
        return null;
    }

    // --- AGREGAR INSCRIPCIÓN ---
    public boolean agregarInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null || inscripcion.getAlumno() == null || inscripcion.getCurso() == null) {
            System.out.println("⚠️ Datos incompletos de la inscripción.");
            return false;
        }

        // usamos el idUsuario del alumno como idAlumno en la tabla inscripciones
        Integer idAlumno = obtenerIdUsuarioPorLegajo(inscripcion.getAlumno().getLegajo());
        // Si tu Inscripcion ya viene con idUsuario seteado, podrías usar directamente:
        // Integer idAlumno = inscripcion.getAlumno().getIdUsuario();

        if (idAlumno == null) {
            System.out.println("⚠️ Alumno no encontrado por legajo.");
            return false;
        }

        String checkSql  = "SELECT 1 FROM inscripciones WHERE idAlumno = ? AND idCurso = ?";
        String insertSql = "INSERT INTO inscripciones " +
                "(fecha, idAlumno, idCurso, idPago, estadoPago, estadoCurso) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar()) {

            // Verificar si ya está inscripto
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, idAlumno);
                check.setInt(2, inscripcion.getCurso().getIdCurso());
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("⚠️ El alumno ya está inscripto en este curso.");
                        return false;
                    }
                }
            }

            // Insertar inscripción
            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

                java.sql.Date fechaSQL = new java.sql.Date(
                        (inscripcion.getFecha() != null ? inscripcion.getFecha() : new Date()).getTime()
                );

                stmt.setDate(1, fechaSQL);
                stmt.setInt(2, idAlumno);
                stmt.setInt(3, inscripcion.getCurso().getIdCurso());

                // idPago opcional
                if (inscripcion.getPago() != null && inscripcion.getPago().getIdPago() > 0) {
                    stmt.setInt(4, inscripcion.getPago().getIdPago());
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }

                // estadoPago: mapear a ENUM('PENDIENTE', 'PAGADO')
                String estadoPagoBD;
                if (inscripcion.getEstadoPago() == null) {
                    estadoPagoBD = "PENDIENTE";
                } else {
                    switch (inscripcion.getEstadoPago()) {
                        case PAGADO:
                            estadoPagoBD = "PAGADO";
                            break;
                        default:
                            // cualquier otro (pendiente, etc.)
                            estadoPagoBD = "PENDIENTE";
                            break;
                    }
                }
                stmt.setString(5, estadoPagoBD);

                // estadoCurso: ENUM BD ('CURSANDO','APROBADO','DESAPROBADO','DADO_DE_BAJA')
                String estadoCursoBD;
                if (inscripcion.getEstadoCurso() == null) {
                    estadoCursoBD = EstadoCurso.CURSANDO.name();
                } else {
                    estadoCursoBD = inscripcion.getEstadoCurso().name();
                }
                stmt.setString(6, estadoCursoBD);

                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            inscripcion.setIdInscripcion(rs.getInt(1));
                        }
                    }

                    // sumar 1 a la cantidad de inscriptos del curso (si tenés esa columna)
                    String updateCurso = "UPDATE cursos SET inscriptos = inscriptos + 1 WHERE idCurso = ?";
                    try (PreparedStatement up = conn.prepareStatement(updateCurso)) {
                        up.setInt(1, inscripcion.getCurso().getIdCurso());
                        up.executeUpdate();
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

    // --- LISTAR INSCRIPCIONES POR LEGAJO ---
    public List<Inscripcion> listarInscripcionesPorLegajo(String legajo) {
        Integer idAlumno = obtenerIdUsuarioPorLegajo(legajo);
        if (idAlumno == null) return new ArrayList<>();
        return listarInscripcionesPorAlumnoIdUsuario(idAlumno);
    }

    // --- ACTUALIZAR ESTADOS ---
    public boolean actualizarEstadoPago(int idInscripcion, EstadoInscripcion nuevoEstado) {
        String estadoPagoBD;
        switch (nuevoEstado) {
            case PAGADO:
                estadoPagoBD = "PAGADO";
                break;
            default:
                estadoPagoBD = "PENDIENTE";
        }
        return ejecutarUpdate("UPDATE inscripciones SET estadoPago = ? WHERE idInscripcion = ?",
                estadoPagoBD, idInscripcion);
    }

    public boolean actualizarEstadoCurso(int idInscripcion, EstadoCurso nuevoEstado) {
        String estadoCursoBD = nuevoEstado.name();
        return ejecutarUpdate("UPDATE inscripciones SET estadoCurso = ? WHERE idInscripcion = ?",
                estadoCursoBD, idInscripcion);
    }

    private boolean ejecutarUpdate(String sql, String estado, int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estado);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar inscripción: " + e.getMessage());
        }
        return false;
    }

    // --- ELIMINAR INSCRIPCIÓN ---
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

    // --- LISTAR INSCRIPCIONES POR idAlumno ---
    private List<Inscripcion> listarInscripcionesPorAlumnoIdUsuario(int idAlumno) {
        List<Inscripcion> lista = new ArrayList<>();

        String sql = """
            SELECT i.idInscripcion, i.fecha, i.estadoPago, i.estadoCurso,
                   a.idUsuario, a.legajo,
                   u.nombre AS alumnoNombre, u.apellido AS alumnoApellido, u.email AS alumnoEmail,

                   c.idCurso, c.titulo AS cursoTitulo, c.cupoMax, c.contenido,

                   d.idUsuario AS idDocente,
                   ud.nombre AS docenteNombre,
                   ud.apellido AS docenteApellido,

                   p.idPago, p.monto, p.fecha AS fechaPago
            FROM inscripciones i
            JOIN alumnos a   ON i.idAlumno = a.idUsuario
            JOIN usuarios u  ON a.idUsuario = u.idUsuario
            JOIN cursos c    ON i.idCurso = c.idCurso
            JOIN docentes d  ON c.idDocente = d.idUsuario
            JOIN usuarios ud ON d.idUsuario = ud.idUsuario
            LEFT JOIN pagos p ON i.idPago = p.idPago
            WHERE i.idAlumno = ?
            """;

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

                    // Docente del curso
                    Docente docente = new Docente(
                            rs.getInt("idDocente"),
                            rs.getString("docenteNombre"),
                            rs.getString("docenteApellido"),
                            null,
                            null,
                            null
                    );

                    Curso curso = new Curso(
                            rs.getInt("idCurso"),
                            rs.getString("cursoTitulo"),
                            rs.getInt("cupoMax"),
                            docente,
                            null,
                            rs.getString("contenido")
                    );

                    Pago pago = null;
                    int idPago = rs.getInt("idPago");
                    if (!rs.wasNull()) {
                        pago = new Pago(idPago, rs.getDate("fechaPago"), rs.getDouble("monto"), alumno);
                    }

                    // mapear estados igual que ya tenés
                    String estadoPagoBD = rs.getString("estadoPago");
                    EstadoInscripcion estadoPagoEnum =
                            "PAGADO".equalsIgnoreCase(estadoPagoBD)
                                    ? EstadoInscripcion.PAGADO
                                    : EstadoInscripcion.PENDIENTE_PAGO;

                    EstadoCurso estadoCursoEnum =
                            EstadoCurso.valueOf(rs.getString("estadoCurso"));

                    lista.add(new Inscripcion(
                            rs.getInt("idInscripcion"),
                            rs.getDate("fecha"),
                            alumno,
                            curso,
                            pago,
                            estadoPagoEnum,
                            estadoCursoEnum
                    ));
                }
            }

            System.out.println("📘 Total inscripciones por alumno: " + lista.size());

        } catch (SQLException e) {
            System.out.println("❌ Error al listar inscripciones: " + e.getMessage());
        }
        return lista;
    }

    // --- CONTAR INSCRIPTOS POR CURSO ---
    public int contarInscriptosPorCurso(int idCurso) {
        String sql = "SELECT COUNT(*) AS cant FROM inscripciones WHERE idCurso = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCurso);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cant");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al contar inscriptos: " + e.getMessage());
        }
        return 0;
    }
}
