package main.app;

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

        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(alumno1);
        usuarios.add(alumno2);
        usuarios.add(docente1);

        // ------------------- Crear curso -------------------
        Curso curso1 = new Curso(
                "c001",
                "Programación 1",
                30,
                docente1,
                "Introducción a variables, condicionales, bucles y arreglos."
        );

        // ------------------- Inscribir alumnos -------------------
        // REEMPLAZA curso1.inscribirAlumno(alumno1);
        alumno1.inscribirse(curso1);
        alumno2.inscribirse(curso1);

        List<Curso> cursos = new ArrayList<>();
        cursos.add(curso1);

        // ------------------- Guardar datos -------------------
        gestor.guardarUsuarios(usuarios);
        gestor.guardarCursos(cursos);

        // ------------------- Cargar datos -------------------
        List<Curso> cursosCargados = gestor.cargarCursos();
        System.out.println("Cursos cargados desde JSON:");
        for (Curso c : cursosCargados) {
            System.out.println(
                    c.getIdCurso() + " - " + c.getTitulo() +
                            " (Docente: " + (c.getDocente() != null ? c.getDocente().getNombre() : "N/A") + ")" +
                            " | Contenido: " + c.getContenido()
            );
        }

        List<Usuario> usuariosCargados = gestor.cargarUsuarios();
        System.out.println("Usuarios cargados desde JSON:");
        for (Usuario u : usuariosCargados) {
            System.out.println(u.getIdUsuario() + " - " + u.getNombre() + " " + u.getApellido());
        }
    }
}
