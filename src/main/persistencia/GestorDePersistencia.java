package main.persistencia;

import com.google.gson.*;
import main.exception.UsuarioDuplicadoException;
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
        new File(fileAreas).getParentFile().mkdirs();
        new File(fileInscripciones).getParentFile().mkdirs();
    }

    // ------------------- USUARIOS -------------------

    public void guardarUsuario(Usuario usuario) {
        try {
            // (Opcional) formato
            if (usuario.getEmail() == null || !usuario.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                throw new IllegalArgumentException("Formato de email inválido: " + usuario.getEmail());
            }

            // 1) Validación centralizada
            validarEmailUnico(usuario.getEmail());

            // 2) Persistencia
            List<UsuarioDTO> lista = cargarUsuariosDTO();
            lista.add(convertirAUsuarioDTO(usuario));
            escribirJSON(lista, fileUsuarios);

            System.out.println("✅ Usuario registrado correctamente: " + usuario.getNombre());

        } catch (UsuarioDuplicadoException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error al guardar usuario: " + e.getMessage());
        }
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
        File file = new File(fileUsuarios);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        try (FileReader fr = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(fr).getAsJsonArray();
            List<UsuarioDTO> lista = new ArrayList<>();
            for (JsonElement elem : array) {
                lista.add(gson.fromJson(elem, UsuarioDTO.class));
            }
            return lista;
        } catch (IOException e) { e.printStackTrace(); }
        return new ArrayList<>();
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

    // ------------------- ÁREAS -------------------

    public void guardarArea(Area area) {
        List<AreaDTO> lista = cargarAreasDTO();
        lista.add(convertirAAreaDTO(area));
        escribirJSON(lista, fileAreas);
        System.out.println("✅ Área guardada correctamente: " + area.getNombre());
    }

    private AreaDTO convertirAAreaDTO(Area area) {
        List<String> nombreCursos = new ArrayList<>();
        for (Curso curso : area.getCursos()) {
            nombreCursos.add(curso.getIdCurso());
        }
        AreaDTO dto = new AreaDTO();
        dto.setIdArea(area.getIdArea());
        dto.setNombre(area.getNombre());
        dto.setNombreCursos(nombreCursos);
        return dto;
    }

    private List<AreaDTO> cargarAreasDTO() {
        File file = new File(fileAreas);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        try (FileReader fr = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(fr).getAsJsonArray();
            List<AreaDTO> lista = new ArrayList<>();
            for (JsonElement elem : array) {
                lista.add(gson.fromJson(elem, AreaDTO.class));
            }
            return lista;
        } catch (IOException e) { e.printStackTrace(); }
        return new ArrayList<>();
    }

    public List<Area> cargarAreas() {
        List<Area> areas = new ArrayList<>();
        List<Curso> cursos = cargarCursosSinAreas();

        Map<String, Curso> mapCursos = new HashMap<>();
        for (Curso c : cursos) mapCursos.put(c.getIdCurso(), c);

        for (AreaDTO dto : cargarAreasDTO()) {
            Area area = new Area(dto.getIdArea(), dto.getNombre());
            for (String idCurso : dto.getNombreCursos()) {
                Curso c = mapCursos.get(idCurso);
                if (c != null) {
                    area.getCursos().add(c);
                    c.setArea(area);
                }
            }
            areas.add(area);
        }
        return areas;
    }

    // ------------------- CURSOS -------------------

    public void guardarCurso(Curso curso) {
        List<CursoDTO> lista = cargarCursosDTO();
        lista.add(convertirACursoDTO(curso));
        escribirJSON(lista, fileCursos);
        System.out.println("✅ Curso guardado correctamente: " + curso.getTitulo());
    }

    private CursoDTO convertirACursoDTO(Curso c) {
        List<String> alumnosIds = new ArrayList<>();
        for (Inscripcion i : c.getInscripciones()) {
            alumnosIds.add(i.getAlumno().getIdUsuario());
        }
        String nombreArea = (c.getArea() != null) ? c.getArea().getIdArea() : null;
        return new CursoDTO(c.getIdCurso(), c.getTitulo(), c.getCupoMax(),
                c.getDocente().getIdUsuario(), nombreArea, c.getContenido(), alumnosIds);
    }

    private List<CursoDTO> cargarCursosDTO() {
        File file = new File(fileCursos);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        try (FileReader fr = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(fr).getAsJsonArray();
            List<CursoDTO> lista = new ArrayList<>();
            for (JsonElement elem : array) lista.add(gson.fromJson(elem, CursoDTO.class));
            return lista;
        } catch (IOException e) { e.printStackTrace(); }
        return new ArrayList<>();
    }

    public List<Curso> cargarCursos() {
        List<Curso> cursos = cargarCursosSinAreas();
        List<Area> areas = cargarAreas();
        return cursos;
    }

    // Para evitar ciclo con áreas
    private List<Curso> cargarCursosSinAreas() {
        List<Curso> cursos = new ArrayList<>();
        List<Usuario> usuarios = cargarUsuarios();
        Map<String, Usuario> mapUsuarios = new HashMap<>();
        for (Usuario u : usuarios) mapUsuarios.put(u.getIdUsuario(), u);

        for (CursoDTO dto : cargarCursosDTO()) {
            Docente docente = (Docente) mapUsuarios.get(dto.getIdDocente());
            Curso curso = new Curso(dto.getIdCurso(), dto.getTitulo(), dto.getCupoMax(),
                    docente, null, dto.getContenido());
            cursos.add(curso);
        }
        return cursos;
    }

    // ------------------- INSCRIPCIONES -------------------

    public void guardarInscripcion(Inscripcion inscripcion) {
        List<InscripcionDTO> lista = cargarInscripcionesDTO();
        lista.add(convertirAInscripcionDTO(inscripcion));
        escribirJSON(lista, fileInscripciones);
        System.out.println("✅ Inscripción guardada: " + inscripcion.getIdInscripcion());
    }

    private InscripcionDTO convertirAInscripcionDTO(Inscripcion i) {
        Pago pago = i.getPago();
        PagoDTO pagoDTO = null;
        if (pago != null) {
            pagoDTO = new PagoDTO(pago.getIdPago(), pago.getFecha(), pago.getMonto(),
                    (pago.getAlumno() != null ? pago.getAlumno().getIdUsuario() : null));
        }
        return new InscripcionDTO(i.getIdInscripcion(), i.getFecha(),
                i.getAlumno() != null ? i.getAlumno().getIdUsuario() : null,
                i.getCurso() != null ? i.getCurso().getIdCurso() : null,
                i.getEstado() != null ? i.getEstado().name() : EstadoInscripcion.PENDIENTE_PAGO.name(),
                pagoDTO);
    }

    private List<InscripcionDTO> cargarInscripcionesDTO() {
        File file = new File(fileInscripciones);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        try (FileReader fr = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(fr).getAsJsonArray();
            List<InscripcionDTO> lista = new ArrayList<>();
            for (JsonElement elem : array) lista.add(gson.fromJson(elem, InscripcionDTO.class));
            return lista;
        } catch (IOException e) { e.printStackTrace(); }
        return new ArrayList<>();
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
                Pago pago = (dto.getPago() != null)
                        ? new Pago(dto.getPago().getIdPago(), dto.getPago().getFecha(),
                        dto.getPago().getMonto(), alumno)
                        : null;
                EstadoInscripcion estado = (dto.getEstado() != null)
                        ? EstadoInscripcion.valueOf(dto.getEstado())
                        : EstadoInscripcion.PENDIENTE_PAGO;

                Inscripcion i = new Inscripcion(dto.getIdInscripcion(), alumno, curso, pago, estado);
                curso.agregarInscripcion(i);
                inscripciones.add(i);
            }
        }
        return inscripciones;
    }

    // ------------------- PAGO -------------------
    public void registrarPago(String idInscripcion, Pago pago) {
        List<Inscripcion> inscripciones = cargarInscripciones();

        Inscripcion inscripcion = null;
        for (Inscripcion i : inscripciones) {
            if (i.getIdInscripcion().equals(idInscripcion)) {
                inscripcion = i;
                break;
            }
        }

        if (inscripcion == null) {
            System.out.println("❌ No se encontró inscripción con ID: " + idInscripcion);
            return;
        }

        // Validar estado
        if (inscripcion.getEstado() != EstadoInscripcion.PENDIENTE_PAGO) {
            System.out.println("⚠️ La inscripción no está pendiente de pago (estado actual: "
                    + inscripcion.getEstado() + ")");
            return;
        }

        // Validar que no haya pago previo
        if (inscripcion.getPago() != null) {
            System.out.println("⚠️ Ya existe un pago registrado para esta inscripción.");
            return;
        }

        // Asociar pago y actualizar estado
        inscripcion.setPago(pago);
        inscripcion.setEstado(EstadoInscripcion.CURSANDO);

        // Guardar todo de nuevo
        List<InscripcionDTO> listaDTO = new ArrayList<>();
        for (Inscripcion i : inscripciones) {
            listaDTO.add(convertirAInscripcionDTO(i));
        }
        escribirJSON(listaDTO, fileInscripciones);

        System.out.println("💰 Pago registrado correctamente para inscripción "
                + idInscripcion + ". Estado actualizado a CURSANDO.");
    }

    // ------------------- UTILS -------------------

    private <T> void escribirJSON(List<T> lista, String path) {
        try (FileWriter fw = new FileWriter(path)) {
            gson.toJson(lista, fw);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Alumno> cargarAlumnos() {
        List<Alumno> lista = new ArrayList<>();
        for (Usuario u : cargarUsuarios()) if (u instanceof Alumno) lista.add((Alumno) u);
        return lista;
    }

    public List<Docente> cargarDocentes() {
        List<Docente> lista = new ArrayList<>();
        for (Usuario u : cargarUsuarios()) if (u instanceof Docente) lista.add((Docente) u);
        return lista;
    }
    private void validarEmailUnico(String email) throws UsuarioDuplicadoException {
        for (Usuario u : cargarUsuarios()) {
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) {
                throw new UsuarioDuplicadoException("Ya existe un usuario con el email: " + email);
            }
        }
    }

}
