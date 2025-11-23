package main.dao;

import main.database.ConexionDB;
import main.modelo.Asistencia;
import main.modelo.Clase;
import main.modelo.Inscripcion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {

    public boolean agregarAsistencia(Asistencia asistencia) {
        String sql = "INSERT INTO asistencia (idInscripcion, idClase, presente) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, asistencia.getInscripcion().getIdInscripcion());
            stmt.setInt(2, asistencia.getClase().getIdClase());
            stmt.setBoolean(3, asistencia.isPresente());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        asistencia.setIdAsistencia(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al registrar asistencia: " + e.getMessage());
        }
        return false;
    }

    public Asistencia obtenerAsistencia(Inscripcion inscripcion, Clase clase) {
        String sql = "SELECT idAsistencia, presente FROM asistencia WHERE idInscripcion = ? AND idClase = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inscripcion.getIdInscripcion());
            stmt.setInt(2, clase.getIdClase());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idAsistencia = rs.getInt("idAsistencia");
                    boolean presente = rs.getBoolean("presente");

                    return new Asistencia(idAsistencia, inscripcion, clase, presente);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener asistencia: " + e.getMessage());
        }

        return null; // si no hay asistencia registrada
    }
}
