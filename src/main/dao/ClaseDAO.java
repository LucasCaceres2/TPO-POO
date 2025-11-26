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
        String sql = "INSERT INTO clase (idCurso, fecha, titulo, contenido) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, clase.getCurso().getIdCurso());

            if (clase.getFecha() != null) {
                stmt.setDate(2, new java.sql.Date(clase.getFecha().getTime()));
            } else {
                stmt.setNull(2, Types.DATE);
            }

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

    public void crearClasesAutomaticas(Curso curso) {

        if (curso == null || curso.getCantidadClases() <= 0) {
            System.out.println("⚠️ Curso inválido para crear clases");
            return;
        }

        String sql = "INSERT INTO clase (idCurso, fecha, titulo, contenido) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 1; i <= curso.getCantidadClases(); i++) {

                stmt.setInt(1, curso.getIdCurso());
                stmt.setDate(2, null); // ✅ sin fecha por ahora
                stmt.setString(3, "Clase " + i); // ✅ solo Clase 1, Clase 2...
                stmt.setString(4, null);

                stmt.executeUpdate();
            }

            System.out.println("✅ Clases creadas automáticamente para el curso " + curso.getTitulo());

        } catch (SQLException e) {
            System.out.println("❌ Error al crear clases automáticas: " + e.getMessage());
        }
    }

    // Actualizar clase (versión segura)
    public boolean actualizarClase(Clase clase) {
        String sql = "UPDATE clase SET fecha = ?, titulo = ?, contenido = ? WHERE idClase = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (clase.getFecha() != null) {
                stmt.setDate(1, new java.sql.Date(clase.getFecha().getTime()));
            } else {
                stmt.setNull(1, Types.DATE);
            }

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

    public void sincronizarClases(Curso curso) {
        int idCurso = curso.getIdCurso();
        int actuales = contarClasesPorCurso(idCurso);
        int nuevas = curso.getCantidadClases();

        // Si hay que agregar clases
        if (nuevas > actuales) {
            for (int i = actuales + 1; i <= nuevas; i++) {
                Clase clase = new Clase(0, curso, null, "Clase " + i, null);
                agregarClase(clase);
            }
            System.out.println("✅ Se agregaron " + (nuevas - actuales) + " clases nuevas al curso " + curso.getTitulo());
        }

        // Si hay que eliminar clases
        if (nuevas < actuales) {
            int aEliminar = actuales - nuevas;

            int eliminadas = eliminarClasesSinAsistenciaHasta(idCurso, aEliminar);

            if (eliminadas < aEliminar) {
                System.out.println("⚠️ Solo se pudieron eliminar " + eliminadas +
                        " clases sin asistencia. Quedan clases con asistencia que no se pueden borrar.");
            } else {
                System.out.println("✅ Se eliminaron " + eliminadas + " clases del curso " + curso.getTitulo());
            }
        }
    }

    public int contarClasesPorCurso(int idCurso) {
        String sql = "SELECT COUNT(*) FROM clase WHERE idCurso = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("❌ Error al contar clases: " + e.getMessage());
        }
        return 0;
    }

    public void eliminarUltimasClases(int idCurso, int cantidad) {
        String sql = """
        DELETE FROM clase
        WHERE idClase IN (
            SELECT idClase FROM clase
            WHERE idCurso = ?
            ORDER BY idClase DESC
            LIMIT ?
        )
    """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            stmt.setInt(2, cantidad);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar clases: " + e.getMessage());
        }
    }

    public int contarClasesSinAsistencia(int idCurso) {
        String sql = """
        SELECT COUNT(*) 
        FROM clase c
        WHERE c.idCurso = ?
          AND NOT EXISTS (
              SELECT 1 FROM asistencia a
              WHERE a.idClase = c.idClase
          )
    """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("❌ Error al contar clases sin asistencia: " + e.getMessage());
        }
        return 0;
    }

    public int eliminarClasesSinAsistenciaHasta(int idCurso, int cantidad) {
        String sql = """
        DELETE FROM clase
        WHERE idClase IN (
            SELECT idClase FROM (
                SELECT c.idClase
                FROM clase c
                WHERE c.idCurso = ?
                  AND NOT EXISTS (
                      SELECT 1 FROM asistencia a
                      WHERE a.idClase = c.idClase
                  )
                ORDER BY c.idClase DESC
                LIMIT ?
            ) AS sub
        )
    """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            stmt.setInt(2, cantidad);

            return stmt.executeUpdate(); // devuelve cuántas eliminó

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar clases sin asistencia: " + e.getMessage());
        }
        return 0;
    }

}
