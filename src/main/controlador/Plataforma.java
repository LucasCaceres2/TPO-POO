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

    // --- Registrar nuevo alumno (legajo autogenerado desde DAO) ---
    public boolean registrarAlumno(String nombre, String apellido, String email, String password) {
        Alumno alumno = new Alumno(nombre, apellido, email, password, null);
        return alumnoDAO.agregarAlumno(alumno);
    }

    // --- Registrar nuevo docente (solo por admin) ---
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
        if (docente == null) return false;

        Area area = areaDAO.obtenerAreaPorNombre(nombreArea);
        if (area == null) return false;

        Curso curso = new Curso(0, titulo, cupoMax, docente, area, descripcion, cantidadClases);

        boolean creado = cursoDAO.agregarCurso(curso);

        if (creado) {
            System.out.println("Curso creado con ID: " + curso.getIdCurso());
            claseDAO.crearClasesAutomaticas(curso);
        }

        return creado;
    }

    // ================== CURSOS ==================

    // --- Listar cursos disponibles (solo activos) ---
    public List<Curso> listarCursos() {
        return cursoDAO.listarCursosActivos();
    }

    // --- Listar todos los cursos (admin) ---
    public List<Curso> listarTodosLosCursos() {
        return cursoDAO.listarTodosLosCursos();
    }

    public boolean desactivarCurso(int idCurso) {
        return cursoDAO.desactivarCurso(idCurso);
    }

    public boolean reactivarCurso(int idCurso) {
        return cursoDAO.reactivarCurso(idCurso);
    }

    public boolean modificarCurso(Curso curso) {

        Curso cursoActual = cursoDAO.obtenerCursoPorId(curso.getIdCurso());
        if (cursoActual == null) return false;

        int idCurso = curso.getIdCurso();
        int actuales = claseDAO.contarClasesPorCurso(idCurso);
        int nuevas = curso.getCantidadClases();

        // Caso 1: subir cantidad de clases → siempre ok
        if (nuevas > actuales) {
            boolean actualizado = cursoDAO.actualizarCursoCompleto(curso);
            if (actualizado) {
                claseDAO.sincronizarClases(curso);
            }
            return actualizado;
        }

        // Caso 2: misma cantidad → solo actualiza datos del curso
        if (nuevas == actuales) {
            return cursoDAO.actualizarCursoCompleto(curso);
        }

        // Caso 3: bajar cantidad de clases → modo seguro
        // Queremos bajar de 'actuales' a 'nuevas'
        int aEliminar = actuales - nuevas;

        // ¿Cuántas clases se pueden borrar realmente?
        int eliminables = claseDAO.contarClasesSinAsistencia(idCurso);

        if (eliminables < aEliminar) {
            System.out.println("⚠️ No se puede reducir la cantidad de clases a " + nuevas +
                    " porque hay clases con asistencia registrada. Mínimo posible: " +
                    (actuales - eliminables));
            return false; // rechazamos la modificación
        }

        // Si llegamos acá, sabemos que se puede
        boolean actualizado = cursoDAO.actualizarCursoCompleto(curso);
        if (actualizado) {
            claseDAO.sincronizarClases(curso);
        }
        return actualizado;
    }

    // ================== CLASES ==================

    public List<Clase> obtenerClasesPorCurso(Curso curso) {
        return claseDAO.obtenerClasesPorCurso(curso);
    }

    public boolean agregarNuevaClase(Clase clase) {
        return claseDAO.agregarClase(clase);
    }

    public boolean modificarClase(Clase clase) {
        return claseDAO.actualizarClase(clase);
    }

    public boolean eliminarClase(int idClase) {
        return claseDAO.eliminarClase(idClase);
    }

    // ================== INSCRIPCIONES ==================

    public boolean inscribirAlumnoEnCurso(String legajoAlumno, String tituloCurso) {
        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajoAlumno);
        Curso curso = cursoDAO.obtenerCursoActivoPorTitulo(tituloCurso);

        if (alumno == null || curso == null) {
            System.out.println("⚠️ No se encontró alumno o curso.");
            return false;
        }

        Inscripcion inscripcion = new Inscripcion(alumno, curso);
        return inscripcionDAO.agregarInscripcion(inscripcion);
    }


    public List<Inscripcion> obtenerInscripcionesDeAlumno(String legajoAlumno) {
        return inscripcionDAO.listarInscripcionesPorLegajo(legajoAlumno);
    }

    public List<Inscripcion> obtenerInscripcionesPorEmail(String emailAlumno) {
        if (emailAlumno == null || emailAlumno.isBlank()) {
            return List.of();
        }

        Alumno alumno = alumnoDAO.obtenerAlumnoPorEmail(emailAlumno);

        if (alumno == null) {
            System.out.println("⚠️ No se encontró alumno con email: " + emailAlumno);
            return List.of();
        }

        return inscripcionDAO.listarInscripcionesPorLegajo(alumno.getLegajo());
    }

    // ================== PAGOS ==================

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

        Pago pago = new Pago(monto, alumno);
        return pagoDAO.agregarPago(pago);
    }

    public String obtenerLegajoPorEmail(String email) {
        Alumno alumno = alumnoDAO.obtenerAlumnoPorEmail(email);
        return alumno != null ? alumno.getLegajo() : null;
    }

    // ================== ASISTENCIA ==================

    public boolean tomarAsistencia(String legajoAlumno, int idCurso, Clase clase, boolean presente) {
        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajoAlumno);
        if (alumno == null) return false;

        Curso curso = cursoDAO.obtenerCursoPorId(idCurso);
        if (curso == null) return false;

        Inscripcion inscripcion = inscripcionDAO.obtenerInscripcion(alumno, curso);
        if (inscripcion == null) return false;

        Asistencia asistencia = new Asistencia(inscripcion, clase, presente);
        return asistenciaDAO.agregarAsistencia(asistencia);
    }

    // ================== CALIFICACIONES ==================

    public boolean registrarCalificacion(String legajoAlumno, int idCurso, TipoEvaluacion tipo, double nota) {
        if (nota < 0 || nota > 10) return false;

        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajoAlumno);
        Curso curso = cursoDAO.obtenerCursoPorId(idCurso);
        if (alumno == null || curso == null) return false;

        Inscripcion inscripcion = inscripcionDAO.obtenerInscripcion(alumno, curso);
        if (inscripcion == null) return false;

        Calificacion calificacion = new Calificacion(inscripcion, tipo, nota);
        return calificacionDAO.agregarCalificacion(calificacion);
    }

    public double calcularPorcentajeAsistencia(String legajoAlumno, int idCurso) {
        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajoAlumno);
        Curso curso = cursoDAO.obtenerCursoPorId(idCurso);
        if (alumno == null || curso == null) return 0;

        Inscripcion inscripcion = inscripcionDAO.obtenerInscripcion(alumno, curso);
        if (inscripcion == null) return 0;

        List<Clase> clases = claseDAO.obtenerClasesPorCurso(curso);
        if (clases.isEmpty()) return 0;

        int presentes = 0;
        for (Clase clase : clases) {
            Asistencia a = asistenciaDAO.obtenerAsistencia(inscripcion, clase);
            if (a != null && a.isPresente()) presentes++;
        }

        return (presentes * 100.0) / clases.size();
    }

    // ================== LOGIN ==================

    public TipoUsuario login(String email, String contrasena) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        return usuarioDAO.obtenerTipoUsuario(email, contrasena);
    }

    // ================== DESACTIVAR USUARIOS ==================

    public boolean desactivarAlumno(String legajo) {
        return alumnoDAO.eliminarAlumno(legajo);
    }

    public boolean desactivarDocente(String matricula) {
        return docenteDAO.eliminarDocente(matricula);
    }
}