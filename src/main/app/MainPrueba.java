package main.app;

import main.modelo.*;
import main.persistencia.GestorDePersistencia;

import java.util.Date;

public class MainPrueba {

    public static void main(String[] args) {

        GestorDePersistencia gestor = new GestorDePersistencia();

        // ----------------- 1. CREAR USUARIOS -----------------
        System.out.println("📌 BLOQUE 1: Crear usuarios");
        Alumno a1 = new Alumno("A001", "Lucas", "Cáceres", "lucas@mail.com", "pass", "L001");
        Alumno a2 = new Alumno("A002", "Martina", "Gómez", "martina@mail.com", "pass", "L002");
        Docente d1 = new Docente("D001", "Ana", "Pérez", "ana@mail.com", "pass", "M001");

        gestor.guardarUsuario(a1);
        gestor.guardarUsuario(a2);
        gestor.guardarUsuario(d1);

        System.out.println("📘 Usuarios actuales:");
        for (Usuario u : gestor.cargarUsuarios()) {
            System.out.println(u.getNombre() + " - " + u.getTipoUsuario());
        }

        // ----------------- 2. CREAR ÁREAS -----------------
        System.out.println("\n📌 BLOQUE 2: Crear áreas");
        Area area1 = new Area("AR001", "Programación");
        Area area2 = new Area("AR002", "Bases de Datos");

        gestor.guardarArea(area1);
        gestor.guardarArea(area2);

        System.out.println("📘 Áreas actuales:");
        for (Area a : gestor.cargarAreas()) {
            System.out.println(a.getNombre());
        }

        // ----------------- 3. CREAR CURSOS -----------------
        System.out.println("\n📌 BLOQUE 3: Crear cursos");
        Curso c1 = new Curso("C001", "Java", 20, d1, area1, "Contenido Java");
        Curso c2 = new Curso("C002", "Base de Datos", 15, d1, area2, "Contenido BD");

        gestor.guardarCurso(c1);
        gestor.guardarCurso(c2);

        System.out.println("📘 Cursos actuales:");
        for (Curso c : gestor.cargarCursos()) {
            System.out.println(c.getTitulo() + " - Área: " +
                    (c.getArea() != null ? c.getArea().getNombre() : "Sin área") +
                    " - Docente: " + c.getDocente().getNombre());
        }

        // ----------------- 4. CREAR INSCRIPCIONES -----------------
        System.out.println("\n📌 BLOQUE 4: Crear inscripciones");
        Inscripcion i1 = new Inscripcion(a1, c1); // Estado por defecto PENDIENTE_PAGO
        Inscripcion i2 = new Inscripcion(a2, c2);

        gestor.guardarInscripcion(i1);
        gestor.guardarInscripcion(i2);

        System.out.println("📘 Inscripciones actuales:");
        for (Inscripcion insc : gestor.cargarInscripciones()) {
            System.out.println(insc.getIdInscripcion() + " - Alumno: " +
                    insc.getAlumno().getNombre() + " - Curso: " + insc.getCurso().getTitulo() +
                    " - Estado: " + insc.getEstado());
        }

        // ----------------- 5. REGISTRAR PAGOS -----------------
        System.out.println("\n📌 BLOQUE 5: Registrar pagos");
        Pago p1 = new Pago("P001", new Date(), 5000, a1);
        gestor.registrarPago(i1.getIdInscripcion(), p1);

        System.out.println("📘 Inscripciones después del pago:");
        for (Inscripcion insc : gestor.cargarInscripciones()) {
            System.out.println(insc.getIdInscripcion() + " - Alumno: " +
                    insc.getAlumno().getNombre() + " - Curso: " + insc.getCurso().getTitulo() +
                    " - Estado: " + insc.getEstado() +
                    (insc.getPago() != null ? " - Pagado: " + insc.getPago().getMonto() : ""));
        }

    }
}
