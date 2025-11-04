package main.dao;

import main.database.ConexionDB;
import main.modelo.Curso;
import main.modelo.Docente;
import main.modelo.Area;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    public boolean agregarCurso(Curso curso) {
        if (curso == null || curso.getTitulo() == null || curso.getTitulo().isEmpty()
                || curso.getDocente() == null || curso.getArea() == null) {
            System.out.println("⚠️ Datos incompletos del curso");
            return false;
        }

        String checkSql = "SELECT 1 FROM cursos WHERE titulo = ? AND idDocente = ? AND idArea = ?";
        String insertSql = "INSERT INTO cursos (titulo, cupoMax, idDocente, idArea, contenido) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar()) {
            // Validar duplicado
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, curso.getTitulo());
                check.setInt(2, curso.getDocente().getIdUsuario());
                check.setInt(3, curso.getArea().getIdArea());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    System.out.println("⚠️ El curso ya existe: " + curso.getTitulo());
                    return false;
                }
            }

            // Insertar
            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, curso.getTitulo());
                stmt.setInt(2, curso.getCupoMax());
                stmt.setInt(3, curso.getDocente().getIdUsuario());
                stmt.setInt(4, curso.getArea().getIdArea());
                stmt.setString(5, curso.getContenido());

                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            curso.setIdCurso(rs.getInt(1));
                        }
                    }
                    System.out.println("✅ Curso agregado correctamente: " + curso.getTitulo());
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al agregar curso: " + e.getMessage());
        }

        return false;
    }


    // 🔹 Obtener curso por ID
    public Curso obtenerCursoPorId(int idCurso) {
        String sql = """
                SELECT c.idCurso, c.titulo, c.cupoMax, c.idDocente, c.idArea, c.contenido,
                       u.nombre AS docenteNombre, u.apellido AS docenteApellido, u.email AS docenteEmail,
                       a.nombre AS areaNombre
                FROM cursos c
                JOIN docentes d ON c.idDocente = d.idUsuario
                JOIN usuarios u ON d.idUsuario = u.idUsuario
                JOIN areas a ON c.idArea = a.idArea
                WHERE c.idCurso = ?
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Docente docente = new Docente(
                            rs.getInt("idDocente"),
                            rs.getString("docenteNombre"),
                            rs.getString("docenteApellido"),
                            rs.getString("docenteEmail"),
                            null,
                            "MATRICULA" // Opcional, si necesitas matricula, tendrías que sumarlo al SELECT
                    );
                    Area area = new Area(rs.getInt("idArea"), rs.getString("areaNombre"));

                    return new Curso(
                            rs.getInt("idCurso"),
                            rs.getString("titulo"),
                            rs.getInt("cupoMax"),
                            docente,
                            area,
                            rs.getString("contenido")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener curso: " + e.getMessage());
        }

        System.out.println("⚠️ No se encontró curso con ID: " + idCurso);
        return null;
    }

    // --- OBTENER CURSO POR TÍTULO ---
    public Curso obtenerCursoPorTitulo(String titulo) {
        String sql = """
            SELECT c.idCurso, c.titulo, c.cupoMax, c.contenido,
                   d.idUsuario AS idDocente, d.matricula,
                   u.nombre AS docenteNombre, u.apellido AS docenteApellido, u.email AS docenteEmail,
                   a.idArea, a.nombre AS areaNombre
            FROM cursos c
            JOIN docentes d ON c.idDocente = d.idUsuario
            JOIN usuarios u ON d.idUsuario = u.idUsuario
            JOIN areas a ON c.idArea = a.idArea
            WHERE LOWER(c.titulo) = LOWER(?)
            """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, titulo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Docente docente = new Docente(
                            rs.getInt("idDocente"),
                            rs.getString("docenteNombre"),
                            rs.getString("docenteApellido"),
                            rs.getString("docenteEmail"),
                            null,
                            rs.getString("matricula")
                    );

                    Area area = new Area(rs.getInt("idArea"), rs.getString("areaNombre"));

                    return new Curso(
                            rs.getInt("idCurso"),
                            rs.getString("titulo"),
                            rs.getInt("cupoMax"),
                            docente,
                            area,
                            rs.getString("contenido")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener curso por título: " + e.getMessage());
        }
        return null;
    }

    // 🔹 Listar todos los cursos
    public List<Curso> listarCursos() {
        List<Curso> cursos = new ArrayList<>();
        String sql = """
                SELECT c.idCurso, c.titulo, c.cupoMax, c.idDocente, c.idArea, c.contenido,
                       u.nombre AS docenteNombre, u.apellido AS docenteApellido, u.email AS docenteEmail,
                       a.nombre AS areaNombre
                FROM cursos c
                JOIN docentes d ON c.idDocente = d.idUsuario
                JOIN usuarios u ON d.idUsuario = u.idUsuario
                JOIN areas a ON c.idArea = a.idArea
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Docente docente = new Docente(
                        rs.getInt("idDocente"),
                        rs.getString("docenteNombre"),
                        rs.getString("docenteApellido"),
                        rs.getString("docenteEmail"),
                        null,
                        "MATRICULA" // opcional
                );
                Area area = new Area(rs.getInt("idArea"), rs.getString("areaNombre"));

                Curso curso = new Curso(
                        rs.getInt("idCurso"),
                        rs.getString("titulo"),
                        rs.getInt("cupoMax"),
                        docente,
                        area,
                        rs.getString("contenido")
                );
                cursos.add(curso);
            }

            System.out.println("📘 Total cursos cargados: " + cursos.size());

        } catch (SQLException e) {
            System.out.println("❌ Error al listar cursos: " + e.getMessage());
        }

        return cursos;
    }

    // 🔹 Actualizar curso
    public boolean actualizarCurso(int idCurso, String campo, String nuevoValor) {
        if (campo == null || campo.isEmpty() || nuevoValor == null || nuevoValor.isEmpty()) return false;

        List<String> camposPermitidos = List.of("titulo", "contenido");
        if (!camposPermitidos.contains(campo)) {
            System.out.println("⚠️ No se puede modificar el campo '" + campo + "'.");
            return false;
        }

        String sql = String.format("UPDATE cursos SET %s = ? WHERE idCurso = ?", campo);

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoValor);
            stmt.setInt(2, idCurso);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ Curso actualizado correctamente.");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar curso: " + e.getMessage());
        }

        return false;
    }

    // 🔹 Eliminar curso
    public boolean eliminarCurso(int idCurso) {
        String sql = "DELETE FROM cursos WHERE idCurso = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("🗑️ Curso eliminado correctamente.");
                return true;
            } else {
                System.out.println("⚠️ No se encontró curso con ID " + idCurso);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar curso: " + e.getMessage());
        }

        return false;
    }
}
