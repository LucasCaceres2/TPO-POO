package main.dto;

import java.util.ArrayList;
import java.util.List;

public class AreaDTO {
    private String idArea;
    private String nombre;
    private List<String> cursosIds; // lista de IDs de cursos o nombres

    public AreaDTO() {
        this.cursosIds = new ArrayList<>();
    }

    // Getters y Setters
    public String getIdArea() {
        return idArea;
    }

    public void setIdArea(String idArea) {
        this.idArea = idArea;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getCursosIds() {
        return cursosIds;
    }

    public void setCursosIds(List<String> cursosIds) {
        this.cursosIds = cursosIds;
    }

    @Override
    public String toString() {
        return "AreaDTO{" +
                "idArea='" + idArea + '\'' +
                ", nombre='" + nombre + '\'' +
                ", cursosIds=" + cursosIds +
                '}';
    }
}
