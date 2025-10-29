package main.app;

import main.dto.*;
import main.modelo.*;
import main.persistencia.GestorDePersistencia;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        GestorDePersistencia gestor = new GestorDePersistencia();

        // ------------------- Crear usuarios -------------------
        Alumno alumno1 = new Alumno("a001", "Lucas", "Gomez", "lucas@gmail.com", "1234", "L001");
        Alumno alumno2 = new Alumno("a002", "María", "Lopez", "maria@gmail.com", "1234", "L002");
        Docente docente1 = new Docente("d001", "Ana", "Pérez", "ana@uade.edu.ar", "1234", "M001");

        // Guardar usuarios
        gestor.guardarUsuario(alumno1);
        gestor.guardarUsuario(alumno2);
        gestor.guardarUsuario(docente1);

        // ------------------- Crear curso -------------------
        Curso curso1 = new Curso("c001", "Programación 1", 30, docente1);

        // Guardar curso
        gestor.guardarCurso(curso1);

        System.out.println("Curso creado por " + docente1.getNombre() + ": " + curso1.getTitulo());

        // ------------------- Inscripciones -------------------
        alumno1.inscribirse(curso1);
        alumno2.inscribirse(curso1);

        // ------------------- Guardar curso actualizado -------------------
        // Convertimos a DTO antes de guardar para evitar ciclos
        CursoDTO cursoDTO = new CursoDTO();
        cursoDTO.setIdCurso(curso1.getIdCurso());
        cursoDTO.setTitulo(curso1.getTitulo());
        cursoDTO.setCupoMax(curso1.getCupoMax());
        cursoDTO.setIdDocente(docente1.getIdUsuario());

        List<String> alumnosIds = new ArrayList<>();
        for (Inscripcion i : curso1.getInscripciones()) {
            alumnosIds.add(i.getAlumno().getIdUsuario());
        }
        cursoDTO.setAlumnosIds(alumnosIds);

        System.out.println("Alumnos inscriptos al curso:");
        for (String id : cursoDTO.getAlumnosIds()) {
            System.out.println("- " + id);
        }

        // ------------------- Cargar datos -------------------
        List<Curso>  cursosCargados = gestor.cargarCursos();
        System.out.println("Cursos cargados desde JSON:");
        for (Curso c : cursosCargados) {
            System.out.println(c.getIdCurso() + " - " + c.getTitulo() + " (Docente: " + c.getDocente().getNombre() + ")");
        }

        List<Usuario> usuariosCargados = gestor.cargarUsuarios();
        System.out.println("Usuarios cargados desde JSON:");
        for (Usuario u : usuariosCargados) {
            System.out.println(u.getIdUsuario() + " - " + u.getNombre() + " " + u.getApellido());
        }
    }
}
