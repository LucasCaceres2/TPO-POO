package main.modelo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import main.exception.UsuarioDuplicadoException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Usuario {

    protected String idUsuario;      // se asigna en registrarse()
    protected String nombre;
    protected String apellido;
    protected String email;
    protected String contrasena;
    protected TipoUsuario tipoUsuario;

    public Usuario(String nombre,
                   String apellido,
                   String email,
                   String contrasena,
                   TipoUsuario tipoUsuario) {

        this.idUsuario = null; // todavía sin ID
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
    }

    public String getIdUsuario()      { return idUsuario; }
    public String getNombre()         { return nombre; }
    public String getApellido()       { return apellido; }
    public String getEmail()          { return email; }
    public String getContrasena()     { return contrasena; }
    public TipoUsuario getTipoUsuario(){ return tipoUsuario; }

    public abstract void iniciarSesion();
    public abstract void cerrarSesion();
    public abstract void actualizarPerfil();

    // -------------------------------------------------
    // registrarse():
    // - valida email único
    // - genera idUsuario único
    // - genera legajo (Alumno) o matrícula (Docente), únicos
    // - guarda en usuarios.json
    // -------------------------------------------------
    public void registrarse() throws UsuarioDuplicadoException {

        String filePath = "src/main/resources/data/usuarios.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray usuariosArray = new JsonArray();

        File file = new File(filePath);

        // 1. Leer archivo si existe
        if (file.exists()) {
            try (FileReader fr = new FileReader(file)) {
                JsonElement root = JsonParser.parseReader(fr);
                if (root != null && root.isJsonArray()) {
                    usuariosArray = root.getAsJsonArray();
                }
            } catch (IOException e) {
                System.out.println("Error al leer usuarios.json: " + e.getMessage());
            }
        } else {
            // crear carpeta si no existe
            file.getParentFile().mkdirs();
        }

        // 2. Recorrer usuarios existentes:
        //    - validar email repetido
        //    - buscar el mayor idUsuario numérico
        //    - buscar el mayor legajo numérico ("LEG-x")
        //    - buscar la mayor matricula numérica ("MAT-x")
        int maxId             = 0;
        int maxLegajoNum      = 0;
        int maxMatriculaNum   = 0;

        for (JsonElement elem : usuariosArray) {
            JsonObject u = elem.getAsJsonObject();

            // --- validar email duplicado ---
            String emailExistente = u.get("email").getAsString();
            if (emailExistente.equalsIgnoreCase(this.email)) {
                throw new UsuarioDuplicadoException(
                        "Ya existe un usuario con el email: " + this.email
                );
            }

            // --- calcular próximo idUsuario ---
            String idStr = u.get("idUsuario").getAsString();
            try {
                int valor = Integer.parseInt(idStr.trim());
                if (valor > maxId) {
                    maxId = valor;
                }
            } catch (NumberFormatException ignore) {
                // ignorar ids no numéricos
            }

            // --- calcular próximo legajo (solo en alumnos guardados) ---
            // asumimos formato "LEG-<numero>"
            if (u.has("legajo")) {
                String legajoExistente = u.get("legajo").getAsString(); // ej "LEG-15"
                if (legajoExistente.startsWith("LEG-")) {
                    String numeroStr = legajoExistente.substring(4); // "15"
                    try {
                        int legVal = Integer.parseInt(numeroStr.trim());
                        if (legVal > maxLegajoNum) {
                            maxLegajoNum = legVal;
                        }
                    } catch (NumberFormatException ignore) {
                        // si estaba corrupto lo salteamos
                    }
                }
            }

            // --- calcular próxima matrícula (solo en docentes guardados) ---
            // asumimos formato "MAT-<numero>"
            if (u.has("matricula")) {
                String matriculaExistente = u.get("matricula").getAsString(); // ej "MAT-9"
                if (matriculaExistente.startsWith("MAT-")) {
                    String numeroStr = matriculaExistente.substring(4); // "9"
                    try {
                        int matVal = Integer.parseInt(numeroStr.trim());
                        if (matVal > maxMatriculaNum) {
                            maxMatriculaNum = matVal;
                        }
                    } catch (NumberFormatException ignore) {
                        // lo ignoramos si no es número válido
                    }
                }
            }
        }

        // 3. Asignar idUsuario nuevo único
        this.idUsuario = String.valueOf(maxId + 1); // ej "7"

        // 4. Asignar legajo o matrícula según el tipo de usuario
        if (this instanceof Alumno) {
            Alumno a = (Alumno) this;
            a.legajo = "LEG-" + (maxLegajoNum + 1); // ej "LEG-16"
        } else if (this instanceof Docente) {
            Docente d = (Docente) this;
            d.matricula = "MAT-" + (maxMatriculaNum + 1); // ej "MAT-10"
        }

        // 5. Convertir ESTE usuario (Alumno o Docente) a JSON
        JsonObject thisJsonObj = gson.toJsonTree(this).getAsJsonObject();
        thisJsonObj.addProperty("tipoUsuario", tipoUsuario.toString());

        // 6. Agregar al array en memoria
        usuariosArray.add(thisJsonObj);

        // 7. Guardar archivo actualizado
        try (FileWriter fw = new FileWriter(filePath)) {
            gson.toJson(usuariosArray, fw);
            System.out.println(
                    "Usuario registrado: " + this.nombre +
                            " (id=" + this.idUsuario +
                            (this instanceof Alumno
                                    ? ", legajo=" + ((Alumno)this).getLegajo()
                                    : (this instanceof Docente
                                    ? ", matricula=" + ((Docente)this).getMatricula()
                                    : "")
                            ) +
                            ")"
            );
        } catch (IOException e) {
            System.out.println("Error al guardar usuario: " + e.getMessage());
        }
    }
}
