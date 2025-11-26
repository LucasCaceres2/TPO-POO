package main.dao;

import main.database.ConexionDB;
import main.modelo.Area;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

    // Crear área (activa por defecto)
    public boolean agregarArea(Area area) {
        if (area == null || area.getNombre() == null || area.getNombre().isEmpty()) return false;

        String sql = "INSERT INTO area (nombre, activo) VALUES (?, TRUE)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, area.getNombre());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar área: " + e.getMessage());
        }
        return false;
    }

    // Listar TODAS (activas e inactivas)
    public List<Area> listarTodasLasAreas() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT idArea, nombre, activo FROM area";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Area a = new Area(rs.getInt("idArea"), rs.getString("nombre"));
                a.setActivo(rs.getBoolean("activo"));
                areas.add(a);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar áreas: " + e.getMessage());
        }
        return areas;
    }

    // Listar solo activas (para combos)
    public List<Area> listarAreasActivas() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT idArea, nombre FROM area WHERE activo = TRUE";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                areas.add(new Area(rs.getInt("idArea"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar áreas activas: " + e.getMessage());
        }
        return areas;
    }

    public Area obtenerAreaPorNombre(String nombre) {
        String sql = "SELECT idArea, nombre, activo FROM area WHERE LOWER(nombre) = LOWER(?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                // 🚫 Si está desactivada, no permitir usarla
                if (!rs.getBoolean("activo")) {
                    System.out.println("⚠️ El área existe pero está desactivada: " + nombre);
                    return null;
                }

                Area area = new Area(
                        rs.getInt("idArea"),
                        rs.getString("nombre")
                );
                area.setActivo(true);
                return area;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener área por nombre: " + e.getMessage());
        }

        return null;
    }

    // Actualizar nombre
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

    // SOFT DELETE
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
