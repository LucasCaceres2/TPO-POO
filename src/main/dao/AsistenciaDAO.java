package main.dao;

import main.database.ConexionDB;
import main.modelo.Asistencia;
import main.modelo.Inscripcion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {

    public boolean agregarAsistencia(Asistencia asistencia) {
        String sql = "INSERT INTO asistencias (idInscripcion, fecha, presente) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, asistencia.getInscripcion().getIdInscripcion());
            stmt.setDate(2, new java.sql.Date(asistencia.getFecha().getTime()));
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

    public boolean actualizarAsistencia(int idAsistencia, boolean presente) {
        String sql = "UPDATE asistencia SET presente = ? WHERE idAsistencia = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, presente);
            stmt.setInt(2, idAsistencia);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar asistencia: " + e.getMessage());
        }
        return false;
    }

    public List<Asistencia> obtenerAsistenciasPorInscripcion(Inscripcion inscripcion) {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT idAsistencia, fecha, presente FROM asistencias WHERE idInscripcion = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inscripcion.getIdInscripcion());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("idAsistencia");
                    java.util.Date fecha = rs.getDate("fecha");
                    boolean presente = rs.getBoolean("presente");
                    lista.add(new Asistencia(id, inscripcion, fecha, presente));
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar asistencias: " + e.getMessage());
        }

        return lista;
    }
}
