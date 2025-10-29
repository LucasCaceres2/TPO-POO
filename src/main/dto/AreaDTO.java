package main.dto;

import java.util.ArrayList;
import java.util.List;

public class AreaDTO {
    private String idArea;
    private String nombre;
    private List<String> nombreCursos; // lista de IDs de cursos o nombres

    public AreaDTO() {
        this.nombreCursos = new ArrayList<>();
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

    public List<String> getNombreCursos() {
        return nombreCursos;
    }

    public void setNombreCursos(List<String> cursosIds) {
        this.nombreCursos = cursosIds;
    }

    @Override
    public String toString() {
        return "AreaDTO{" +
                "idArea='" + idArea + '\'' +
                ", nombre='" + nombre + '\'' +
                ", Cursos=" + nombreCursos +
                '}';
    }
}
