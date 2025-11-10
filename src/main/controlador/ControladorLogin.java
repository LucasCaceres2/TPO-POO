package main.controlador;

import main.database.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControladorLogin {

    public String login(String email, String contrasena) {
        if (email == null || email.trim().isEmpty()
                || contrasena == null || contrasena.trim().isEmpty()) {
            return "ERROR_VACIO";
        }

        String sql = "SELECT tipoUsuario FROM usuarios WHERE email = ? AND contrasena = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            ps.setString(2, contrasena.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tipoUsuario");
                } else {
                    return "ERROR_CREDENCIALES";
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR_BD";
        }
    }
}
