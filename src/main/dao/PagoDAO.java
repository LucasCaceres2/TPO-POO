package main.dao;

import main.database.ConexionDB;
import main.modelo.Alumno;
import main.modelo.Pago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    // Inserta un pago. El objeto Pago debe contener un Alumno con idUsuario.
    public boolean agregarPago(Pago pago) {
        if (pago == null || pago.getAlumno() == null) {
            System.out.println("⚠️ Datos incompletos del pago.");
            return false;
        }

        String sql = "INSERT INTO pagos (fecha, monto, idUsuario) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            java.sql.Date fechaSQL = new java.sql.Date(
                    (pago.getFecha() != null ? pago.getFecha() : new java.util.Date()).getTime()
            );
            stmt.setDate(1, fechaSQL);
            stmt.setDouble(2, pago.getMonto());
            stmt.setInt(3, pago.getAlumno().getIdUsuario());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pago.setIdPago(rs.getInt(1));
                    }
                }
                System.out.println("✅ Pago registrado correctamente.");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar pago: " + e.getMessage());
        }
        return false;
    }

    // Obtener pago por idPago
    public Pago obtenerPagoPorId(int idPago) {
        String sql = """
                SELECT p.idPago, p.fecha, p.monto, p.idUsuario,
                       a.legajo, u.nombre, u.apellido, u.email
                FROM pagos p
                JOIN alumnos a ON p.idUsuario = a.idUsuario
                JOIN usuarios u ON a.idUsuario = u.idUsuario
                WHERE p.idPago = ?
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPago);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Alumno alumno = construirAlumnoDesdeResultSetAlumno(rs);
                    return new Pago(
                            rs.getInt("idPago"),
                            rs.getDate("fecha"),
                            rs.getDouble("monto"),
                            alumno
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener pago: " + e.getMessage());
        }
        System.out.println("⚠️ No se encontró pago con id: " + idPago);
        return null;
    }

    // Listar pagos de un alumno (recibe idUsuario)
    public List<Pago> listarPagosPorAlumnoIdUsuario(int idUsuario) {
        List<Pago> pagos = new ArrayList<>();

        String sql = """
                SELECT p.idPago, p.fecha, p.monto, p.idUsuario,
                       a.legajo, u.nombre, u.apellido, u.email
                FROM pagos p
                JOIN alumnos a ON p.idUsuario = a.idUsuario
                JOIN usuarios u ON a.idUsuario = u.idUsuario
                WHERE p.idUsuario = ?
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Alumno alumno = construirAlumnoDesdeResultSetAlumno(rs);
                    pagos.add(new Pago(
                            rs.getInt("idPago"),
                            rs.getDate("fecha"),
                            rs.getDouble("monto"),
                            alumno
                    ));
                }
            }

            System.out.println("📘 Total pagos cargados: " + pagos.size());
        } catch (SQLException e) {
            System.out.println("❌ Error al listar pagos: " + e.getMessage());
        }

        return pagos;
    }

    // Actualizar monto de pago
    public boolean actualizarMontoPago(int idPago, double nuevoMonto) {
        String sql = "UPDATE pagos SET monto = ? WHERE idPago = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, nuevoMonto);
            stmt.setInt(2, idPago);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar pago: " + e.getMessage());
        }
        return false;
    }

    // Eliminar pago
    public boolean eliminarPago(int idPago) {
        String sql = "DELETE FROM pagos WHERE idPago = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPago);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar pago: " + e.getMessage());
        }
        return false;
    }

    private Alumno construirAlumnoDesdeResultSetAlumno(ResultSet rs) throws SQLException {
        return new Alumno(
                rs.getInt("idUsuario"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("email"),
                null,
                rs.getString("legajo")
        );
    }
}
