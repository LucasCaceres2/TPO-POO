package main.dao;

import main.database.ConexionDB;
import main.modelo.TipoUsuario;
import main.modelo.Usuario;

import java.sql.*;

public class UsuarioDAO {

    // 🔹 Crear usuario
    public int agregarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getEmail() == null || !usuario.getEmail().contains("@")) {
            System.out.println("⚠️ Usuario o email inválido.");
            return -1;
        }

        String checkSql = "SELECT 1 FROM usuarios WHERE email = ?";
        String insertSql = "INSERT INTO usuarios (nombre, apellido, email, contrasena, tipoUsuario) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar()) {
            // Evitar duplicados por email
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, usuario.getEmail());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    System.out.println("⚠️ Ya existe un usuario con email " + usuario.getEmail());
                    return -1;
                }
            }

            // Insertar usuario
            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, usuario.getNombre());
                stmt.setString(2, usuario.getApellido());
                stmt.setString(3, usuario.getEmail());
                stmt.setString(4, usuario.getContrasena());
                stmt.setString(5, usuario.getTipoUsuario().name());

                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idUsuario = generatedKeys.getInt(1);
                            System.out.println("✅ Usuario creado con id: " + idUsuario);
                            return idUsuario;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar usuario: " + e.getMessage());
        }

        return -1;
    }

    // 🔹 Leer usuario por id
    public Usuario obtenerUsuarioPorId(int idUsuario) {
        String sql = "SELECT * FROM usuarios WHERE idUsuario = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("contrasena"),
                            TipoUsuario.valueOf(rs.getString("tipoUsuario"))
                    ) {
                        {
                            this.idUsuario = idUsuario;
                        }
                    };
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener usuario: " + e.getMessage());
        }

        System.out.println("⚠️ No se encontró usuario con id: " + idUsuario);
        return null;
    }

    // 🔹 Leer usuario por email
    public Usuario obtenerUsuarioPorEmail(String email) {
        if (email == null || email.isEmpty()) return null;

        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getString("contrasena"),
                            TipoUsuario.valueOf(rs.getString("tipoUsuario"))
                    ) {
                        {
                            this.idUsuario = rs.getInt("idUsuario");
                        }
                    };
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener usuario: " + e.getMessage());
        }

        return null;
    }

    // 🔹 Actualizar datos del usuario
    public boolean actualizarUsuario(int idUsuario, String campo, String nuevoValor) {
        if (idUsuario <= 0 || campo == null || campo.isEmpty()) return false;

        // Solo campos permitidos
        if (!campo.equals("nombre") && !campo.equals("apellido") && !campo.equals("email") && !campo.equals("contrasena")) {
            System.out.println("⚠️ Campo no permitido: " + campo);
            return false;
        }

        String sql = "UPDATE usuarios SET " + campo + " = ? WHERE idUsuario = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoValor);
            stmt.setInt(2, idUsuario);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ Usuario actualizado correctamente.");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar usuario: " + e.getMessage());
        }

        return false;
    }

    // 🔹 Eliminar usuario
    public boolean eliminarUsuario(int idUsuario) {
        if (idUsuario <= 0) return false;

        String sql = "DELETE FROM usuarios WHERE idUsuario = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("🗑️ Usuario eliminado correctamente.");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar usuario: " + e.getMessage());
        }

        return false;
    }


}
