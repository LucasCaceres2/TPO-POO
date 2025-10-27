package main.modelo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// Clase abstracta base
public abstract class Usuario {
    protected String idUsuario;
    protected String nombre;
    protected String apellido;
    protected String email;
    protected String contrasena;
    protected TipoUsuario tipoUsuario; // enum para indicar ALUMNO o DOCENTE

    // Constructor
    public Usuario(String idUsuario, String nombre, String apellido, String email, String contrasena, TipoUsuario tipoUsuario) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
    }

    //Getters
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getContrasena() { return contrasena; }
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }

    // Métodos abstractos
    public abstract void iniciarSesion();
    public abstract void cerrarSesion();
    public abstract void actualizarPerfil();

    // Método registrarse con JSON
    public void registrarse() {
        String filePath = "src/main/resources/data/usuarios.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray usuariosArray = new JsonArray();

        File file = new File(filePath);
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
            // Crear carpeta si no existe
            file.getParentFile().mkdirs();
        }

        // Convertir el objeto actual (Alumno o Docente) a JsonObject
        JsonElement thisJsonElem = gson.toJsonTree(this);
        JsonObject thisJsonObj = thisJsonElem.getAsJsonObject();

        // Guardar el enum tipoUsuario como string
        thisJsonObj.addProperty("tipoUsuario", tipoUsuario.toString());

        // Agregar al array y escribir en disco
        usuariosArray.add(thisJsonObj);

        try (FileWriter fw = new FileWriter(filePath)) {
            gson.toJson(usuariosArray, fw);
            System.out.println("Usuario registrado correctamente: " + this.nombre);
        } catch (IOException e) {
            System.out.println("Error al guardar usuario: " + e.getMessage());
        }
    }
}
