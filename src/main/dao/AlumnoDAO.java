package main.dao;

import main.database.ConexionDB;
import main.modelo.Alumno;
import main.modelo.TipoUsuario;
import main.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO {

    // 🔹 Crear alumno (primero crea usuario)
    public boolean agregarAlumno(Alumno alumno) {
        if (alumno == null || alumno.getLegajo() == null || alumno.getLegajo().isEmpty()) {
            System.out.println("⚠️ El alumno o su legajo no pueden ser nulos.");
            return false;
        }

        // 1️⃣ Crear usuario base
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        int idUsuario = usuarioDAO.agregarUsuario(alumno); // devuelve idUsuario generado
        if (idUsuario <= 0) return false;
        alumno.setIdUsuario(idUsuario);

        // 2️⃣ Evitar duplicados por legajo
        String checkSql = "SELECT 1 FROM alumno WHERE legajo = ?";
        String insertSql = "INSERT INTO alumno (idUsuario, legajo) VALUES (?, ?)";

        try (Connection conn = ConexionDB.conectar()) {

            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, alumno.getLegajo());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    System.out.println("⚠️ Ya existe un alumno con el legajo " + alumno.getLegajo());
                    return false;
                }
            }

            // 3️⃣ Insertar alumno en la tabla
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, alumno.getIdUsuario());
                stmt.setString(2, alumno.getLegajo());

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

    // 🔹 Obtener alumno por legajo
    public Alumno obtenerAlumnoPorLegajo(String legajo) {
        if (legajo == null || legajo.isEmpty()) return null;

        String sql = """
                SELECT a.legajo, u.idUsuario, u.nombre, u.apellido, u.email, u.tipoUsuario
                FROM alumno a
                JOIN usuario u ON a.idUsuario = u.idUsuario
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
                FROM alumno a
                JOIN usuario u ON a.idUsuario = u.idUsuario
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
    public boolean actualizarDatoPerfil(String legajo, String campo, String nuevoValor) {
        if (legajo == null || campo == null || legajo.isEmpty() || campo.isEmpty()) return false;

        List<String> camposPermitidos = List.of("nombre", "apellido", "email", "contrasena");
        if (!camposPermitidos.contains(campo)) {
            System.out.println("⚠️ No se puede modificar el campo '" + campo + "'.");
            return false;
        }

        String sql = String.format("""
                UPDATE usuario u
                JOIN alumno a ON u.idUsuario = a.idUsuario
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

        String sql = "DELETE FROM alumno WHERE legajo = ?";

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

    public Alumno obtenerAlumnoPorEmail(String email) {
        String sql = """
            SELECT u.idUsuario,
                   u.nombre,
                   u.apellido,
                   u.email,
                   u.contrasena,
                   a.legajo
            FROM usuario u
            JOIN alumno a ON u.idUsuario = a.idUsuario
            WHERE u.email = ?
            """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idUsuario = rs.getInt("idUsuario");
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    String emailDb = rs.getString("email");
                    String contrasena = rs.getString("contrasena");
                    String legajo = rs.getString("legajo");

                    // usamos tu constructor existente
                    return new Alumno(idUsuario, nombre, apellido, emailDb, contrasena, legajo);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener alumno por email: " + e.getMessage());
        }
        return null;
    }


    public boolean actualizarAlumno(Alumno alumno) {
        if (alumno == null || alumno.getIdUsuario() <= 0) {
            System.out.println("⚠️ Alumno inválido para actualización.");
            return false;
        }

        String sql = """
            UPDATE usuario
            SET nombre = ?, apellido = ?, email = ?
            WHERE idUsuario = ?
            """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido());
            ps.setString(3, alumno.getEmail());
            ps.setInt(4, alumno.getIdUsuario());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ Perfil de alumno actualizado (idUsuario=" + alumno.getIdUsuario() + ")");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar alumno: " + e.getMessage());
        }

        return false;
    }
}
