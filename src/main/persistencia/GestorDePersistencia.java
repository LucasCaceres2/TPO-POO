package main.persistencia;

import com.google.gson.*;
import main.modelo.*;
import main.dto.*;

import java.io.*;
import java.util.*;

public class GestorDePersistencia {

    private final String fileUsuarios = "src/main/resources/data/usuarios.json";
    private final String fileCursos = "src/main/resources/data/cursos.json";
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
        for (Inscripcion i : c.getInscripciones()) alumnosIds.add(i.getAlumno().getIdUsuario());
        return new CursoDTO(c.getIdCurso(), c.getTitulo(), c.getCupoMax(),
                c.getDocente().getIdUsuario(), alumnosIds);
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
        Map<String, Usuario> mapUsuarios = new HashMap<>();
        for (Usuario u : usuarios) mapUsuarios.put(u.getIdUsuario(), u);

        for (CursoDTO dto : cargarCursosDTO()) {
            Docente docente = (Docente) mapUsuarios.get(dto.getIdDocente());
            Curso curso = new Curso(dto.getIdCurso(), dto.getTitulo(), dto.getCupoMax(), docente);

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

    public Curso cargarCurso(String idCurso) {
        for (Curso c : cargarCursos()) if (c.getIdCurso().equals(idCurso)) return c;
        return null;
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

}
