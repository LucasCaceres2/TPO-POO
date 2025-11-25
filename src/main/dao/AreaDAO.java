package main.dao;

import main.database.ConexionDB;
import main.modelo.Area;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

    // --- Crear área ---
    public boolean agregarArea(Area area) {

        if (area == null || area.getNombre() == null || area.getNombre().isBlank()) {
            System.out.println("⚠️ El área no puede ser nula.");
            return false;
        }

        String checkSql = "SELECT 1 FROM area WHERE LOWER(nombre) = LOWER(?)";
        String insertSql = "INSERT INTO area (nombre, activo) VALUES (?, TRUE)";

        try (Connection conn = ConexionDB.conectar()) {

            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, area.getNombre());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    System.out.println("⚠️ Ya existe el área: " + area.getNombre());
                    return false;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, area.getNombre());
                int filas = ps.executeUpdate();

                if (filas > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            area.setIdArea(rs.getInt(1));
                        }
                    }
                    System.out.println("✅ Área creada: " + area.getNombre());
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar área: " + e.getMessage());
        }

        return false;
    }

    // --- Listar SOLO áreas activas ---
    public List<Area> listarAreasActivas() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT idArea, nombre FROM area WHERE activo = TRUE";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                areas.add(new Area(
                        rs.getInt("idArea"),
                        rs.getString("nombre")
                ));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar áreas activas: " + e.getMessage());
        }

        return areas;
    }

    // --- Listar TODAS (activas e inactivas) ---
    public List<Area> listarTodasLasAreas() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT idArea, nombre, activo FROM area";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Area a = new Area(
                        rs.getInt("idArea"),
                        rs.getString("nombre")
                );
                a.setActivo(rs.getBoolean("activo"));
                areas.add(a);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar áreas: " + e.getMessage());
        }

        return areas;
    }

    // --- Obtener área por nombre ---
    public Area obtenerAreaPorNombre(String nombre) {
        String sql = "SELECT idArea, nombre, activo FROM area WHERE LOWER(nombre) = LOWER(?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Area area = new Area(
                        rs.getInt("idArea"),
                        rs.getString("nombre")
                );
                area.setActivo(rs.getBoolean("activo"));
                return area;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al buscar área: " + e.getMessage());
        }
        return null;
    }

    // --- Actualizar área ---
    public boolean actualizarArea(int idArea, String nuevoNombre) {

        String sql = "UPDATE area SET nombre = ? WHERE idArea = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoNombre);
            stmt.setInt(2, idArea);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar área: " + e.getMessage());
        }
        return false;
    }

    // --- Desactivar área ---
    public boolean desactivarArea(int idArea) {

        String sql = "UPDATE area SET activo = FALSE WHERE idArea = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idArea);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al desactivar área: " + e.getMessage());
        }
        return false;
    }

    // --- Reactivar área ---
    public boolean reactivarArea(int idArea) {

        String sql = "UPDATE area SET activo = TRUE WHERE idArea = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idArea);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al reactivar área: " + e.getMessage());
        }
        return false;
    }
}
