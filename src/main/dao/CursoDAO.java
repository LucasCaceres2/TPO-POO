package main.dao;

import main.database.ConexionDB;
import main.modelo.Curso;
import main.modelo.Docente;
import main.modelo.Area;
import main.modelo.Clase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

public class CursoDAO {

    public boolean agregarCurso(Curso curso) {
        if (curso == null || curso.getTitulo() == null || curso.getTitulo().isEmpty()
                || curso.getDocente() == null || curso.getArea() == null) {
            System.out.println("Datos incompletos del curso");
            return false;
        }

        String checkSql = "SELECT 1 FROM curso WHERE titulo = ? AND idDocente = ? AND idArea = ?";
        String insertSql = "INSERT INTO curso " +
                "(titulo, cupoMax, idDocente, idArea, contenido, cantidadClases, precio) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.conectar()) {
            // Validar duplicado
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, curso.getTitulo());
                check.setInt(2, curso.getDocente().getIdUsuario());
                check.setInt(3, curso.getArea().getIdArea());
                ResultSet rs = check.executeQuery();
                if (rs.next()) {
                    System.out.println("El curso ya existe: " + curso.getTitulo());
                    return false;
                }
            }

            // Insertar
            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, curso.getTitulo());
                stmt.setInt(2, curso.getCupoMax());
                stmt.setInt(3, curso.getDocente().getIdUsuario());
                stmt.setInt(4, curso.getArea().getIdArea());
                stmt.setString(5, curso.getDescripcion());
                stmt.setInt(6, curso.getCantidadClases());
                stmt.setDouble(7, curso.getPrecio());

                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            curso.setIdCurso(rs.getInt(1));
                        }
                    }


                    generarClasesParaCurso(curso);

                    System.out.println(" Curso agregado correctamente: " + curso.getTitulo());
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al agregar curso: " + e.getMessage());
        }

        return false;
    }


    // 🔹 Obtener curso por ID
    public Curso obtenerCursoPorId(int idCurso) {
        String sql = """
            SELECT c.idCurso, c.titulo, c.cupoMax, c.idDocente, c.idArea,
                   c.contenido, c.cantidadClases, c.precio,
                   u.nombre AS docenteNombre, u.apellido AS docenteApellido, u.email AS docenteEmail,
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
                            "MATRICULA"
                    );
                    Area area = new Area(rs.getInt("idArea"), rs.getString("areaNombre"));

                    return new Curso(
                            rs.getInt("idCurso"),
                            rs.getString("titulo"),
                            rs.getInt("cupoMax"),
                            docente,
                            area,
                            rs.getString("contenido"),
                            rs.getInt("cantidadClases"),
                            rs.getDouble("precio")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println(" Error al obtener curso: " + e.getMessage());
        }

        System.out.println(" No se encontró curso con ID: " + idCurso);
        return null;
    }

    // --- OBTENER CURSO POR TÍTULO ---
    public Curso obtenerCursoPorTitulo(String titulo) {
        String sql = """
            SELECT c.idCurso, c.titulo, c.cupoMax, c.contenido, c.cantidadClases, c.precio,
                   d.idUsuario AS idDocente, d.matricula,
                   u.nombre AS docenteNombre, u.apellido AS docenteApellido, u.email AS docenteEmail,
                   a.idArea, a.nombre AS areaNombre
            FROM curso c
            JOIN docente d ON c.idDocente = d.idUsuario
            JOIN usuario u ON d.idUsuario = u.idUsuario
            JOIN area a ON c.idArea = a.idArea
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
                            rs.getString("contenido"),
                            rs.getInt("cantidadClases"),
                            rs.getDouble("precio")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(" Error al obtener curso por título: " + e.getMessage());
        }
        return null;
    }

    // 🔹 Listar todos los cursos
    public List<Curso> listarCursos() {
        List<Curso> cursos = new ArrayList<>();
        String sql = """
                SELECT c.idCurso, c.titulo, c.cupoMax, c.idDocente, c.idArea,
                       c.contenido, c.cantidadClases, c.precio,
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
                        "MATRICULA" // opcional
                );
                Area area = new Area(rs.getInt("idArea"), rs.getString("areaNombre"));

                Curso curso = new Curso(
                        rs.getInt("idCurso"),
                        rs.getString("titulo"),
                        rs.getInt("cupoMax"),
                        docente,
                        area,
                        rs.getString("contenido"),
                        rs.getInt("cantidadClases"),
                        rs.getDouble("precio")
                );
                cursos.add(curso);
            }

            System.out.println(" Total cursos cargados: " + cursos.size());

        } catch (SQLException e) {
            System.out.println(" Error al listar cursos: " + e.getMessage());
        }

        return cursos;
    }

    //  Listar cursos por docente
    public List<Curso> listarCursosPorDocente(int idDocente) {
        List<Curso> cursos = new ArrayList<>();
        String sql = """
                SELECT c.idCurso, c.titulo, c.cupoMax, c.idDocente, c.idArea,
                       c.contenido, c.cantidadClases, c.precio,
                       d.matricula,
                       u.nombre AS docenteNombre, u.apellido AS docenteApellido, u.email AS docenteEmail,
                       a.nombre AS areaNombre
                FROM curso c
                JOIN docente d ON c.idDocente = d.idUsuario
                JOIN usuario u ON d.idUsuario = u.idUsuario
                JOIN area a ON c.idArea = a.idArea
                WHERE c.idDocente = ?
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

                    Area area = new Area(rs.getInt("idArea"), rs.getString("areaNombre"));

                    Curso curso = new Curso(
                            rs.getInt("idCurso"),
                            rs.getString("titulo"),
                            rs.getInt("cupoMax"),
                            docente,
                            area,
                            rs.getString("contenido"),
                            rs.getInt("cantidadClases"),
                            rs.getDouble("precio")
                    );
                    cursos.add(curso);
                }
            }

            System.out.println(" Total cursos del docente: " + cursos.size());

        } catch (SQLException e) {
            System.out.println(" Error al listar cursos por docente: " + e.getMessage());
        }

        return cursos;
    }

    // 🔹 Actualizar curso
    public boolean actualizarCurso(int idCurso, String campo, String nuevoValor) {
        if (campo == null || campo.isEmpty() || nuevoValor == null || nuevoValor.isEmpty())
            return false;

        List<String> camposPermitidos = List.of("titulo", "contenido", "precio");
        if (!camposPermitidos.contains(campo)) {
            System.out.println(" No se puede modificar el campo '" + campo + "'.");
            return false;
        }

        String sql = String.format("UPDATE curso SET %s = ? WHERE idCurso = ?", campo);

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if ("precio".equals(campo)) {
                // convertir texto a número
                double valor = Double.parseDouble(nuevoValor);
                stmt.setDouble(1, valor);
            } else {
                stmt.setString(1, nuevoValor);
            }

            stmt.setInt(2, idCurso);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                System.out.println(" Curso actualizado correctamente.");
                return true;
            }

        } catch (SQLException e) {
            System.out.println(" Error al actualizar curso: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println(" Valor de precio inválido: " + nuevoValor);
        }

        return false;
    }


    // 🔹 Eliminar curso
    public boolean eliminarCurso(int idCurso) {
        String sql = "DELETE FROM curso WHERE idCurso = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println(" Curso eliminado correctamente.");
                return true;
            } else {
                System.out.println(" No se encontró curso con ID " + idCurso);
            }

        } catch (SQLException e) {
            System.out.println(" Error al eliminar curso: " + e.getMessage());
        }

        return false;
    }
    // Genera automáticamente las clases de un curso nuevo
    private void generarClasesParaCurso(Curso curso) {
        if (curso == null) return;
        int total = curso.getCantidadClases();
        if (total <= 0) return;

        ClaseDAO claseDAO = new ClaseDAO();

        // Podés cambiar la lógica de fechas: hoy + 1 semana, etc.
        Calendar cal = Calendar.getInstance();  // arranca hoy

        for (int i = 1; i <= total; i++) {
            Date fecha = cal.getTime();

            String tituloClase = curso.getTitulo() + " - Clase " + i;
            String contenido = "";  // vacío por ahora, se puede editar después

            Clase clase = new Clase(curso, fecha, tituloClase, contenido);

            boolean ok = claseDAO.agregarClase(clase);
            if (!ok) {
                System.out.println("No se pudo crear la clase " + i +
                        " para el curso " + curso.getTitulo());
            }

            // siguiente clase una semana después (ajustalo si querés otra frecuencia)
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        }
    }

}
