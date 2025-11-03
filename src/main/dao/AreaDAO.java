package main.dao;

import main.database.ConexionDB;
import main.modelo.Area;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

    // 🔹 Crear área
    public boolean agregarArea(Area area) {
        if (area == null || area.getNombre() == null || area.getNombre().isEmpty()) {
            System.out.println("⚠️ El área no puede ser nula ni vacía.");
            return false;
        }

        String checkSql = "SELECT 1 FROM areas WHERE nombre = ?";
        String insertSql = "INSERT INTO areas (nombre) VALUES (?)";

        try (Connection conn = ConexionDB.conectar()) {

            // Evitar duplicados por nombre
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, area.getNombre());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    System.out.println("⚠️ Ya existe un área con nombre: " + area.getNombre());
                    return false;
                }
            }

            // Insertar área
            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, area.getNombre());
                int filas = stmt.executeUpdate();

                if (filas > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            area.setIdArea(rs.getInt(1));
                        }
                    }
                    System.out.println("✅ Área agregada correctamente: " + area.getNombre());
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar área: " + e.getMessage());
        }

        return false;
    }

    // 🔹 Obtener área por ID
    public Area obtenerAreaPorId(int idArea) {
        String sql = "SELECT idArea, nombre FROM areas WHERE idArea = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idArea);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Area(rs.getInt("idArea"), rs.getString("nombre"));
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener área: " + e.getMessage());
        }

        System.out.println("⚠️ No se encontró un área con ID: " + idArea);
        return null;
    }

    // 🔹 Listar todas las áreas
    public List<Area> listarAreas() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT idArea, nombre FROM areas";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                areas.add(new Area(rs.getInt("idArea"), rs.getString("nombre")));
            }

            System.out.println("📘 Total áreas cargadas: " + areas.size());

        } catch (SQLException e) {
            System.out.println("❌ Error al listar áreas: " + e.getMessage());
        }

        return areas;
    }

    // 🔹 Actualizar área
    public boolean actualizarArea(int idArea, String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.isEmpty()) return false;

        String sql = "UPDATE areas SET nombre = ? WHERE idArea = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoNombre);
            stmt.setInt(2, idArea);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ Área actualizada correctamente: " + nuevoNombre);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar área: " + e.getMessage());
        }

        return false;
    }

    // 🔹 Eliminar área
    public boolean eliminarArea(int idArea) {
        String sql = "DELETE FROM areas WHERE idArea = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idArea);
            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("🗑️ Área eliminada correctamente: " + idArea);
                return true;
            } else {
                System.out.println("⚠️ No se encontró área con ID " + idArea);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar área: " + e.getMessage());
        }

        return false;
    }
}
