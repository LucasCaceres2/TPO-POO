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
    private String descripcion; //  texto general del curso
    private boolean activo = true;
    private transient List<Clase> clases;
    private transient List<Inscripcion> inscripciones;
    private int cantidadClases;

    // 🔹 Constructor para crear curso nuevo (antes de BD)
    public Curso(String titulo, int cupoMax, Docente docente, Area area, String descripcion,int cantidadClases) {
        this.titulo = titulo;
        this.cupoMax = cupoMax;
        this.docente = docente;
        this.area = area;
        this.descripcion = descripcion;
        this.inscripciones = new ArrayList<>();
        this.cantidadClases = cantidadClases;
        this.activo = true; //
    }

    // 🔹 Constructor para instanciar desde BD
    public Curso(int idCurso, String titulo, int cupoMax, Docente docente, Area area, String descripcion,int cantidadClases) {
        this.idCurso = idCurso;
        this.titulo = titulo;
        this.cupoMax = cupoMax;
        this.docente = docente;
        this.area = area;
        this.descripcion = descripcion;
        this.inscripciones = new ArrayList<>();
        this.cantidadClases = cantidadClases;
        this.activo = activo;
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
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<Inscripcion> getInscripciones() { 
        return inscripciones; 
    }
    
    public void setInscripciones(List<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }

    public int getCantidadClases() {
        return cantidadClases;
    }

    public void setCantidadClases(int cantidadClases) { this.cantidadClases = cantidadClases; }

    public List<Clase> getClases() { return clases; }

    public void setClases(List<Clase> clases) { this.clases = clases; }

    public boolean isActivo() { return activo; }

    public void setActivo(boolean activo) { this.activo = activo; }

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
        return (titulo != null) ? titulo : ("Curso " + idCurso);
    }
}
