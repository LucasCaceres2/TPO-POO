package main.controlador;

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
    private ClaseDAO claseDAO = new ClaseDAO();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final CalificacionDAO calificacionDAO = new CalificacionDAO();

    // --- Registrar nuevo alumno ---
    public boolean registrarAlumno(String nombre, String apellido, String email, String password) {
        Alumno alumno = new Alumno(nombre, apellido, email, password, null);
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

    public boolean tomarAsistencia(String legajoAlumno, int idCurso, Clase clase, boolean presente) {
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
                clase,
                presente
        );

        return asistenciaDAO.agregarAsistencia(asistencia);
    }

    public boolean registrarCalificacion(String legajoAlumno, int idCurso, TipoEvaluacion tipo, double nota) {
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

        // 🔹 Obtenemos todas las clases del curso
        List<Clase> clases = claseDAO.obtenerClasesPorCurso(curso);
        if (clases.isEmpty()) return 0.0;

        int presentes = 0;
        int total = clases.size(); // total de clases planificadas

        for (Clase clase : clases) {
            // Obtenemos asistencia del alumno para esta clase
            Asistencia asistencia = asistenciaDAO.obtenerAsistencia(inscripcion, clase);
            if (asistencia != null && asistencia.isPresente()) {
                presentes++;
            }
        }

        if (total == 0) return 0.0;

        return (presentes * 100.0) / total;
    }

    public boolean inscribirAlumnoEnCurso(String emailAlumno, int idCurso) {
        // Supongo que tenés este metodo; si no, se puede crear en AlumnoDAO
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

        // Reusamos el metodo existente que trabaja con legajo
        return inscripcionDAO.listarInscripcionesPorLegajo(alumno.getLegajo());
    }

    public void ajustarCantidadClases(Curso curso, int nuevaCantidad) {
        List<Clase> clasesActuales = curso.getClases();

        if (nuevaCantidad > curso.getCantidadClases()) {
            // agregar nuevas clases
            for (int i = curso.getCantidadClases(); i < nuevaCantidad; i++) {
                Clase nueva = new Clase(curso, null, "Clase " + (i+1), "Contenido inicial");
                claseDAO.agregarClase(nueva); // persistimos en BD
                clasesActuales.add(nueva);     // agregamos al objeto en memoria
            }
        } else if (nuevaCantidad < curso.getCantidadClases()) {
            // eliminar clases extra
            List<Clase> aEliminar = clasesActuales.subList(nuevaCantidad, clasesActuales.size());
            for (Clase c : aEliminar) {
                claseDAO.eliminarClase(c.getIdClase());
            }
            aEliminar.clear();
        }

        curso.setCantidadClases(nuevaCantidad);
    }
}
