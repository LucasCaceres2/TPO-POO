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
    protected int idUsuario;
    protected String nombre;
    protected String apellido;
    protected String email;
    protected String contrasena;
    protected TipoUsuario tipoUsuario;

    // 🔹 Constructor para crear un usuario nuevo (antes de insertarlo en BD)
    public Usuario(String nombre, String apellido, String email, String contrasena, TipoUsuario tipoUsuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
    }

    // 🔹 Constructor para instanciar un usuario que ya existe en BD
    public Usuario(int idUsuario, String nombre, String apellido, String email, String contrasena, TipoUsuario tipoUsuario) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
    }

    // 🔹 Getters
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getContrasena() { return contrasena; }
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

}