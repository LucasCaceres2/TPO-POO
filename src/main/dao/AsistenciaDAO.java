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
        String sql = "INSERT INTO asistencia (idInscripcion, fecha, presente) VALUES (?, ?, ?)";

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

    public List<Asistencia> obtenerAsistenciasPorInscripcion(Inscripcion inscripcion) {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT idAsistencia, idClase, presente FROM asistencia WHERE idInscripcion = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inscripcion.getIdInscripcion());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    int idAsistencia = rs.getInt("idAsistencia");
                    int idClase = rs.getInt("idClase");
                    boolean presente = rs.getBoolean("presente");

                    // Necesitamos una clase mínima con solo idClase
                    Clase clase = new Clase(idClase, null, null, null);

                    lista.add(new Asistencia(idAsistencia, inscripcion, clase, presente));
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar asistencias: " + e.getMessage());
        }

        return lista;
    }
}
