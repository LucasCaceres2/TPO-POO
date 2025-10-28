package main.modelo;

import java.util.ArrayList;
import java.util.List;

public class Area {
    private String idArea;
    private String nombre;
    private List<Curso> cursos;

    public Area(String idArea, String nombre) {
        this.idArea = idArea;
        this.nombre = nombre;
        this.cursos = new ArrayList<>();
    }
/*
    public void agregarCurso(Curso curso) {
        cursos.add(curso);
        curso.setArea(this);
        System.out.println("Curso agregado al área: " + nombre);
    }
*/
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
    public String getIdArea() { return idArea; }
    public String getNombre() { return nombre; }
    public List<Curso> getCursos() { return cursos; }
}
