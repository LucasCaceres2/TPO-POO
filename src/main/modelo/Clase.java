package main.modelo;

import java.util.Date;

public class Clase {
    private int idClase;
    private Curso curso;      // FK
    private Date fecha;
    private String titulo;    // título de la clase
    private String contenido; // descripción o desarrollo textual de la clase

    public Clase( Curso curso, Date fecha, String titulo, String contenido) {
        this.curso = curso;
        this.fecha = fecha;
        this.titulo = titulo;
        this.contenido = contenido;
    }
    public Clase(int idClase, Curso curso, Date fecha, String titulo, String contenido) {
        this.idClase = idClase;
        this.curso = curso;
        this.fecha = fecha;
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public Clase() { }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public void setIdClase(int idClase) { this.idClase = idClase; }
    public int getIdClase() { return idClase; }

    @Override
    public String toString() {
        return titulo + " (" + fecha + ")";
    }
}

