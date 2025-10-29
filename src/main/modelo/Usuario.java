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
    public String getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getContrasena() { return contrasena; }
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }

    // Métodos abstractos
    public abstract void iniciarSesion();
    public abstract void cerrarSesion();
    public abstract void actualizarPerfil();

}
