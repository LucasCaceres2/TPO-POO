package main.app;

import main.modelo.Alumno;
import main.modelo.Docente;
import main.persistencia.GestorDePersistencia;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        GestorDePersistencia gestor = new GestorDePersistencia();

        Alumno alumno1 = new Alumno("A001", "Lucas", "Caceres", "lucas@example.com", "1234", "L001");
        Docente docente1 = new Docente("D001", "Juan", "Perez", "juan@example.com", "abcd", "M001");

        // Guardar en JSON
        gestor.guardarUsuario(alumno1);
        gestor.guardarUsuario(docente1);

        // Cargar y mostrar usuarios
        List usuarios = gestor.cargarUsuarios();
        System.out.println("\nUsuarios cargados del JSON:");
        for (Object u : usuarios) {
            System.out.println(u);
        }

        List alumnos = gestor.cargarAlumnos();
        System.out.println("\nAlumnos cargados del JSON:");
        for (Object a : alumnos) {
            System.out.println(a);
        }
    }
}
