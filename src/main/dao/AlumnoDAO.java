package main.dao;

import main.database.ConexionDB;
import main.modelo.Alumno;
import main.modelo.TipoUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO {

    // 🔹 Crear (INSERT)
    public boolean agregarAlumno(Alumno alumno) {
        if (alumno == null || alumno.getLegajo() == null || alumno.getLegajo().isEmpty()) {
            System.out.println("⚠️ El alumno o su legajo no pueden ser nulos.");
            return false;
        }

        if (alumno.getEmail() == null || !alumno.getEmail().contains("@")) {
            System.out.println("⚠️ El email del alumno no es válido.");
            return false;
        }

        String checkSql = "SELECT 1 FROM alumnos WHERE legajo = ?";
        String insertSql = "INSERT INTO alumnos (legajo, idUsuario) VALUES (?, ?)";

        try (Connection conn = ConexionDB.conectar()) {
            // Evitar duplicados
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, alumno.getLegajo());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    System.out.println("⚠️ Ya existe un alumno con el legajo " + alumno.getLegajo());
                    return false;
                }
            }

            // Insertar
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setString(1, alumno.getLegajo());
                stmt.setInt(2, alumno.getIdUsuario());

                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    System.out.println("✅ Alumno agregado correctamente: " + alumno.getNombre());
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar alumno: " + e.getMessage());
        }

        return false;
    }

    // 🔹 Leer (SELECT por legajo)
    public Alumno obtenerAlumnoPorLegajo(String legajo) {
        if (legajo == null || legajo.isEmpty()) {
            System.out.println("⚠️ El legajo no puede estar vacío.");
            return null;
        }

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
                            null,
                            TipoUsuario.valueOf(rs.getString("tipoUsuario")),
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

    // 🔹 Listar todos
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
                Alumno alumno = new Alumno(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        null,
                        TipoUsuario.valueOf(rs.getString("tipoUsuario")),
                        rs.getString("legajo")
                );
                alumnos.add(alumno);
            }

            System.out.println("📘 Total alumnos cargados: " + alumnos.size());

        } catch (SQLException e) {
            System.out.println("❌ Error al listar alumnos: " + e.getMessage());
        }

        return alumnos;
    }

    // 🔹 Actualizar campo específico del perfil
    public boolean actualizarDatoPerfil(String legajo, String campo, String nuevoValor) {
        if (legajo == null || legajo.isEmpty() || campo == null || campo.isEmpty()) {
            System.out.println("⚠️ Legajo y campo son obligatorios.");
            return false;
        }

        // Validar campos permitidos
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
            } else {
                System.out.println("⚠️ No se encontró alumno con legajo " + legajo);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar " + campo + ": " + e.getMessage());
        }

        return false;
    }

    // 🔹 Eliminar (DELETE)
    public boolean eliminarAlumno(String legajo) {
        if (legajo == null || legajo.isEmpty()) {
            System.out.println("⚠️ El legajo no puede estar vacío.");
            return false;
        }

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
}
