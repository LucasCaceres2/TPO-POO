package main.modelo;

import main.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class Curso {
    private String idCurso;
    private String titulo;
    private int cupoMax;
    private Docente docente;
    private Area area;
    private String contenido;
    private transient List<Inscripcion> inscripciones;

    public Curso(String idCurso, String titulo, int cupoMax, Docente docente, Area area, String contenido) {
        this.idCurso = idCurso;
        this.titulo = titulo;
        this.cupoMax = cupoMax;
        this.docente = docente;
        this.area = area;
        this.contenido = contenido;
        this.inscripciones = new ArrayList<>();
    }

    public Curso(String titulo, int cupoMax, Docente docente, Area area, String contenido) {
        this(IdGenerator.newCursoId(), titulo, cupoMax, docente, area, contenido);
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
        // Asociar la inscripción al alumno
        if (!inscripcion.getAlumno().getInscripciones().contains(inscripcion)) {
            inscripcion.getAlumno().getInscripciones().add(inscripcion);
        }
        // Asociar la inscripción al curso en memoria
        inscripcion.setCurso(this);
    }

    // Getters y Setters
    public String getIdCurso() { return idCurso; }
    public String getTitulo() { return titulo; }
    public int getCupoMax() { return cupoMax; }
    public Docente getDocente() { return docente; }
    public Area getArea() { return area; }
    public String getContenido() { return contenido; }
    public List<Inscripcion> getInscripciones() { return inscripciones; }

    public void setDocente(Docente docente) { this.docente = docente; }
    public void setArea(Area area) { this.area = area; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    @Override
    public String toString() {
        return "Curso{" + idCurso + " - " + titulo + "}";
    }
}
