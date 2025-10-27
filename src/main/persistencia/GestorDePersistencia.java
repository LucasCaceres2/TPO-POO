package main.persistencia;

import com.google.gson.*;
import main.modelo.Alumno;
import main.modelo.Docente;
import main.modelo.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorDePersistencia {

    private final String filePath = "src/main/resources/data/usuarios.json";
    private Gson gson;

    public GestorDePersistencia() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        // Crear carpeta si no existe
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
    }

    // Guardar un usuario (Alumno o Docente)
    public void guardarUsuario(Usuario usuario) {
        List<JsonObject> usuariosArray = new ArrayList<>();

        // Leer el archivo existente si existe
        File file = new File(filePath);
        if (file.exists()) {
            try (FileReader fr = new FileReader(file)) {
                JsonElement root = JsonParser.parseReader(fr);
                if (root != null && root.isJsonArray()) {
                    for (JsonElement elem : root.getAsJsonArray()) {
                        usuariosArray.add(elem.getAsJsonObject());
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al leer usuarios.json: " + e.getMessage());
            }
        }

        // Convertir el usuario a JsonObject
        JsonElement thisJsonElem = gson.toJsonTree(usuario);
        JsonObject thisJsonObj = thisJsonElem.getAsJsonObject();

        // Guardar el tipo de usuario
        thisJsonObj.addProperty("tipoUsuario", usuario.getTipoUsuario().toString());

        // Agregar al array y escribir en disco
        usuariosArray.add(thisJsonObj);

        try (FileWriter fw = new FileWriter(filePath)) {
            gson.toJson(usuariosArray, fw);
            System.out.println("Usuario registrado correctamente: " + usuario.getNombre());
        } catch (IOException e) {
            System.out.println("Error al guardar usuario: " + e.getMessage());
        }
    }

    // Cargar todos los usuarios desde el JSON
    public List<Usuario> cargarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return usuarios;

        try (FileReader fr = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(fr).getAsJsonArray();
            for (JsonElement elem : array) {
                JsonObject obj = elem.getAsJsonObject();
                String tipo = obj.get("tipoUsuario").getAsString();

                if (tipo.equals("ALUMNO")) {
                    Alumno alumno = new Alumno(
                            obj.get("idUsuario").getAsString(),
                            obj.get("nombre").getAsString(),
                            obj.get("apellido").getAsString(),
                            obj.get("email").getAsString(),
                            obj.get("contrasena").getAsString(),
                            obj.get("legajo").getAsString()
                    );
                    usuarios.add(alumno);
                } else if (tipo.equals("DOCENTE")) {
                    Docente docente = new Docente(
                            obj.get("idUsuario").getAsString(),
                            obj.get("nombre").getAsString(),
                            obj.get("apellido").getAsString(),
                            obj.get("email").getAsString(),
                            obj.get("contrasena").getAsString(),
                            obj.get("matricula").getAsString()
                    );
                    usuarios.add(docente);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    public List<Docente> cargarDocentes() {
        List<Docente> docentes = new ArrayList<>();
        for (Usuario u : cargarUsuarios()) {
            if (u instanceof Docente) {
                docentes.add((Docente) u);
            }
        }
        return docentes;
    }

    public List<Alumno> cargarAlumnos() {
        List<Alumno> alumnos = new ArrayList<>();
        for (Usuario u : cargarUsuarios()) {
            if (u instanceof Alumno) {
                alumnos.add((Alumno) u);
            }
        }
        return alumnos;
    }

}
