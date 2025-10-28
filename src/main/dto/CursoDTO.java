// CursoDTO.java
package main.dto;

import java.util.List;

public class CursoDTO {
    private String idCurso;
    private String titulo;
    private int cupoMax;
    private String idDocente;
    private List<String> alumnosIds; // opcional

    public CursoDTO() {}

    public CursoDTO(String idCurso, String titulo, int cupoMax, String idDocente, List<String> alumnosIds) {
        this.idCurso = idCurso;
        this.titulo = titulo;
        this.cupoMax = cupoMax;
        this.idDocente = idDocente;
        this.alumnosIds = alumnosIds;
    }

    public String getIdCurso() { return idCurso; }
    public void setIdCurso(String idCurso) { this.idCurso = idCurso; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getCupoMax() { return cupoMax; }
    public void setCupoMax(int cupoMax) { this.cupoMax = cupoMax; }

    public String getIdDocente() { return idDocente; }
    public void setIdDocente(String idDocente) { this.idDocente = idDocente; }

    public List<String> getAlumnosIds() { return alumnosIds; }
    public void setAlumnosIds(List<String> alumnosIds) { this.alumnosIds = alumnosIds; }
}
