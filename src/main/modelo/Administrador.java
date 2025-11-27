package main.modelo;

import main.controlador.Plataforma;
import main.dao.*;

import java.util.List;

public class Administrador extends Usuario {

    // Fachada de reglas de negocio
    private final Plataforma plataforma = new Plataforma();

    // Acceso directo a DAOs para operaciones de mantenimiento
    private final AreaDAO areaDAO = new AreaDAO();
    private final CursoDAO cursoDAO = new CursoDAO();
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final DocenteDAO docenteDAO = new DocenteDAO();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();

    public Administrador(int idUsuario,
                         String nombre,
                         String apellido,
                         String email,
                         String contrasena) {
        super(idUsuario, nombre, apellido, email, contrasena, TipoUsuario.ADMIN);
    }

    // ===================== ÁREAS =====================

    // ya lo usás en formGestionAreas
    public boolean crearArea(String nombreArea) {
        return plataforma.crearArea(nombreArea);
    }

    public boolean actualizarArea(int idArea, String nuevoNombre) {
        return areaDAO.actualizarArea(idArea, nuevoNombre);
    }

    public boolean eliminarArea(int idArea) {
        return areaDAO.eliminarArea(idArea);
    }

    public List<Area> listarAreas() {
        return areaDAO.listarAreas();
    }

    // ===================== CURSOS =====================

    // ya lo usás en formGestionCursos
    public boolean crearCurso(String titulo,
                              int cupoMax,
                              String matriculaDocente,
                              String nombreArea,
                              String descripcion,
                              int cantidadClases,
                              double precio) {
        return plataforma.crearCurso(
                titulo,
                cupoMax,
                matriculaDocente,
                nombreArea,
                descripcion,
                cantidadClases,
                precio
        );
    }

    // ya lo usás en formGestionCursos (eliminar)
    public boolean eliminarCurso(int idCurso) {
        return plataforma.eliminarCurso(idCurso);
    }

    // soporte para pantallas de consulta
    public List<Curso> listarCursos() {
        return cursoDAO.listarCursos();
    }

    public boolean actualizarCursoTitulo(int idCurso, String nuevoTitulo) {
        return cursoDAO.actualizarCurso(idCurso, "titulo", nuevoTitulo);
    }

    public boolean actualizarCursoContenido(int idCurso, String nuevoContenido) {
        return cursoDAO.actualizarCurso(idCurso, "contenido", nuevoContenido);
    }

    public boolean actualizarCursoPrecio(int idCurso, double nuevoPrecio) {
        return cursoDAO.actualizarCurso(idCurso, "precio", String.valueOf(nuevoPrecio));
    }

    // ===================== ALUMNOS =====================

    public boolean crearAlumno(String legajo,
                               String nombre,
                               String apellido,
                               String email,
                               String contrasena) {

        Alumno alumno = new Alumno(nombre, apellido, email, contrasena, legajo);
        return alumnoDAO.agregarAlumno(alumno);
    }

    public boolean actualizarAlumno(Alumno alumno) {
        return alumnoDAO.actualizarAlumno(alumno);
    }

    public boolean eliminarAlumno(String legajo) {
        return alumnoDAO.eliminarAlumno(legajo);
    }

    public Alumno obtenerAlumnoPorLegajo(String legajo) {
        return alumnoDAO.obtenerAlumnoPorLegajo(legajo);
    }

    public List<Alumno> listarAlumnos() {
        return alumnoDAO.listarAlumnos();
    }

    // ===================== DOCENTES =====================

    public boolean crearDocente(String matricula,
                                String nombre,
                                String apellido,
                                String email,
                                String contrasena) {

        Docente docente = new Docente(nombre, apellido, email, contrasena, matricula);
        return docenteDAO.agregarDocente(docente);
    }


    public boolean actualizarDocenteCampo(String matricula,
                                          String campo,
                                          String nuevoValor) {
        // campos válidos en tu DAO: nombre, apellido, email, contrasena
        return docenteDAO.actualizarDocente(matricula, campo, nuevoValor);
    }

    public boolean actualizarDocente(Docente docente) {
        if (docente == null
                || docente.getMatricula() == null
                || docente.getMatricula().isBlank()) {
            return false;
        }

        String matricula = docente.getMatricula();

        boolean ok = true;
        // Acá actualizamos todos los campos básicos usando el DAO existente
        if (docente.getNombre() != null) {
            ok &= docenteDAO.actualizarDocente(matricula, "nombre", docente.getNombre());
        }
        if (docente.getApellido() != null) {
            ok &= docenteDAO.actualizarDocente(matricula, "apellido", docente.getApellido());
        }
        if (docente.getEmail() != null) {
            ok &= docenteDAO.actualizarDocente(matricula, "email", docente.getEmail());
        }
        if (docente.getContrasena() != null && !docente.getContrasena().isBlank()) {
            ok &= docenteDAO.actualizarDocente(matricula, "contrasena", docente.getContrasena());
        }

        return ok;
    }

    public boolean eliminarDocente(String matricula) {
        return docenteDAO.eliminarDocente(matricula);
    }

    public Docente obtenerDocentePorMatricula(String matricula) {
        return docenteDAO.obtenerDocentePorMatricula(matricula);
    }

    public List<Docente> listarDocentes() {
        return docenteDAO.listarDocentes();
    }

    // ===================== INSCRIPCIONES (consultas) =====================

    public List<Inscripcion> listarTodasLasInscripciones() {
        return inscripcionDAO.listarTodasInscripciones();
    }

    public List<Inscripcion> listarInscripcionesPorCurso(int idCurso) {
        return inscripcionDAO.listarInscripcionesPorCurso(idCurso);
    }

    public List<Inscripcion> listarInscripcionesPorLegajo(String legajo) {
        return inscripcionDAO.listarInscripcionesPorLegajo(legajo);
    }
}
