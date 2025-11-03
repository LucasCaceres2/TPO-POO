package main.modelo;

import java.util.ArrayList;
import java.util.List;

public class Area {
    private int idArea;
    private String nombre;
    private List<Curso> cursos;

    public Area(String nombre) {
        this.nombre = nombre;
        this.cursos = new ArrayList<>();
    }

    public Area(int idArea, String nombre) {
        this.idArea = idArea;
        this.nombre = nombre;
        this.cursos = new ArrayList<>();
    }

    // Método para mantener la relación bidireccional con Curso
    public void agregarCurso(Curso curso) {
        if (!cursos.contains(curso)) {
            cursos.add(curso);
            curso.setArea(this); // Asigna esta área al curso
            System.out.println("Curso agregado al área: " + nombre);
        }
    }

    public void listarCursos() {
        System.out.println("Cursos del área " + nombre + ":");
        if (cursos.isEmpty()) {
            System.out.println("No hay cursos registrados.");
        } else {
            for (Curso curso : cursos) {
                System.out.println("- " + curso.getTitulo());
            }
        }
    }

    // Getters
    public int getIdArea() { return idArea; }
    public String getNombre() { return nombre; }
    public List<Curso> getCursos() { return cursos; }

    // Setters
    public void setIdArea(int idArea) { this.idArea = idArea; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCursos(List<Curso> cursos) { this.cursos = cursos; }

    @Override
    public String toString() {
        return "Area{" + idArea + " - " + nombre + "}";
    }

}
