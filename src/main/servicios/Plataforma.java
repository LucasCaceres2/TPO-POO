package main.servicios;

import main.dao.*;
import main.modelo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Plataforma {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private AlumnoDAO alumnoDAO = new AlumnoDAO();
    private DocenteDAO docenteDAO = new DocenteDAO();
    private CursoDAO cursoDAO = new CursoDAO();
    private InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private PagoDAO pagoDAO = new PagoDAO();
    private AreaDAO areaDAO = new AreaDAO();
    private AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private CalificacionDAO calificacionDAO = new CalificacionDAO();

    // --- Registro de nuevo alumno ---
    public boolean registrarAlumno(String nombre, String apellido, String email, String contrasena) {
        Alumno alumno = new Alumno(0, nombre, apellido, email, contrasena, null); // legajo se genera en DAO
        return alumnoDAO.agregarAlumno(alumno);
    }

    // --- Registrar nuevo docente ---
    public boolean registrarDocente(String nombre, String apellido, String email, String contrasena, String matricula) {
        Docente docente = new Docente(nombre, apellido, email, contrasena, matricula);
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

        List<Asistencia> asistencias = asistenciaDAO.obtenerAsistenciasPorInscripcion(inscripcion);
        if (asistencias == null || asistencias.isEmpty()) {
            return 0.0;
        }

        java.util.Date hoy = new java.util.Date();
        int presentes = 0;
        int clasesHastaHoy = 0;

        for (Asistencia a : asistencias) {
            if (!a.getFecha().after(hoy)) { // solo contamos clases hasta hoy
                clasesHastaHoy++;
                if (a.isPresente()) {
                    presentes++;
                }
            }
        }

        if (clasesHastaHoy == 0) {
            return 0.0;
        }

        return (presentes * 100.0) / clasesHastaHoy;
    }

    public List<Alumno> listarAlumnos() {
        return alumnoDAO.listarAlumnos();
    }

    public boolean actualizarAlumno(String legajo, String campo, String nuevoValor) {
        if (legajo == null || legajo.isEmpty() || campo == null || campo.isEmpty()) return false;

        List<String> camposPermitidos = List.of("nombre", "apellido", "email", "contrasena");
        if (!camposPermitidos.contains(campo)) {
            System.out.println("⚠️ No se puede modificar el campo '" + campo + "'.");
            return false;
        }

        return alumnoDAO.actualizarAlumno(legajo, campo, nuevoValor);
    }

    public boolean eliminarAlumno(String legajo) {
        Alumno a = alumnoDAO.obtenerAlumnoPorLegajo(legajo);
        if (a == null) return false;
        return alumnoDAO.eliminarAlumno(a.getLegajo());
    }

    // --- DOCENTES ---
    public List<Docente> listarDocentes() {
        return docenteDAO.listarDocentes();
    }

    public boolean actualizarDocente(String matricula, String campo, String nuevoValor) {
        if (matricula == null || matricula.isEmpty() || campo == null || campo.isEmpty()) return false;

        List<String> camposPermitidos = List.of("nombre", "apellido", "email", "contrasena");
        if (!camposPermitidos.contains(campo)) {
            System.out.println("⚠️ No se puede modificar el campo '" + campo + "'.");
            return false;
        }

        return docenteDAO.actualizarDocente(matricula, campo, nuevoValor);
    }

    public boolean eliminarDocente(String matricula) {
        Docente d = docenteDAO.obtenerDocentePorMatricula(matricula);
        if (d == null) return false;
        return docenteDAO.eliminarDocente(d.getMatricula());
    }

    public boolean actualizarCurso(int idCurso, String campo, String nuevoValor) {
        if (idCurso <= 0 || campo == null || campo.isEmpty()) return false;

        List<String> camposPermitidos = List.of("titulo", "cupoMax", "descripcion", "cantidadClases");
        if (!camposPermitidos.contains(campo)) {
            System.out.println("⚠️ No se puede modificar el campo '" + campo + "'.");
            return false;
        }

        return cursoDAO.actualizarCurso(idCurso, campo, nuevoValor);
    }

    public boolean eliminarCurso(int idCurso) {
        Curso c = cursoDAO.obtenerCursoPorId(idCurso);
        if (c == null) return false;
        return cursoDAO.eliminarCurso(idCurso);
    }

    // --- ÁREAS ---
    public List<Area> listarAreas() {
        return areaDAO.listarAreas();
    }

    public boolean actualizarArea(int idArea, String nuevoNombre) {
        if (idArea <= 0 || nuevoNombre == null || nuevoNombre.isEmpty()) return false;
        return areaDAO.actualizarArea(idArea, nuevoNombre);
    }

    public boolean eliminarArea(int idArea) {
        Area a = areaDAO.obtenerAreaPorId(idArea);
        if (a == null) return false;
        return areaDAO.eliminarArea(idArea);
    }

    public List<Curso> listarCursosPorDocente(String matricula) {
        Docente docente = docenteDAO.obtenerDocentePorMatricula(matricula);
        if (docente == null) return new ArrayList<>();
        return cursoDAO.listarCursosPorDocente(docente.getIdUsuario());
    }

    public List<Alumno> listarAlumnosInscritos(int idCurso) {
        return cursoDAO.listarAlumnosInscritos(idCurso); // ya devuelve List<Alumno>
    }

    // 🔹 Obtener docente por matrícula desde Plataforma
    public Docente obtenerDocentePorMatricula(String matricula) {
        return docenteDAO.obtenerDocentePorMatricula(matricula);
    }

    public boolean darseDeBaja(String legajo, int idCurso) {
        return inscripcionDAO.darseDeBaja(legajo, idCurso);
    }

    public void mostrarAsistencias(String legajo) {
        List<Inscripcion> inscripciones = inscripcionDAO.listarInscripcionesPorLegajo(legajo);
        System.out.println("\n📊 Asistencias:");
        for (Inscripcion i : inscripciones) {
            Double asistencia = i.getPorcentajeAsistencia();
            System.out.println("- " + i.getCurso().getTitulo() + " : " +
                    (asistencia != null ? String.format("%.2f%%", asistencia) : "N/A"));
        }
    }

    public void mostrarCalificaciones(String legajo) {
        List<Inscripcion> inscripciones = inscripcionDAO.listarInscripcionesPorLegajo(legajo);
        System.out.println("\n📝 Calificaciones:");
        for (Inscripcion i : inscripciones) {
            System.out.print("- " + i.getCurso().getTitulo() + " : ");
            for (TipoEvaluacion t : TipoEvaluacion.values()) {
                Double nota = i.getCalificacion(t);
                if (nota != null) {
                    System.out.print(t + "=" + nota + " ");
                }
            }
            System.out.println();
        }
    }

    public void mostrarRendimiento(String legajo) {
        List<Inscripcion> inscripciones = inscripcionDAO.listarInscripcionesPorLegajo(legajo);
        System.out.println("\n📈 Rendimiento:");
        for (Inscripcion i : inscripciones) {
            System.out.println("- " + i.resumenRendimiento());
        }
    }

    public boolean darseDeBajaCurso(String legajo, String tituloCurso) {
        Curso curso = cursoDAO.obtenerCursoPorTitulo(tituloCurso);
        if (curso == null) {
            System.out.println("⚠️ Curso no encontrado.");
            return false;
        }
        Inscripcion insc = inscripcionDAO.obtenerInscripcion(
                alumnoDAO.obtenerAlumnoPorLegajo(legajo), curso);
        if (insc == null || !insc.puedeDarseDeBaja()) {
            System.out.println("⚠️ No se puede dar de baja de este curso.");
            return false;
        }
        insc.darseDeBaja();
        return inscripcionDAO.actualizarEstadoCurso(insc.getIdInscripcion(), insc.getEstadoCurso());
    }

    // Devuelve true si la inscripción fue exitosa
    public boolean inscribirAlumnoPorArea(String legajoAlumno) {
        // 1️⃣ Mostrar áreas disponibles
        List<String> areas = cursoDAO.listarAreas(); // metodo que devuelve áreas únicas
        if (areas.isEmpty()) {
            System.out.println("⚠️ No hay áreas disponibles.");
            return false;
        }

        System.out.println("\nÁreas disponibles:");
        for (int i = 0; i < areas.size(); i++) {
            System.out.println((i + 1) + ". " + areas.get(i));
        }

        System.out.print("Elegí un área (número): ");
        int idxArea = new Scanner(System.in).nextInt() - 1;
        if (idxArea < 0 || idxArea >= areas.size()) {
            System.out.println("⚠️ Área inválida.");
            return false;
        }
        String areaElegida = areas.get(idxArea);

        // 2️⃣ Mostrar cursos del área seleccionada
        List<String> cursos = cursoDAO.listarCursosPorArea(areaElegida); // devuelve títulos de cursos
        if (cursos.isEmpty()) {
            System.out.println("⚠️ No hay cursos en esta área.");
            return false;
        }

        System.out.println("\nCursos disponibles en " + areaElegida + ":");
        for (int i = 0; i < cursos.size(); i++) {
            System.out.println((i + 1) + ". " + cursos.get(i));
        }

        System.out.print("Elegí un curso (número): ");
        int idxCurso = new Scanner(System.in).nextInt() - 1;
        if (idxCurso < 0 || idxCurso >= cursos.size()) {
            System.out.println("⚠️ Curso inválido.");
            return false;
        }

        String cursoElegido = cursos.get(idxCurso);

        // 3️⃣ Inscribir alumno al curso
        return inscribirAlumnoEnCurso(legajoAlumno, cursoElegido);
    }

    public String validarLogin(String email, String contrasena) {
        if (email == null || contrasena == null) {
            System.out.println("⚠️ Email y contraseña no pueden ser nulos.");
            return null;
        }

        Usuario usuario = usuarioDAO.obtenerUsuarioPorEmail(email);

        if (usuario == null) {
            System.out.println("⚠️ No existe un usuario con ese email.");
            return null;
        }

        if (!usuario.getContrasena().equals(contrasena)) {
            System.out.println("❌ Contraseña incorrecta.");
            return null;
        }

        System.out.println("✅ Login exitoso como " + usuario.getTipoUsuario());
        return usuario.getTipoUsuario().toString();
    }

    public String obtenerLegajoPorEmail(String email) {
        Alumno alumno = alumnoDAO.obtenerAlumnoPorEmail(email);
        return alumno != null ? alumno.getLegajo() : null;
    }

    public String obtenerMatriculaPorEmail(String email) {
        return docenteDAO.obtenerMatriculaPorEmail(email);
    }

}