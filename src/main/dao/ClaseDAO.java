package main.dao;

import main.database.ConexionDB;
import main.modelo.Clase;
import main.modelo.Curso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClaseDAO {

    // Crear clase
    public boolean agregarClase(Clase clase) {
        String sql = "INSERT INTO clase (idCurso, fecha, titulo, contenido) VALUES (?, ?, ?, ?)\n";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, clase.getCurso().getIdCurso());
            stmt.setDate(2, new java.sql.Date(clase.getFecha().getTime()));
            stmt.setString(3, clase.getTitulo());
            stmt.setString(4, clase.getContenido());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        clase.setIdClase(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar clase: " + e.getMessage());
        }

        return false;
    }

    // Obtener todas las clases de un curso
    public List<Clase> obtenerClasesPorCurso(Curso curso) {
        List<Clase> clases = new ArrayList<>();
        String sql = "SELECT idClase, fecha, titulo, contenido FROM clase WHERE idCurso = ? ORDER BY fecha";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, curso.getIdCurso());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idClase = rs.getInt("idClase");
                    Date fecha = rs.getDate("fecha");
                    String titulo = rs.getString("titulo");
                    String contenido = rs.getString("contenido");

                    clases.add(new Clase(idClase, curso, fecha, titulo, contenido));
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener clases: " + e.getMessage());
        }
        return clases;
    }

    // Obtener una clase por ID
    public Clase obtenerClasePorId(int idClase, Curso curso) {
        String sql = "SELECT fecha, titulo, contenido FROM clase WHERE idClase = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idClase);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date fecha = rs.getDate("fecha");
                    String titulo = rs.getString("titulo");
                    String contenido = rs.getString("contenido");

                    return new Clase(idClase, curso, fecha, titulo, contenido);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener clase: " + e.getMessage());
        }

        return null;
    }

    // Actualizar clase
    public boolean actualizarClase(Clase clase) {
        String sql = "UPDATE clase SET fecha = ?, titulo = ?, contenido = ? WHERE idClase = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(clase.getFecha().getTime()));
            stmt.setString(2, clase.getTitulo());
            stmt.setString(3, clase.getContenido());
            stmt.setInt(4, clase.getIdClase());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar clase: " + e.getMessage());
        }
        return false;
    }

    // Eliminar clase
    public boolean eliminarClase(int idClase) {
        String sql = "DELETE FROM clase WHERE idClase = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idClase);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar clase: " + e.getMessage());
        }
        return false;
    }
}
