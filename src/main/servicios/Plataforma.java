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
    // Campos en Plataforma:
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final CalificacionDAO calificacionDAO = new CalificacionDAO();

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
    public boolean crearCurso(String titulo, int cupoMax, String matriculaDocente, String nombreArea, String descripcion, int cantidadClases) {
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

        Curso curso = new Curso(0, titulo, cupoMax, docente, area, descripcion, cantidadClases);
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
        if (monto <= 0) {
            System.out.println("⚠️ El monto del pago debe ser mayor a 0.");
            return false;
        }

        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajoAlumno);
        if (alumno == null) {
            System.out.println("⚠️ Alumno no encontrado con legajo: " + legajoAlumno);
            return false;
        }

        // Lógica de negocio: crear entidad consistente
        Pago pago = new Pago(monto, alumno);

        // Persistencia delegada al DAO
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

    public boolean tomarAsistencia(String legajoAlumno, int idCurso, java.util.Date fecha, boolean presente) {
        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajoAlumno);
        if (alumno == null) {
            System.out.println("⚠️ Alumno no encontrado.");
            return false;
        }

        Curso curso = cursoDAO.obtenerCursoPorId(idCurso);
        if (curso == null) {
            System.out.println("⚠️ Curso no encontrado.");
            return false;
        }

        Inscripcion inscripcion = inscripcionDAO.obtenerInscripcion(alumno, curso);
        if (inscripcion == null) {
            System.out.println("⚠️ El alumno no está inscripto en este curso.");
            return false;
        }

        Asistencia asistencia = new Asistencia(
                inscripcion,
                (fecha != null) ? fecha : new java.util.Date(),
                presente
        );

        return asistenciaDAO.agregarAsistencia(asistencia);
    }

    public boolean registrarCalificacion(String legajoAlumno, int idCurso, String tipo, double nota) {
        if (nota < 0 || nota > 10) {
            System.out.println("⚠️ La nota debe estar entre 0 y 10.");
            return false;
        }

        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajoAlumno);
        if (alumno == null) {
            System.out.println("⚠️ Alumno no encontrado.");
            return false;
        }

        Curso curso = cursoDAO.obtenerCursoPorId(idCurso);
        if (curso == null) {
            System.out.println("⚠️ Curso no encontrado.");
            return false;
        }

        Inscripcion inscripcion = inscripcionDAO.obtenerInscripcion(alumno, curso);
        if (inscripcion == null) {
            System.out.println("⚠️ El alumno no está inscripto en este curso.");
            return false;
        }

        Calificacion calificacion = new Calificacion(inscripcion, tipo, nota);

        return calificacionDAO.agregarCalificacion(calificacion);
    }
    public double calcularPorcentajeAsistencia(String legajoAlumno, int idCurso) {
        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajoAlumno);
        if (alumno == null) {
            System.out.println("⚠️ Alumno no encontrado.");
            return 0.0;
        }

        Curso curso = cursoDAO.obtenerCursoPorId(idCurso);
        if (curso == null) {
            System.out.println("⚠️ Curso no encontrado.");
            return 0.0;
        }

        Inscripcion inscripcion = inscripcionDAO.obtenerInscripcion(alumno, curso);
        if (inscripcion == null) {
            System.out.println("⚠️ El alumno no está inscripto en este curso.");
            return 0.0;
        }

        List<Asistencia> asistencias = asistenciaDAO.obtenerAsistenciasPorInscripcion(inscripcion);
        if (asistencias == null || asistencias.isEmpty()) {
            return 0.0;
        }

        int presentes = 0;
        for (int i = 0; i < asistencias.size(); i++) {
            Asistencia a = asistencias.get(i);
            if (a.isPresente()) {
                presentes++;
            }
        }

        int totalReferencia;

        if (curso.getCantidadClases() > 0) {
            // Usamos la cantidad planificada del curso
            totalReferencia = curso.getCantidadClases();
        } else {
            // Si no está seteado, usamos la cantidad de asistencias registradas
            totalReferencia = asistencias.size();
        }

        if (totalReferencia == 0) {
            return 0.0;
        }

        return (presentes * 100.0) / totalReferencia;
    }

    public boolean inscribirAlumnoEnCurso(String emailAlumno, int idCurso) {
        // Supongo que tenés este método; si no, se puede crear en AlumnoDAO
        Alumno alumno = alumnoDAO.obtenerAlumnoPorEmail(emailAlumno);
        if (alumno == null) {
            System.out.println("⚠️ Alumno no encontrado por email: " + emailAlumno);
            return false;
        }

        Curso curso = cursoDAO.obtenerCursoPorId(idCurso);
        if (curso == null) {
            System.out.println("⚠️ Curso no encontrado con id: " + idCurso);
            return false;
        }

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
