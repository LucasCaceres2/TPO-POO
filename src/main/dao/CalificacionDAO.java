package main.dao;

import main.database.ConexionDB;
import main.modelo.Calificacion;
import main.modelo.Inscripcion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalificacionDAO {

    public boolean agregarCalificacion(Calificacion calificacion) {
        String sql = "INSERT INTO calificaciones (idInscripcion, tipo, nota, fecha) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, calificacion.getInscripcion().getIdInscripcion());
            stmt.setString(2, calificacion.getTipo());
            stmt.setDouble(3, calificacion.getNota());
            stmt.setDate(4, new java.sql.Date(calificacion.getFecha().getTime()));

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        calificacion.setIdCalificacion(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al registrar calificación: " + e.getMessage());
        }
        return false;
    }

    public List<Calificacion> obtenerCalificacionesPorInscripcion(Inscripcion inscripcion) {
        List<Calificacion> lista = new ArrayList<>();
        String sql = "SELECT idCalificacion, tipo, nota, fecha FROM calificaciones WHERE idInscripcion = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inscripcion.getIdInscripcion());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("idCalificacion");
                    String tipo = rs.getString("tipo");
                    double nota = rs.getDouble("nota");
                    java.util.Date fecha = rs.getDate("fecha");
                    lista.add(new Calificacion(id, inscripcion, tipo, nota, fecha));
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar calificaciones: " + e.getMessage());
        }

        return lista;
    }
}
