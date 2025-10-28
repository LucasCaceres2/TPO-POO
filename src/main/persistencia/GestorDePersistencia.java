package main.persistencia;

import com.google.gson.*;
import main.modelo.*;

import java.io.*;
import java.util.*;

public class GestorDePersistencia {

    private final String fileUsuarios = "src/main/resources/data/usuarios.json";
    private final String fileCursos   = "src/main/resources/data/cursos.json";
    private final Gson gson;

    public GestorDePersistencia() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        // Asegurar que existan las carpetas/archivos
        File fUsuarios = new File(fileUsuarios);
        File fCursos   = new File(fileCursos);

        fUsuarios.getParentFile().mkdirs();
        fCursos.getParentFile().mkdirs();

        try {
            if (!fUsuarios.exists()) fUsuarios.createNewFile();
            if (!fCursos.exists())   fCursos.createNewFile();

            // Si están vacíos, inicializarlos como []
            if (fUsuarios.length() == 0) {
                try (FileWriter fw = new FileWriter(fUsuarios)) {
                    fw.write("[]");
                }
            }
            if (fCursos.length() == 0) {
                try (FileWriter fw = new FileWriter(fCursos)) {
                    fw.write("[]");
                }
            }
        } catch (IOException e) {
            System.out.println("Error inicializando archivos: " + e.getMessage());
        }
    }

    // ==========================
    // USUARIOS
    // ==========================

    public void guardarUsuarios(List<Usuario> usuarios) {
        JsonArray arr = new JsonArray();

        for (Usuario u : usuarios) {
            JsonObject jsonU = new JsonObject();
            jsonU.addProperty("idUsuario",   u.getIdUsuario());
            jsonU.addProperty("nombre",      u.getNombre());
            jsonU.addProperty("apellido",    u.getApellido());
            jsonU.addProperty("email",       u.getEmail());
            jsonU.addProperty("contrasena",  u.getContrasena());
            jsonU.addProperty("tipoUsuario", u.getTipoUsuario().toString());

            if (u instanceof Alumno) {
                Alumno a = (Alumno) u;
                jsonU.addProperty("legajo", a.getLegajo());
            }

            if (u instanceof Docente) {
                Docente d = (Docente) u;
                jsonU.addProperty("matricula", d.getMatricula());
            }

            arr.add(jsonU);
        }

        try (FileWriter fw = new FileWriter(fileUsuarios)) {
            gson.toJson(arr, fw);
        } catch (IOException e) {
            System.out.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    public List<Usuario> cargarUsuarios() {
        List<Usuario> resultado = new ArrayList<>();

        try (FileReader fr = new FileReader(fileUsuarios)) {
            JsonArray arr = JsonParser.parseReader(fr).getAsJsonArray();

            for (JsonElement elem : arr) {
                JsonObject o = elem.getAsJsonObject();

                String id          = o.get("idUsuario").getAsString();
                String nombre      = o.get("nombre").getAsString();
                String apellido    = o.get("apellido").getAsString();
                String email       = o.get("email").getAsString();
                String contrasena  = o.get("contrasena").getAsString();
                String tipo        = o.get("tipoUsuario").getAsString();

                if ("ALUMNO".equalsIgnoreCase(tipo)) {
                    String legajo = o.has("legajo") ? o.get("legajo").getAsString() : "";
                    Alumno a = new Alumno(id, nombre, apellido, email, contrasena, legajo);
                    resultado.add(a);

                } else if ("DOCENTE".equalsIgnoreCase(tipo)) {
                    String matricula = o.has("matricula") ? o.get("matricula").getAsString() : "";
                    Docente d = new Docente(id, nombre, apellido, email, contrasena, matricula);
                    resultado.add(d);

                } else {
                    System.out.println("TipoUsuario desconocido: " + tipo);
                }
            }

        } catch (IOException e) {
            System.out.println("Error cargando usuarios: " + e.getMessage());
        }

        return resultado;
    }

    // ==========================
    // CURSOS
    // ==========================

    // Guarda TODOS los cursos (lista completa) en cursos.json
    public void guardarCursos(List<Curso> cursos) {
        JsonArray arr = new JsonArray();

        for (Curso c : cursos) {
            JsonObject o = new JsonObject();

            o.addProperty("idCurso",   c.getIdCurso());
            o.addProperty("titulo",    c.getTitulo());
            o.addProperty("cupoMax",   c.getCupoMax());
            o.addProperty("idDocente", c.getDocente() != null ? c.getDocente().getIdUsuario() : null);

            // Guardamos el contenido/temario del curso (String)
            o.addProperty("contenido", c.getContenido());

            // Guardamos también qué alumnos están inscriptos
            JsonArray alumnosIds = new JsonArray();
            for (Inscripcion insc : c.getInscripciones()) {
                Alumno alumno = insc.getAlumno();
                if (alumno != null) {
                    alumnosIds.add(alumno.getIdUsuario());
                }
            }
            o.add("alumnosIds", alumnosIds);

            arr.add(o);
        }

        try (FileWriter fw = new FileWriter(fileCursos)) {
            gson.toJson(arr, fw);
        } catch (IOException e) {
            System.out.println("Error guardando cursos: " + e.getMessage());
        }
    }

    // Carga TODOS los cursos desde cursos.json (reconstruye objetos reales)
    public List<Curso> cargarCursos() {
        List<Curso> resultado = new ArrayList<>();

        // Necesitamos los usuarios para mapear docente y alumnos
        List<Usuario> usuarios = cargarUsuarios();

        try (FileReader fr = new FileReader(fileCursos)) {
            JsonArray arr = JsonParser.parseReader(fr).getAsJsonArray();

            for (JsonElement elem : arr) {
                JsonObject o = elem.getAsJsonObject();

                String idCurso   = o.get("idCurso").getAsString();
                String titulo    = o.get("titulo").getAsString();
                int cupoMax      = o.get("cupoMax").getAsInt();

                String idDocente = o.has("idDocente") && !o.get("idDocente").isJsonNull()
                        ? o.get("idDocente").getAsString()
                        : null;

                String contenido = o.has("contenido")
                        ? o.get("contenido").getAsString()
                        : "";

                // Buscar docente por id
                Docente docenteEncontrado = null;
                if (idDocente != null) {
                    for (Usuario u : usuarios) {
                        if (u instanceof Docente && u.getIdUsuario().equals(idDocente)) {
                            docenteEncontrado = (Docente) u;
                            break;
                        }
                    }
                }

                // Reconstruimos el curso
                Curso curso = new Curso(idCurso, titulo, cupoMax, docenteEncontrado, contenido);

                // Reconstruimos las inscripciones alumno <-> curso
                if (o.has("alumnosIds")) {
                    JsonArray arrAlu = o.get("alumnosIds").getAsJsonArray();
                    for (JsonElement aluIdElem : arrAlu) {
                        String aluId = aluIdElem.getAsString();

                        // Busco el Alumno correspondiente
                        for (Usuario u : usuarios) {
                            if (u instanceof Alumno && u.getIdUsuario().equals(aluId)) {
                                Alumno alumnoEncontrado = (Alumno) u;

                                // Creo la inscripción
                                Inscripcion insc = new Inscripcion(alumnoEncontrado, curso);

                                // La agrego al curso
                                curso.agregarInscripcion(insc);

                                // Y también al Alumno (para mantener consistencia en memoria)
                                alumnoEncontrado.getInscripciones().add(insc);
                            }
                        }
                    }
                }

                resultado.add(curso);
            }

        } catch (IOException e) {
            System.out.println("Error cargando cursos: " + e.getMessage());
        }

        return resultado;
    }

    // Guarda UN curso puntual y actualiza el archivo
    // (compatibilidad con Docente.crearCurso(...))
    public void guardarCurso(Curso curso) {
        // 1. Cargo todos los cursos actuales del archivo
        List<Curso> cursosExistentes = cargarCursos();

        // 2. Veo si ya existía un curso con ese idCurso
        boolean reemplazado = false;
        for (int i = 0; i < cursosExistentes.size(); i++) {
            if (cursosExistentes.get(i).getIdCurso().equals(curso.getIdCurso())) {
                cursosExistentes.set(i, curso);
                reemplazado = true;
                break;
            }
        }

        // 3. Si no estaba, lo agrego
        if (!reemplazado) {
            cursosExistentes.add(curso);
        }

        // 4. Reescribo el archivo completo usando guardarCursos(...)
        guardarCursos(cursosExistentes);
    }
}
