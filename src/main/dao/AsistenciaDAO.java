package main.dao;

import main.database.ConexionDB;
import main.modelo.Asistencia;
import main.modelo.Clase;
import main.modelo.Inscripcion;
import main.modelo.Curso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {

    public boolean agregarAsistencia(Asistencia asistencia) {
        String sql = "INSERT INTO asistencia (idInscripcion, idClase, presente) VALUES (?, ?, ?)";

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
            System.out.println(" Error al registrar asistencia: " + e.getMessage());
        }
        return false;
    }

    public Asistencia obtenerAsistencia(Inscripcion inscripcion, Clase clase) {
        String sql = "SELECT idAsistencia, presente FROM asistencia WHERE idInscripcion = ? AND idClase = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inscripcion.getIdInscripcion());
            stmt.setInt(2, clase.getIdClase());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idAsistencia = rs.getInt("idAsistencia");
                    boolean presente = rs.getBoolean("presente");

                    return new Asistencia(idAsistencia, inscripcion, clase, presente);
                }
            }

        } catch (SQLException e) {
            System.out.println(" Error al obtener asistencia: " + e.getMessage());
        }

        return null; // si no hay asistencia registrada
    }
    public List<Object[]> listarCursosCursandoPorAlumno(String emailAlumno) {
        List<Object[]> lista = new ArrayList<>();

        String sql = """
                SELECT DISTINCT
                    cu.idCurso,
                    cu.titulo AS cursoTitulo,
                    uDoc.nombre  AS docenteNombre,
                    uDoc.apellido AS docenteApellido
                FROM inscripcion i
                JOIN alumno al       ON i.idAlumno   = al.idUsuario
                JOIN usuario uAl     ON al.idUsuario = uAl.idUsuario
                JOIN curso cu        ON i.idCurso    = cu.idCurso
                JOIN docente d       ON cu.idDocente = d.idUsuario
                JOIN usuario uDoc    ON d.idUsuario  = uDoc.idUsuario
                WHERE uAl.email = ?
                  AND i.estadoCurso = 'CURSANDO'
                ORDER BY cu.titulo
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emailAlumno);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idCurso = rs.getInt("idCurso");
                    String tituloCurso = rs.getString("cursoTitulo");
                    String docente = rs.getString("docenteNombre") + " " + rs.getString("docenteApellido");

                    Object[] fila = new Object[] { idCurso, tituloCurso, docente };
                    lista.add(fila);
                }
            }

        } catch (SQLException e) {
            System.out.println(" Error al listar cursos CURSANDO por alumno: " + e.getMessage());
        }

        return lista;
    }
    public List<Object[]> listarAsistenciasPorAlumnoYCurso(String emailAlumno, int idCurso) {
        List<Object[]> lista = new ArrayList<>();

        String sql = """
                SELECT
                    a.idAsistencia,
                    c.titulo  AS claseTitulo,
                    c.fecha   AS fechaClase,
                    a.presente
                FROM asistencia a
                JOIN inscripcion i  ON a.idInscripcion = i.idInscripcion
                JOIN alumno al      ON i.idAlumno      = al.idUsuario
                JOIN usuario u      ON al.idUsuario    = u.idUsuario
                JOIN clase c        ON a.idClase       = c.idClase
                JOIN curso cu       ON c.idCurso       = cu.idCurso
                WHERE u.email = ?
                  AND cu.idCurso = ?
                ORDER BY c.fecha
                """;

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emailAlumno);
            stmt.setInt(2, idCurso);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[] {
                            rs.getInt("idAsistencia"),
                            rs.getString("claseTitulo"),
                            rs.getTimestamp("fechaClase"),
                            rs.getBoolean("presente")
                    };
                    lista.add(fila);
                }
            }

        } catch (SQLException e) {
            System.out.println(" Error al listar asistencias por alumno y curso: " + e.getMessage());
        }

        return lista;
    }



}
