package main.app;

import main.modelo.*;
import main.servicios.*;
import java.time.LocalDate;
/*
public class Main {
    public static void main(String[] args) {

        // Crear plataforma
        Plataforma plataforma = new Plataforma("EduMaster");

        // Crear áreas
        Area areaProgramacion = new Area("A1", "Programación");
        Area areaMarketing = new Area("A2", "Marketing Digital");
        plataforma.agregarArea(areaProgramacion);
        plataforma.agregarArea(areaMarketing);

        // Crear docentes
        Docente docente1 = new Docente("Juan", "Pérez", "D100", "Desarrollo Web");
        Docente docente2 = new Docente("Laura", "Gómez", "D200", "SEO y Publicidad");

        // Crear cursos
        Curso cursoJava = new Curso("C001", "Java desde Cero", docente1);
        Curso cursoSEO = new Curso("C002", "SEO para Principiantes", docente2);

        areaProgramacion.agregarCurso(cursoJava);
        areaMarketing.agregarCurso(cursoSEO);

        // Crear contenidos
        Contenido c1 = new Contenido("CT01", "video", "Introducción a Java");
        Contenido c2 = new Contenido("CT02", "texto", "Conceptos básicos de programación");
        cursoJava.agregarContenido(c1);
        cursoJava.agregarContenido(c2);

        // Crear alumnos
        Alumno alumno1 = new Alumno("María", "López", "A100");
        Alumno alumno2 = new Alumno("Carlos", "Fernández", "A200");

        // Inscribir alumnos
        Inscripcion inscripcion1 = new Inscripcion(alumno1, cursoJava, LocalDate.now());
        Inscripcion inscripcion2 = new Inscripcion(alumno2, cursoSEO, LocalDate.now());
        plataforma.registrarInscripcion(inscripcion1);
        plataforma.registrarInscripcion(inscripcion2);

        // Registrar pagos
        Pago pago1 = new Pago(alumno1, 15000.0, LocalDate.now());
        Pago pago2 = new Pago(alumno2, 18000.0, LocalDate.now());
        plataforma.registrarPago(pago1);
        plataforma.registrarPago(pago2);

        // Mostrar datos generales
        System.out.println("\n=== INFORMACIÓN GENERAL ===");
        plataforma.listarAreas();
        System.out.println();
        areaProgramacion.listarCursos();
        System.out.println();
        cursoJava.mostrarContenidos();

        // Mostrar resumen de inscripciones y pagos
        System.out.println("\n=== INSCRIPCIONES REGISTRADAS ===");
        for (Inscripcion i : plataforma.getInscripciones()) {
            System.out.println(i.getAlumno().getNombre() + " → " + i.getCurso().getTitulo());
        }

        System.out.println("\n=== PAGOS REGISTRADOS ===");
        for (Pago p : plataforma.getPagos()) {
            System.out.println(p.getAlumno().getNombre() + " pagó $" + p.getMonto());
        }

        System.out.println("\n✅ Sistema de gestión de cursos online funcionando correctamente.");
    }
}
*/