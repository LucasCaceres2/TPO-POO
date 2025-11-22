package main.dao;

import main.database.ConexionDB;
import main.modelo.Docente;
import main.modelo.TipoUsuario;
import main.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocenteDAO {

    // 🔹 Crear docente
    public boolean agregarDocente(Docente docente) {
        if (docente == null || docente.getMatricula() == null || docente.getMatricula().isEmpty()) {
            System.out.println("⚠️ El docente o su matrícula no pueden ser nulos.");
            return false;
        }

        // Primero se crea el usuario base
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        int idUsuario = usuarioDAO.agregarUsuario(docente);
        if (idUsuario <= 0) return false;
        docente.setIdUsuario(idUsuario);

        String checkSql = "SELECT 1 FROM docente WHERE matricula = ?";
        String insertSql = "INSERT INTO docente (idUsuario, matricula) VALUES (?, ?)";

        try (Connection conn = ConexionDB.conectar()) {

            // Evitar duplicados por matrícula
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, docente.getMatricula());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    System.out.println("⚠️ Ya existe un docente con matrícula " + docente.getMatricula());
                    return false;
                }
            }

            // Insertar docente
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, docente.getIdUsuario());
                stmt.setString(2, docente.getMatricula());

                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    System.out.println("✅ Docente agregado correctamente: " + docente.getNombre());
                    return true;
                }
            }


        } catch (SQLException e) {
            System.out.println("❌ Error al agregar docente: " + e.getMessage());
        }

        return false;
    }

    // 🔹 Obtener docente por matrícula
    public Docente obtenerDocentePorMatricula(String matricula) {
        if (matricula == null || matricula.isEmpty()) return null;

        String sql = """
                SELECT d.idUsuario, d.matricula, u.idUsuario, u.nombre, u.apellido, u.email, u.tipoUsuario
                FROM docente d
                JOIN usuario u ON d.idUsuario = u.idUsuario
                WHERE d.matricula = ?
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, matricula);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Docente(
                            rs.getInt("idUsuario"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            null,
                            rs.getString("matricula")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener docente: " + e.getMessage());
        }

        System.out.println("⚠️ No se encontró docente con matrícula: " + matricula);
        return null;
    }

    // 🔹 Listar todos los docentes
    public List<Docente> listarDocentes() {
        List<Docente> docentes = new ArrayList<>();
        String sql = """
                SELECT d.idUsuario, d.matricula, u.idUsuario, u.nombre, u.apellido, u.email, u.tipoUsuario
                FROM docente d
                JOIN usuario u ON d.idUsuario = u.idUsuario
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Docente docente = new Docente(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        null,
                        rs.getString("matricula")
                );
                docentes.add(docente);
            }

            System.out.println("📘 Total docentes cargados: " + docentes.size());

        } catch (SQLException e) {
            System.out.println("❌ Error al listar docentes: " + e.getMessage());
        }

        return docentes;
    }

    // 🔹 Actualizar datos del docente
    public boolean actualizarDocente(String matricula, String campo, String nuevoValor) {
        if (matricula == null || matricula.isEmpty() || campo == null || campo.isEmpty()) return false;

        // Solo campos permitidos
        List<String> camposPermitidos = List.of("nombre", "apellido", "email", "contrasena");
        if (!camposPermitidos.contains(campo)) {
            System.out.println("⚠️ No se puede modificar el campo '" + campo + "'.");
            return false;
        }

        String sql = String.format("""
                UPDATE usuario u
                JOIN docente d ON u.idUsuario = d.idUsuario
                SET u.%s = ?
                WHERE d.matricula = ?
                """, campo);

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoValor);
            stmt.setString(2, matricula);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ Campo '" + campo + "' actualizado correctamente para matrícula " + matricula);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar docente: " + e.getMessage());
        }

        return false;
    }

    // 🔹 Eliminar docente
    public boolean eliminarDocente(String matricula) {
        if (matricula == null || matricula.isEmpty()) return false;

        String sql = "DELETE FROM docente WHERE matricula = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, matricula);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("🗑️ Docente eliminado correctamente: " + matricula);
                return true;
            } else {
                System.out.println("⚠️ No se encontró docente con matrícula " + matricula);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar docente: " + e.getMessage());
        }

        return false;
    }


    public Docente obtenerDocentePorEmail(String email) {
        if (email == null || email.isEmpty()) return null;

        String sql = """
            SELECT d.idUsuario,
                   d.matricula,
                   u.nombre,
                   u.apellido,
                   u.email
            FROM docente d
            JOIN usuario u ON d.idUsuario = u.idUsuario
            WHERE u.email = ?
            """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Docente(
                            rs.getInt("idUsuario"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            null, // contraseña no se trae
                            rs.getString("matricula")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener docente por email: " + e.getMessage());
        }

        System.out.println("⚠️ No se encontró docente con email: " + email);
        return null;
    }

}
