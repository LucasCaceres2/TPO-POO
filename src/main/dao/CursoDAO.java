package main.dao;

import main.database.ConexionDB;
import main.modelo.Curso;
import main.modelo.Docente;
import main.modelo.Area;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    // ============================
    // CREAR CURSO
    // ============================
    public boolean agregarCurso(Curso curso) {
        if (curso == null || curso.getTitulo() == null || curso.getTitulo().isEmpty()
                || curso.getDocente() == null || curso.getArea() == null) {
            System.out.println("⚠️ Datos incompletos del curso");
            return false;
        }

        String checkSql = "SELECT 1 FROM curso WHERE titulo = ? AND idDocente = ? AND idArea = ?";
        String insertSql = "INSERT INTO curso (titulo, cupoMax, idDocente, idArea, contenido, cantidadClases, activo) VALUES (?, ?, ?, ?, ?, ?, TRUE)";

        try (Connection conn = ConexionDB.conectar()) {

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

            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, curso.getTitulo());
                stmt.setInt(2, curso.getCupoMax());
                stmt.setInt(3, curso.getDocente().getIdUsuario());
                stmt.setInt(4, curso.getArea().getIdArea());
                stmt.setString(5, curso.getDescripcion());
                stmt.setInt(6, curso.getCantidadClases());

                if (stmt.executeUpdate() > 0) {
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


    // ============================
    // LISTAR CURSOS ACTIVOS (para alumnos)
    // ============================
    public List<Curso> listarCursosActivos() {
        List<Curso> cursos = new ArrayList<>();

        String sql = """
            SELECT c.idCurso, c.titulo, c.cupoMax, c.idDocente, c.idArea,
                   c.contenido, c.cantidadClases, c.activo,
                   u.nombre AS docenteNombre, u.apellido AS docenteApellido, u.email AS docenteEmail,
                   a.nombre AS areaNombre
            FROM curso c
            JOIN docente d ON c.idDocente = d.idUsuario
            JOIN usuario u ON d.idUsuario = u.idUsuario
            JOIN area a ON c.idArea = a.idArea
            WHERE c.activo = TRUE
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
                        "MATRICULA"
                );

                Area area = new Area(rs.getInt("idArea"), rs.getString("areaNombre"));

                Curso curso = new Curso(
                        rs.getInt("idCurso"),
                        rs.getString("titulo"),
                        rs.getInt("cupoMax"),
                        docente,
                        area,
                        rs.getString("contenido"),
                        rs.getInt("cantidadClases")
                );

                curso.setActivo(true);
                cursos.add(curso);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar cursos activos: " + e.getMessage());
        }
        return cursos;
    }

    // ============================
    // LISTAR CURSOS ACTIVOS (para alumnos)
    // ============================

    public List<Curso> listarCursosPorDocente(int idDocente) {
        List<Curso> cursos = new ArrayList<>();

        String sql = """
        SELECT c.idCurso, c.titulo, c.cupoMax, c.idDocente, c.idArea,
               c.contenido, c.cantidadClases, c.activo,
               d.matricula,
               u.nombre AS docenteNombre, u.apellido AS docenteApellido, u.email AS docenteEmail,
               a.nombre AS areaNombre
        FROM curso c
        JOIN docente d ON c.idDocente = d.idUsuario
        JOIN usuario u ON d.idUsuario = u.idUsuario
        JOIN area a ON c.idArea = a.idArea
        WHERE c.idDocente = ? AND c.activo = TRUE
        """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDocente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    Docente docente = new Docente(
                            rs.getInt("idDocente"),
                            rs.getString("docenteNombre"),
                            rs.getString("docenteApellido"),
                            rs.getString("docenteEmail"),
                            null,
                            rs.getString("matricula")
                    );

                    Area area = new Area(
                            rs.getInt("idArea"),
                            rs.getString("areaNombre")
                    );

                    Curso curso = new Curso(
                            rs.getInt("idCurso"),
                            rs.getString("titulo"),
                            rs.getInt("cupoMax"),
                            docente,
                            area,
                            rs.getString("contenido"),
                            rs.getInt("cantidadClases")
                    );

                    curso.setActivo(rs.getBoolean("activo"));
                    cursos.add(curso);
                }
            }

            System.out.println("📘 Cursos activos del docente: " + cursos.size());

        } catch (SQLException e) {
            System.out.println("❌ Error al listar cursos por docente: " + e.getMessage());
        }

        return cursos;
    }

    // ============================
    // LISTAR TODOS LOS CURSOS (para ADMIN)
    // ============================
    public List<Curso> listarTodosLosCursos() {
        List<Curso> cursos = new ArrayList<>();

        String sql = """
            SELECT c.idCurso, c.titulo, c.cupoMax, c.idDocente, c.idArea,
                   c.contenido, c.cantidadClases, c.activo,
                   u.nombre AS docenteNombre, u.apellido AS docenteApellido, u.email AS docenteEmail,
                   a.nombre AS areaNombre
            FROM curso c
            JOIN docente d ON c.idDocente = d.idUsuario
            JOIN usuario u ON d.idUsuario = u.idUsuario
            JOIN area a ON c.idArea = a.idArea
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
                        "MATRICULA"
                );

                Area area = new Area(rs.getInt("idArea"), rs.getString("areaNombre"));

                Curso curso = new Curso(
                        rs.getInt("idCurso"),
                        rs.getString("titulo"),
                        rs.getInt("cupoMax"),
                        docente,
                        area,
                        rs.getString("contenido"),
                        rs.getInt("cantidadClases")
                );

                curso.setActivo(rs.getBoolean("activo"));
                cursos.add(curso);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar todos los cursos: " + e.getMessage());
        }
        return cursos;
    }


    // ============================
    // OBTENER CURSO ACTIVO POR TÍTULO
    // ============================
    public Curso obtenerCursoActivoPorTitulo(String titulo) {
        String sql = """
            SELECT c.idCurso, c.titulo, c.cupoMax, c.contenido, c.cantidadClases,
                   d.idUsuario AS idDocente, u.nombre, u.apellido, u.email,
                   a.idArea, a.nombre AS areaNombre
            FROM curso c
            JOIN docente d ON c.idDocente = d.idUsuario
            JOIN usuario u ON d.idUsuario = u.idUsuario
            JOIN area a ON c.idArea = a.idArea
            WHERE LOWER(c.titulo) = LOWER(?) AND c.activo = TRUE
            """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, titulo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Docente docente = new Docente(
                            rs.getInt("idDocente"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            null,
                            "MATRICULA"
                    );

                    Area area = new Area(rs.getInt("idArea"), rs.getString("areaNombre"));

                    Curso curso = new Curso(
                            rs.getInt("idCurso"),
                            rs.getString("titulo"),
                            rs.getInt("cupoMax"),
                            docente,
                            area,
                            rs.getString("contenido"),
                            rs.getInt("cantidadClases")
                    );

                    curso.setActivo(true);
                    return curso;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener curso: " + e.getMessage());
        }
        return null;
    }

    // ============================
    // OBTENER CURSO ACTIVO POR ID
    // ============================

    public Curso obtenerCursoPorId(int idCurso) {

        String sql = """
        SELECT c.idCurso, c.titulo, c.cupoMax, c.idDocente, c.idArea,
               c.contenido, c.cantidadClases, c.activo,
               u.nombre AS docenteNombre, u.apellido AS docenteApellido, u.email AS docenteEmail,
               d.matricula,
               a.nombre AS areaNombre
        FROM curso c
        JOIN docente d ON c.idDocente = d.idUsuario
        JOIN usuario u ON d.idUsuario = u.idUsuario
        JOIN area a ON c.idArea = a.idArea
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
                            rs.getString("matricula")
                    );

                    Area area = new Area(
                            rs.getInt("idArea"),
                            rs.getString("areaNombre")
                    );

                    Curso curso = new Curso(
                            rs.getInt("idCurso"),
                            rs.getString("titulo"),
                            rs.getInt("cupoMax"),
                            docente,
                            area,
                            rs.getString("contenido"),
                            rs.getInt("cantidadClases")
                    );

                    curso.setActivo(rs.getBoolean("activo"));
                    return curso;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener curso por ID: " + e.getMessage());
        }

        System.out.println("⚠️ No se encontró curso con ID: " + idCurso);
        return null;
    }

    // ============================
    // SOFT DELETE
    // ============================
    public boolean desactivarCurso(int idCurso) {
        String sql = "UPDATE curso SET activo = FALSE WHERE idCurso = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al desactivar curso: " + e.getMessage());
        }
        return false;
    }

    public boolean reactivarCurso(int idCurso) {
        String sql = "UPDATE curso SET activo = TRUE WHERE idCurso = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al reactivar curso: " + e.getMessage());
        }
        return false;
    }
}
