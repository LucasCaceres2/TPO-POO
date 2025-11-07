package main.controlador;

import main.database.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControladorLogin {

    /**
     * Intenta iniciar sesión con email y contraseña.
     *
     * @return
     *  - "ALUMNO"  si las credenciales son correctas y es alumno
     *  - "DOCENTE" si las credenciales son correctas y es docente
     *  - "ERROR_VACIO" si falta email o contraseña
     *  - "ERROR_CREDENCIALES" si no coincide nada en la BD
     *  - "ERROR_BD" si hubo un problema con la conexión/consulta
     */
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
                    // tipoUsuario: 'ALUMNO' o 'DOCENTE'
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
