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

    // ===========================
    //     MÉTODOS DE NEGOCIO
    // ===========================

    // --- Registrar nuevo alumno ---
    public boolean registrarAlumno(String nombre, String apellido, String email, String password, String legajo) {
        Alumno alumno = new Alumno(nombre, apellido, email, password, legajo);
        return alumnoDAO.agregarAlumno(alumno);
    }

    // --- Registrar nuevo docente ---
    public boolean registrarDocente(String nombre, String apellido, String email, String password, String matricula) {
        Docente docente = new Docente(nombre, apellido, email, password, matricula);
        return docenteDAO.agregarDocente(docente);
    }

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

    // --- Mostrar cursos de un alumno (reemplaza a verCursosInscritos()) ---
    public void mostrarCursosDeAlumno(String legajo) {
        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajo);
        if (alumno == null) {
            System.out.println("❌ Alumno no encontrado.");
            return;
        }

        alumno.cargarInscripciones();
        List<String> cursos = alumno.obtenerTitulosCursosInscritos();

        if (cursos.isEmpty()) {
            System.out.println(alumno.getNombre() + " no tiene cursos inscritos.");
        } else {
            System.out.println("📘 Cursos de " + alumno.getNombre() + ":");
            for (String titulo : cursos) {
                System.out.println("  - " + titulo);
            }
        }
    }

    // --- Inscribir con validación previa  ---
    public boolean inscribirAlumnoEnCursoConValidacion(String legajoAlumno, String tituloCurso) {
        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajoAlumno);
        Curso curso = cursoDAO.obtenerCursoPorTitulo(tituloCurso);

        if (alumno == null || curso == null) {
            System.out.println("⚠️ No se encontró alumno o curso.");
            return false;
        }

        // 🔹 Cargar inscripciones para validar
        alumno.cargarInscripciones();

        // 🔹 Validar antes de intentar inscribir
        if (!alumno.puedeInscribirseA(curso)) {
            System.out.println("❌ El alumno no puede inscribirse (sin cupo o ya inscrito).");
            return false;
        }

        Inscripcion inscripcion = new Inscripcion(alumno, curso);
        boolean exito = inscripcionDAO.agregarInscripcion(inscripcion);

        if (exito) {
            System.out.println("✅ " + alumno.getNombre() + " inscrito correctamente en: " + curso.getTitulo());
        }

        return exito;
    }
}
