package main.modelo;

import main.dao.InscripcionDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Curso {
    private int idCurso;
    private String titulo;
    private int cupoMax;
    private Docente docente;
    private Area area;
    private String contenido;
    private transient List<Inscripcion> inscripciones;

    // 🔹 Constructor para crear curso nuevo (antes de BD)
    public Curso(String titulo, int cupoMax, Docente docente, Area area, String contenido) {
        this.titulo = titulo;
        this.cupoMax = cupoMax;
        this.docente = docente;
        this.area = area;
        this.contenido = contenido;
        this.inscripciones = new ArrayList<>();
    }

    // 🔹 Constructor para instanciar desde BD
    public Curso(int idCurso, String titulo, int cupoMax, Docente docente, Area area, String contenido) {
        this.idCurso = idCurso;
        this.titulo = titulo;
        this.cupoMax = cupoMax;
        this.docente = docente;
        this.area = area;
        this.contenido = contenido;
        this.inscripciones = new ArrayList<>();
    }

    // 🔹 Cargar inscripciones desde BD
    public void cargarInscripciones() {
        if (this.idCurso <= 0) {
            return;
        }
        InscripcionDAO inscripcionDAO = new InscripcionDAO();
        this.inscripciones = inscripcionDAO.listarInscripcionesPorCurso(this.idCurso);
    }

    // 🔹 Obtener nombres de alumnos inscritos (sin imprimir)
    public List<String> obtenerNombresAlumnosInscritos() {
        if (inscripciones == null || inscripciones.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> nombres = new ArrayList<>();
        for (Inscripcion i : inscripciones) {
            if (i.getAlumno() != null) {
                nombres.add(i.getAlumno().getNombre() + " " + i.getAlumno().getApellido());
            }
        }
        return nombres;
    }

    // 🔹 Validación: ¿Tiene cupo disponible?
    public boolean tieneCupo() {
        if (inscripciones == null) return true;
        return inscripciones.size() < cupoMax;
    }

    // 🔹 Obtener cantidad de inscriptos
    public int getCantidadInscriptos() {
        return (inscripciones != null) ? inscripciones.size() : 0;
    }

    // 🔹 Obtener cupos disponibles
    public int getCuposDisponibles() {
        return cupoMax - getCantidadInscriptos();
    }

    // Getters y Setters
    public int getIdCurso() { 
        return idCurso; 
    }
    
    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso; 
    }
    
    public String getTitulo() { 
        return titulo; 
    }
    
    public int getCupoMax() { 
        return cupoMax; 
    }
    
    public Docente getDocente() { 
        return docente; 
    }
    
    public void setDocente(Docente docente) { 
        this.docente = docente; 
    }
    
    public Area getArea() { 
        return area; 
    }
    
    public void setArea(Area area) { 
        this.area = area; 
    }
    
    public String getContenido() { 
        return contenido; 
    }
    
    public void setContenido(String contenido) { 
        this.contenido = contenido; 
    }
    
    public List<Inscripcion> getInscripciones() { 
        return inscripciones; 
    }
    
    public void setInscripciones(List<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }

    // 🔹 equals() y hashCode() basados en idCurso
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Curso)) return false;
        Curso curso = (Curso) o;
        return idCurso == curso.idCurso;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCurso);
    }

    // 🔹 toString() mejorado
    @Override
    public String toString() {
        return String.format("Curso{id=%d, titulo='%s', cupo=%d/%d, docente=%s}", 
            idCurso, titulo, getCantidadInscriptos(), cupoMax, 
            (docente != null ? docente.getNombre() : "N/A"));
    }
}
