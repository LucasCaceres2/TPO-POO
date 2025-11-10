package main.dao;

import main.database.ConexionDB;
import main.modelo.Alumno;
import main.modelo.TipoUsuario;
import main.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO {

    // 🔹 Crear alumno (genera legajo automáticamente)
    public boolean agregarAlumno(Alumno alumno) {
        if (alumno == null) {
            System.out.println("⚠️ El alumno no puede ser nulo.");
            return false;
        }

        // 1️⃣ Crear usuario base
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        int idUsuario = usuarioDAO.agregarUsuario(alumno); // devuelve idUsuario generado
        if (idUsuario <= 0) return false;
        alumno.setIdUsuario(idUsuario);

        // 2️⃣ Generar legajo automáticamente
        alumno.setLegajo(generarLegajo());

        // 3️⃣ Insertar alumno en la tabla
        String insertSql = "INSERT INTO alumnos (idUsuario, legajo) VALUES (?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            stmt.setInt(1, alumno.getIdUsuario());
            stmt.setString(2, alumno.getLegajo());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ Alumno agregado correctamente: " + alumno.getNombre()
                        + " (Legajo: " + alumno.getLegajo() + ")");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar alumno: " + e.getMessage());
        }

        return false;
    }

    // 🔹 Obtener alumno por legajo
    public Alumno obtenerAlumnoPorLegajo(String legajo) {
        if (legajo == null || legajo.isEmpty()) return null;

        String sql = """
                SELECT a.legajo, u.idUsuario, u.nombre, u.apellido, u.email, u.tipoUsuario
                FROM alumnos a
                JOIN usuarios u ON a.idUsuario = u.idUsuario
                WHERE a.legajo = ?
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, legajo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Alumno(
                            rs.getInt("idUsuario"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            (String) null, // contraseña no se devuelve
                            rs.getString("legajo")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener alumno: " + e.getMessage());
        }

        System.out.println("⚠️ No se encontró un alumno con legajo: " + legajo);
        return null;
    }

    // 🔹 Listar todos los alumnos
    public List<Alumno> listarAlumnos() {
        List<Alumno> alumnos = new ArrayList<>();
        String sql = """
                SELECT a.legajo, u.idUsuario, u.nombre, u.apellido, u.email, u.tipoUsuario
                FROM alumnos a
                JOIN usuarios u ON a.idUsuario = u.idUsuario
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                alumnos.add(new Alumno(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        (String) null,
                        rs.getString("legajo")
                ));
            }

            System.out.println("📘 Total alumnos cargados: " + alumnos.size());

        } catch (SQLException e) {
            System.out.println("❌ Error al listar alumnos: " + e.getMessage());
        }

        return alumnos;
    }

    // 🔹 Actualizar dato de perfil
    public boolean actualizarAlumno(String legajo, String campo, String nuevoValor) {
        if (legajo == null || campo == null || legajo.isEmpty() || campo.isEmpty()) return false;

        List<String> camposPermitidos = List.of("nombre", "apellido", "email", "contrasena");
        if (!camposPermitidos.contains(campo)) {
            System.out.println("⚠️ No se puede modificar el campo '" + campo + "'.");
            return false;
        }

        String sql = String.format("""
                UPDATE usuarios u
                JOIN alumnos a ON u.idUsuario = a.idUsuario
                SET u.%s = ?
                WHERE a.legajo = ?
                """, campo);

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoValor);
            stmt.setString(2, legajo);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ Campo '" + campo + "' actualizado correctamente para legajo " + legajo);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar: " + e.getMessage());
        }

        return false;
    }

    // 🔹 Eliminar alumno
    public boolean eliminarAlumno(String legajo) {
        if (legajo == null || legajo.isEmpty()) return false;

        String sql = "DELETE FROM alumnos WHERE legajo = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, legajo);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("🗑️ Alumno eliminado correctamente: " + legajo);
                return true;
            } else {
                System.out.println("⚠️ No se encontró alumno con legajo " + legajo);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar alumno: " + e.getMessage());
        }

        return false;
    }

    private String generarLegajo() {
        String sql = "SELECT MAX(legajo) FROM alumnos WHERE legajo LIKE 'A%'";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next() && rs.getString(1) != null) {
                String ultimoLegajo = rs.getString(1);
                int numero = Integer.parseInt(ultimoLegajo.substring(1)) + 1;
                return "A" + String.format("%04d", numero);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al generar legajo: " + e.getMessage());
        }

        // Si no hay ninguno todavía
        return "A0001";
    }

    private int contarAlumnos() {
        String sql = "SELECT COUNT(*) FROM alumnos";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al contar alumnos: " + e.getMessage());
        }
        return 0;
    }

    public String obtenerLegajoPorEmail(String email) {
        if (email == null || email.isEmpty()) return null;

        String sql = """
            SELECT a.legajo
            FROM alumnos a
            JOIN usuarios u ON a.idUsuario = u.idUsuario
            WHERE u.email = ?
            """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("legajo");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener legajo por email: " + e.getMessage());
        }

        return null;
    }

    // AlumnoDAO
    public Alumno obtenerAlumnoPorEmail(String email) {
        if (email == null || email.isEmpty()) return null;

        String sql = """
        SELECT a.legajo, u.idUsuario, u.nombre, u.apellido, u.email
        FROM alumnos a
        JOIN usuarios u ON a.idUsuario = u.idUsuario
        WHERE u.email = ?
        """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
