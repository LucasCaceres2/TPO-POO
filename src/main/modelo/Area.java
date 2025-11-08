package main.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Area {
    private int idArea;
    private String nombre;
    private List<Curso> cursos = new ArrayList<>();

    public Area(String nombre) {
        setNombre(nombre);
    }

    public Area(int idArea, String nombre) {
        this.idArea = idArea;
        setNombre(nombre);
    }

    // Relación bidireccional
    public void agregarCurso(Curso curso) {
        if (curso == null) return;

        if (!cursos.contains(curso)) {
            cursos.add(curso);
            if (curso.getArea() != this) {
                curso.setArea(this);
            }
        }
    }

    public void removerCurso(Curso curso) {
        if (curso == null) return;

        if (cursos.remove(curso) && curso.getArea() == this) {
            curso.setArea(null);
        }
    }

    // Getters
    public int getIdArea() { return idArea; }
    public String getNombre() { return nombre; }
    public List<Curso> getCursos() { return new ArrayList<>(cursos); }

    // Setters
    public void setIdArea(int idArea) { this.idArea = idArea; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del área no puede ser vacío");
        }
        this.nombre = nombre.trim();
    }

    // equals / hashCode opcional, basado en idArea
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Area)) return false;
        Area other = (Area) o;
        return idArea != 0 && idArea == other.idArea;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idArea);
    }

    @Override
    public String toString() {
        return "Area{" + idArea + " - " + nombre + "}";
    }
}