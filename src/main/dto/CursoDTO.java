package main.dto;

import java.util.List;

public class CursoDTO {
    private String idCurso;
    private String titulo;
    private int cupoMax;
    private String idDocente;
    private String idArea; // referencia al área del curso
    private List<String> alumnosIds; // lista de IDs de alumnos inscriptos

    public CursoDTO() {}

    public CursoDTO(String idCurso, String titulo, int cupoMax, String idDocente, String idArea, List<String> alumnosIds) {
        this.idCurso = idCurso;
        this.titulo = titulo;
        this.cupoMax = cupoMax;
        this.idDocente = idDocente;
        this.idArea = idArea;
        this.alumnosIds = alumnosIds;
    }

    // Getters y Setters
    public String getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(String idCurso) {
        this.idCurso = idCurso;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getCupoMax() {
        return cupoMax;
    }

    public void setCupoMax(int cupoMax) {
        this.cupoMax = cupoMax;
    }

    public String getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(String idDocente) {
        this.idDocente = idDocente;
    }

    public String getIdArea() {
        return idArea;
    }

    public void setIdArea(String idArea) {
        this.idArea = idArea;
    }

    public List<String> getAlumnosIds() {
        return alumnosIds;
    }

    public void setAlumnosIds(List<String> alumnosIds) {
        this.alumnosIds = alumnosIds;
    }

    @Override
    public String toString() {
        return "CursoDTO{" +
                "idCurso='" + idCurso + '\'' +
                ", titulo='" + titulo + '\'' +
                ", cupoMax=" + cupoMax +
                ", idDocente='" + idDocente + '\'' +
                ", idArea='" + idArea + '\'' +
                ", alumnosIds=" + alumnosIds +
                '}';
    }
}
