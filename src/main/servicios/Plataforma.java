package main.servicios;

import main.dao.*;
import main.modelo.*;

import java.util.List;

public class Plataforma {

    private AlumnoDAO alumnoDAO = new AlumnoDAO();
    private DocenteDAO docenteDAO = new DocenteDAO();
    private CursoDAO cursoDAO = new CursoDAO();
    private InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private PagoDAO pagoDAO = new PagoDAO();
    private AreaDAO areaDAO = new AreaDAO();

    // ================== REGISTRO DE ALUMNO ==================
    public boolean registrarAlumno(String nombre,
                                   String apellido,
                                   String email,
                                   String contrasena,
                                   String legajo) {

        try {
            // Validaciones mínimas de negocio (además de las del ControladorRegistro)
            if (nombre == null || nombre.isBlank()) return false;
            if (apellido == null || apellido.isBlank()) return false;
            if (email == null || !email.contains("@")) return false;
            if (contrasena == null || contrasena.isBlank()) return false;
            if (legajo == null || legajo.isBlank()) return false;

            // Crear objeto de dominio
            Alumno alumno = new Alumno(nombre, apellido, email, contrasena, legajo);

            // Guardar en BD usando el DAO
            return alumnoDAO.agregarAlumno(alumno);

        } catch (Exception e) {
            System.out.println("❌ Error al registrar alumno en Plataforma: " + e.getMessage());
            return false;
        }
    }

    // ================== REGISTRO DE DOCENTE ==================
    public boolean registrarDocente(String nombre,
                                    String apellido,
                                    String email,
                                    String contrasena,
                                    String matricula) {

        try {
            if (nombre == null || nombre.isBlank()) return false;
            if (apellido == null || apellido.isBlank()) return false;
            if (email == null || !email.contains("@")) return false;
            if (contrasena == null || contrasena.isBlank()) return false;
            if (matricula == null || matricula.isBlank()) return false;

            Docente docente = new Docente(nombre, apellido, email, contrasena, matricula);

            return docenteDAO.agregarDocente(docente);

        } catch (Exception e) {
            System.out.println("❌ Error al registrar docente en Plataforma: " + e.getMessage());
            return false;
        }
    }

    // (dejá el resto de métodos que ya tenías para cursos, inscripciones, etc.)



// --- Crear nueva área ---
    public boolean crearArea(String nombreArea) {
        Area area = new Area(0, nombreArea);
        return areaDAO.agregarArea(area);
    }

    // --- Crear curso nuevo ---
    public boolean crearCurso(String titulo, int duracionHoras, String matriculaDocente, String nombreArea, String descripcion) {
        Docente docente = docenteDAO.obtenerDocentePorMatricula(matriculaDocente);
        if (docente == null) {
            System.out.println("⚠️ No se encontró el docente con matrícula: " + matriculaDocente);
            return false;
        }

        Area area = areaDAO.obtenerAreaPorNombre(nombreArea);
        if (area == null) {
            System.out.println("⚠️ No se encontró el área con nombre: " + nombreArea);
            return false;
        }

        Curso curso = new Curso(0, titulo, duracionHoras, docente, area, descripcion);
        return cursoDAO.agregarCurso(curso);
    }

    // --- Inscribir alumno en curso ---
    public boolean inscribirAlumnoEnCurso(String legajoAlumno, String tituloCurso) {
        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajoAlumno);
        Curso curso = cursoDAO.obtenerCursoPorTitulo(tituloCurso);

        if (alumno == null || curso == null) {
            System.out.println("⚠️ No se encontró alumno o curso.");
            return false;
        }

        Inscripcion inscripcion = new Inscripcion(alumno, curso);
        return inscripcionDAO.agregarInscripcion(inscripcion);
    }

    // --- Registrar pago ---
    public boolean registrarPago(String legajoAlumno, double monto) {
        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajoAlumno);
        if (alumno == null) {
            System.out.println("⚠️ Alumno no encontrado.");
            return false;
        }

        Pago pago = new Pago(0, new java.sql.Date(System.currentTimeMillis()), monto, alumno);
        return pagoDAO.agregarPago(pago);
    }

    // --- Listar inscripciones por legajo ---
    public List<Inscripcion> obtenerInscripcionesDeAlumno(String legajoAlumno) {
        return inscripcionDAO.listarInscripcionesPorLegajo(legajoAlumno);
    }

    // --- Listar cursos disponibles ---
    public List<Curso> listarCursos() {
        return cursoDAO.listarCursos();
    }


    // --- Alias: obtener cursos disponibles para el alumno (por ahora todos) ---
    public List<Curso> obtenerCursosDisponibles() {
        return cursoDAO.listarCursos(); // si después tenés estado, filtrás acá
    }

    // --- Inscribir alumno en curso usando email + idCurso (desde la tabla) ---
    public boolean inscribirAlumnoEnCurso(String emailAlumno, int idCurso) {
        // 1) Buscar alumno por email
        Alumno alumno = alumnoDAO.obtenerAlumnoPorEmail(emailAlumno);
        if (alumno == null) {
            System.out.println("⚠️ No se encontró alumno con email: " + emailAlumno);
            return false;
        }

        // 2) Buscar curso por id
        Curso curso = cursoDAO.obtenerCursoPorId(idCurso);
        if (curso == null) {
            System.out.println("⚠️ No se encontró curso con id: " + idCurso);
            return false;
        }

        // 3) Crear inscripción (evitá duplicados en el DAO si querés)
        Inscripcion inscripcion = new Inscripcion(alumno, curso);
        return inscripcionDAO.agregarInscripcion(inscripcion);
    }

    public List<Inscripcion> obtenerInscripcionesDeAlumnoPorEmail(String emailAlumno) {
        if (emailAlumno == null || emailAlumno.isBlank()) {
            return List.of();
        }

        // Necesitamos el alumno para conocer su legajo
        Alumno alumno = alumnoDAO.obtenerAlumnoPorEmail(emailAlumno);
        if (alumno == null) {
            System.out.println("⚠️ No se encontró alumno con email: " + emailAlumno);
            return List.of();
        }

        // Reusamos el método existente que trabaja con legajo
        return inscripcionDAO.listarInscripcionesPorLegajo(alumno.getLegajo());
    }

}
