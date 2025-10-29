package main.persistencia;

import com.google.gson.*;
import main.modelo.*;
import main.dto.*;

import java.io.*;
import java.util.*;

public class GestorDePersistencia {

    private final String fileUsuarios = "src/main/resources/data/usuarios.json";
    private final String fileCursos = "src/main/resources/data/cursos.json";
    private final String fileAreas = "src/main/resources/data/areas.json";
    private final String fileInscripciones = "src/main/resources/data/inscripciones.json";

    private final Gson gson;

    public GestorDePersistencia() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        new File(fileUsuarios).getParentFile().mkdirs();
        new File(fileCursos).getParentFile().mkdirs();
    }

    // ------------------- USUARIOS -------------------

    public void guardarUsuario(Usuario usuario) {
        List<UsuarioDTO> lista = cargarUsuariosDTO();
        UsuarioDTO dto = convertirAUsuarioDTO(usuario);
        lista.add(dto);
        escribirJSON(lista, fileUsuarios);
        System.out.println("✅ Usuario registrado correctamente: " + usuario.getNombre());
    }

    private UsuarioDTO convertirAUsuarioDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setEmail(u.getEmail());
        dto.setContrasena(u.getContrasena());
        dto.setTipoUsuario(u.getTipoUsuario().toString());
        if (u instanceof Alumno) dto.setLegajo(((Alumno) u).getLegajo());
        if (u instanceof Docente) dto.setMatricula(((Docente) u).getMatricula());
        return dto;
    }

    private List<UsuarioDTO> cargarUsuariosDTO() {
        List<UsuarioDTO> lista = new ArrayList<>();
        File file = new File(fileUsuarios);
        if (!file.exists()) return lista;
        try (FileReader fr = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(fr).getAsJsonArray();
            for (JsonElement elem : array) {
                lista.add(gson.fromJson(elem, UsuarioDTO.class));
            }
        } catch (IOException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Usuario> cargarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        for (UsuarioDTO dto : cargarUsuariosDTO()) {
            if ("ALUMNO".equals(dto.getTipoUsuario())) {
                usuarios.add(new Alumno(dto.getIdUsuario(), dto.getNombre(), dto.getApellido(),
                        dto.getEmail(), dto.getContrasena(), dto.getLegajo()));
            } else if ("DOCENTE".equals(dto.getTipoUsuario())) {
                usuarios.add(new Docente(dto.getIdUsuario(), dto.getNombre(), dto.getApellido(),
                        dto.getEmail(), dto.getContrasena(), dto.getMatricula()));
            }
        }
        return usuarios;
    }

    // ------------------- CURSOS -------------------

    public void guardarCurso(Curso curso) {
        List<CursoDTO> lista = cargarCursosDTO();
        CursoDTO dto = convertirACursoDTO(curso);
        lista.add(dto);
        escribirJSON(lista, fileCursos);
        System.out.println("✅ Curso guardado correctamente: " + curso.getTitulo());
    }

    private CursoDTO convertirACursoDTO(Curso c) {
        List<String> alumnosIds = new ArrayList<>();
        for (Inscripcion i : c.getInscripciones()) {
            alumnosIds.add(i.getAlumno().getIdUsuario());
        }

        String nombreArea = (c.getArea() != null) ? c.getArea().getNombre() : null;

        return new CursoDTO(
                c.getIdCurso(),
                c.getTitulo(),
                c.getCupoMax(),
                c.getDocente().getIdUsuario(),
                nombreArea, // 🔹 ahora guardamos el nombre del área
                c.getContenido(),
                alumnosIds
        );
    }

    private List<CursoDTO> cargarCursosDTO() {
        List<CursoDTO> lista = new ArrayList<>();
        File file = new File(fileCursos);
        if (!file.exists()) return lista;
        try (FileReader fr = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(fr).getAsJsonArray();
            for (JsonElement elem : array) {
                lista.add(gson.fromJson(elem, CursoDTO.class));
            }
        } catch (IOException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Curso> cargarCursos() {
        List<Curso> cursos = new ArrayList<>();
        List<Usuario> usuarios = cargarUsuarios();
        List<Area> areas = cargarAreas(); // 🔹 cargamos las áreas primero

        Map<String, Usuario> mapUsuarios = new HashMap<>();
        for (Usuario u : usuarios) mapUsuarios.put(u.getIdUsuario(), u);

        Map<String, Area> mapAreas = new HashMap<>();
        for (Area a : areas) mapAreas.put(a.getNombre(), a); // 🔹 clave = nombre

        for (CursoDTO dto : cargarCursosDTO()) {
            Docente docente = (Docente) mapUsuarios.get(dto.getIdDocente());
            Area area = (dto.getNombreArea() != null) ? mapAreas.get(dto.getNombreArea()) : null;

            Curso curso = new Curso(dto.getIdCurso(), dto.getTitulo(), dto.getCupoMax(), docente, area, dto.getContenido());

            if (area != null) area.getCursos().add(curso);
            if (dto.getAlumnosIds() != null) {
                for (String idAlumno : dto.getAlumnosIds()) {
                    Alumno a = (Alumno) mapUsuarios.get(idAlumno);
                    if (a != null) curso.agregarInscripcion(new Inscripcion(a, curso));
                }
            }
            cursos.add(curso);
        }
        return cursos;
    }

    // ------------------- ÁREAS -------------------

    public void guardarArea(Area area) {
        List<AreaDTO> lista = cargarAreasDTO();
        AreaDTO dto = convertirAAreaDTO(area);
        lista.add(dto);
        escribirJSON(lista, fileAreas);
        System.out.println("✅ Área guardada correctamente: " + area.getNombre());
    }

    private AreaDTO convertirAAreaDTO(Area area) {
        List<String> nombreCursos = new ArrayList<>();
        for (Curso curso : area.getCursos() ) {
            nombreCursos.add(curso.getTitulo());
        }

        AreaDTO dto = new AreaDTO();
        dto.setIdArea(area.getIdArea());
        dto.setNombre(area.getNombre());
        dto.setNombreCursos(nombreCursos);
        return dto;
    }

    private List<AreaDTO> cargarAreasDTO() {
        List<AreaDTO> lista = new ArrayList<>();
        File file = new File(fileAreas);
        if (!file.exists()) return lista;

        try (FileReader fr = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(fr).getAsJsonArray();
            for (JsonElement elem : array) {
                lista.add(gson.fromJson(elem, AreaDTO.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Area> cargarAreas() {
        List<Area> areas = new ArrayList<>();
        List<Curso> cursos = cargarCursos();
        Map<String, Curso> mapCursos = new HashMap<>();
        for (Curso c : cursos) mapCursos.put(c.getIdCurso(), c);

        for (AreaDTO dto : cargarAreasDTO()) {
            Area area = new Area(dto.getIdArea(), dto.getNombre());
            for (String nombreCurso : dto.getNombreCursos()) {
                for (Curso curso : cursos) {
                    if (curso.getTitulo().equals(nombreCurso)) {
                        area.getCursos().add(curso);
                        curso.setArea(area);
                        break;
                    }
                }
            }

            areas.add(area);
        }
        return areas;
    }

    // --------------- INSCRIPCION -----------------

    public void guardarInscripcion(Inscripcion inscripcion) {
        List<InscripcionDTO> lista = cargarInscripcionesDTO();
        InscripcionDTO dto = convertirAInscripcionDTO(inscripcion);
        lista.add(dto);
        escribirJSON(lista, fileInscripciones);
        System.out.println("✅ Inscripción guardada: " + inscripcion.getIdInscripcion());
    }

    private InscripcionDTO convertirAInscripcionDTO(Inscripcion i) {
        Pago pago = i.getPago();
        PagoDTO pagoDTO = null;

        // Convertimos el Pago a PagoDTO si existe
        if (pago != null) {
            pagoDTO = new PagoDTO(
                    pago.getIdPago(),
                    pago.getFecha(),
                    pago.getMonto(),
                    pago.getAlumno().getIdUsuario()
            );
        }

        // Guardamos el estado como String para el JSON
        String estadoStr = (i.getEstado() != null) ? i.getEstado().name() : EstadoInscripcion.PENDIENTE_PAGO.name();

        return new InscripcionDTO(
                i.getIdInscripcion(),
                i.getFecha(),
                i.getAlumno().getIdUsuario(),
                (i.getCurso() != null ? i.getCurso().getIdCurso() : null),
                estadoStr,
                pagoDTO
        );
    }


    // Cargar todas las inscripciones como DTOs
    private List<InscripcionDTO> cargarInscripcionesDTO() {
        List<InscripcionDTO> lista = new ArrayList<>();
        File file = new File(fileInscripciones);
        if (!file.exists()) return lista;
        try (FileReader fr = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(fr).getAsJsonArray();
            for (JsonElement elem : array) {
                lista.add(gson.fromJson(elem, InscripcionDTO.class));
            }
        } catch (IOException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Inscripcion> cargarInscripciones() {
        List<Inscripcion> inscripciones = new ArrayList<>();
        List<Alumno> alumnos = cargarAlumnos();
        List<Curso> cursos = cargarCursos();

        Map<String, Alumno> mapAlumnos = new HashMap<>();
        for (Alumno a : alumnos) mapAlumnos.put(a.getIdUsuario(), a);

        Map<String, Curso> mapCursos = new HashMap<>();
        for (Curso c : cursos) mapCursos.put(c.getIdCurso(), c);

        for (InscripcionDTO dto : cargarInscripcionesDTO()) {
            Alumno alumno = mapAlumnos.get(dto.getIdAlumno());
            Curso curso = mapCursos.get(dto.getIdCurso());

            if (alumno != null && curso != null) {
                // Constructor que asigna por defecto PENDIENTE_PAGO
                Inscripcion i = new Inscripcion(alumno, curso);

                // Asignar estado según el DTO
                if (dto.getEstado() != null) {
                    i.setEstado(EstadoInscripcion.valueOf(dto.getEstado()));
                }

                // Convertir PagoDTO a Pago si existe
                if (dto.getPago() != null) {
                    PagoDTO pagoDTO = dto.getPago();
                    Pago pago = new Pago(
                            pagoDTO.getIdPago(),
                            pagoDTO.getFecha(),
                            pagoDTO.getMonto(),
                            alumno
                    );
                    i.setPago(pago);
                }

                curso.agregarInscripcion(i); // mantiene la relación curso ↔ inscripción
                inscripciones.add(i);
            }
        }
        return inscripciones;
    }


    // ----------------BORRAR DATOS ----------------

    public void eliminarUsuario(String idUsuario) {
        List<UsuarioDTO> lista = cargarUsuariosDTO();
        lista.removeIf(u -> u.getIdUsuario().equals(idUsuario));
        escribirJSON(lista, fileUsuarios);
    }

    public void eliminarCurso(String idCurso) {
        List<CursoDTO> lista = cargarCursosDTO();
        lista.removeIf(c -> c.getIdCurso().equals(idCurso));
        escribirJSON(lista, fileCursos);
    }

    public void eliminarArea(String idArea) {
        List<AreaDTO> lista = cargarAreasDTO();
        lista.removeIf(a -> a.getIdArea().equals(idArea));
        escribirJSON(lista, fileAreas);
    }

    public void eliminarInscripcion(Inscripcion inscripcion) {
        List<InscripcionDTO> lista = cargarInscripcionesDTO();
        lista.removeIf(dto -> dto.getIdInscripcion().equals(inscripcion.getIdInscripcion()));
        escribirJSON(lista, fileInscripciones);
    }


    // ------------------- UTILS -------------------

    private <T> void escribirJSON(List<T> lista, String path) {
        try (FileWriter fw = new FileWriter(path)) {
            gson.toJson(lista, fw);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Alumno> cargarAlumnosDeCurso(Curso curso) {
        List<Alumno> alumnos = new ArrayList<>();
        if (curso == null) return alumnos;
        for (Inscripcion i : curso.getInscripciones()) alumnos.add(i.getAlumno());
        return alumnos;
    }

    public List<Docente> cargarDocentes() {
        List<Docente> lista = new ArrayList<>();
        for (Usuario u : cargarUsuarios()) if (u instanceof Docente) lista.add((Docente) u);
        return lista;
    }

    public List<Alumno> cargarAlumnos() {
        List<Alumno> lista = new ArrayList<>();
        for (Usuario u : cargarUsuarios()) if (u instanceof Alumno) lista.add((Alumno) u);
        return lista;
    }

    public Curso cargarCurso(String idCurso) {
        for (Curso c : cargarCursos()) if (c.getIdCurso().equals(idCurso)) return c;
        return null;
    }

    public void actualizarEstadoInscripcion(String idInscripcion, EstadoInscripcion nuevoEstado) {
        List<InscripcionDTO> lista = cargarInscripcionesDTO();
        boolean encontrado = false;

        for (InscripcionDTO dto : lista) {
            if (dto.getIdInscripcion().equals(idInscripcion)) {
                dto.setEstado(nuevoEstado.name());
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            escribirJSON(lista, fileInscripciones);
            System.out.println("✅ Estado de inscripción actualizado: " + idInscripcion + " → " + nuevoEstado);
        } else {
            System.out.println("❌ No se encontró la inscripción con id: " + idInscripcion);
        }
    }

}
