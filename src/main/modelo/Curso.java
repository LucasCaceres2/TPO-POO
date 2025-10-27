package main.modelo;

import java.util.ArrayList;
import java.util.List;

public class Curso {
    private String idCurso;
    private String titulo;
    private int cupoMax;
    private Docente docente;
    private Area area;
    private List<Contenido> contenidos;
    private List<Inscripcion> inscripciones;

    public Curso(String idCurso, String titulo, int cupoMax, Docente docente, Area area) {
        this.idCurso = idCurso;
        this.titulo = titulo;
        this.cupoMax = cupoMax;
        this.docente = docente;
        this.area = area;
        this.contenidos = new ArrayList<>();
        this.inscripciones = new ArrayList<>();
    }

    // Métodos de negocio
    public void agregarContenido(Contenido contenido) {
        contenidos.add(contenido);
        contenido.setCurso(this);
    }

    public void listarAlumnos() {
        System.out.println("Alumnos inscriptos en " + titulo + ":");
        if (inscripciones.isEmpty()) {
            System.out.println("No hay alumnos inscriptos.");
        } else {
            for (Inscripcion i : inscripciones) {
                System.out.println("- " + i.getAlumno().getNombre() + " " + i.getAlumno().getApellido());
            }
        }
    }

    public void modificarCurso() {
        System.out.println("Curso " + titulo + " modificado.");
    }

    public boolean tieneCupo() {
        return inscripciones.size() < cupoMax;
    }

    public void agregarInscripcion(Inscripcion inscripcion) {
        inscripciones.add(inscripcion);
    }

    // Getters y Setters
    public String getIdCurso() { return idCurso; }
    public String getTitulo() { return titulo; }
    public int getCupoMax() { return cupoMax; }
    public Docente getDocente() { return docente; }
    public Area getArea() { return area; }
    public List<Contenido> getContenidos() { return contenidos; }
    public List<Inscripcion> getInscripciones() { return inscripciones; }

    public void setDocente(Docente docente) { this.docente = docente; }
    public void setArea(Area area) { this.area = area; }
}
