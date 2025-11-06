package main.dto;

import java.util.List;

public class CursoDTO {
    private String idCurso;
    private String titulo;
    private int cupoMax;
    private String idDocente;
    private String nombreArea; // referencia al área del curso
    private String contenido;
    private List<String> alumnosIds; // lista de IDs de alumnos inscriptos

    public CursoDTO() {}

    public CursoDTO(String idCurso, String titulo, int cupoMax, String idDocente, String nombreArea, String contenido ,List<String> alumnosIds) {
        this.idCurso = idCurso;
        this.titulo = titulo;
        this.cupoMax = cupoMax;
        this.idDocente = idDocente;
        this.nombreArea = nombreArea;
        this.contenido = contenido;
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

    public String getNombreArea() {
        return nombreArea;
    }

    public void setNombreArea(String nombreArea) {
        this.nombreArea = nombreArea;
    }

    public List<String> getAlumnosIds() {
        return alumnosIds;
    }

    public void setAlumnosIds(List<String> alumnosIds) {
        this.alumnosIds = alumnosIds;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    @Override
    public String toString() {
        return "CursoDTO{" +
                "idCurso='" + idCurso + '\'' +
                ", titulo='" + titulo + '\'' +
                ", cupoMax=" + cupoMax +
                ", idDocente='" + idDocente + '\'' +
                ", nombreArea='" + nombreArea + '\'' +
                ", contenido='" + contenido + '\'' +
                ", alumnosIds=" + alumnosIds +
                '}';
    }
}
