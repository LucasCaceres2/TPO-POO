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
}
