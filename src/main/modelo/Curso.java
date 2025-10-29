package main.modelo;

import java.util.ArrayList;
import java.util.List;

public class Curso {
    private String idCurso;
    private String titulo;
    private int cupoMax;
    private Docente docente;
    private Area area;

    // Nuevo: el contenido del curso ahora es un único String
    private String contenido;

    private List<Inscripcion> inscripciones;

    public Curso(String idCurso, String titulo, int cupoMax, Docente docente, Area area) {
        this.idCurso = idCurso;
        this.titulo = titulo;
        this.cupoMax = cupoMax;
        this.docente = docente;
        this.area = area;
        this.contenido = ""; // por defecto vacío
        this.inscripciones = new ArrayList<>();
    }

    // =========================
    // Gestión del contenido
    // =========================

    // Setea o reemplaza el contenido completo del curso
    public void setContenido(String contenido) {
        this.contenido = contenido;
        System.out.println("Contenido actualizado para el curso: " + titulo);
    }

    // Devuelve el contenido actual
    public String getContenido() {
        return contenido;
    }

    // Muestra el contenido en consola (por comodidad para debug / menú)
    public void mostrarContenido() {
        System.out.println("Contenido del curso " + titulo + ":");
        if (contenido == null || contenido.isEmpty()) {
            System.out.println("(Sin contenido cargado)");
        } else {
            System.out.println(contenido);
        }
    }

    // =========================
    // Gestión de alumnos / inscripciones
    // =========================

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
        if (!tieneCupo()) {
            System.out.println("⚠ No hay cupo disponible en " + titulo);
            return;
        }

        // agregar al curso
        inscripciones.add(inscripcion);

        // asociar en alumno (si todavía no está)
        if (!inscripcion.getAlumno().getInscripciones().contains(inscripcion)) {
            inscripcion.getAlumno().getInscripciones().add(inscripcion);
        }

        // asociar el curso dentro de la inscripción
        inscripcion.setCurso(this);
    }

    // =========================
    // Getters / Setters básicos
    // =========================

    public String getIdCurso() { return idCurso; }
    public String getTitulo() { return titulo; }
    public int getCupoMax() { return cupoMax; }
    public Docente getDocente() { return docente; }
    public Area getArea() { return area; }
    public List<Inscripcion> getInscripciones() { return inscripciones; }

    public void setDocente(Docente docente) { this.docente = docente; }
    public void setArea(Area area) { this.area = area; }

    @Override
    public String toString() {
        return "Curso{" + idCurso + " - " + titulo + "}";
    }
}
