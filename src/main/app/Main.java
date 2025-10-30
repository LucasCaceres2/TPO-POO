/*
package main.app;

import main.persistencia.GestorDePersistencia;
import main.modelo.*;

import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        GestorDePersistencia gestor = new GestorDePersistencia();

        // 1) Crear usuarios y cursos de prueba
        Alumno alumno1 = new Alumno("A001", "Lucas", "Cáceres", "lucas@mail.com", "pass", "L001");
        Alumno alumno2 = new Alumno("A002", "Martina", "Pérez", "martina@mail.com", "pass", "L002");
        Docente docente1 = new Docente("D001", "Ana", "Gómez", "ana@mail.com", "pass", "M001");

        Curso curso1 = new Curso("C001", "Programación en Java", 30, docente1, null, "Contenido de ejemplo");
        Curso curso2 = new Curso("C002", "Base de Datos", 25, docente1, null, "Contenido BBDD");

        // 2) Guardar usuarios y cursos (persistencia)
        gestor.guardarUsuario(alumno1);
        gestor.guardarUsuario(alumno2);
        gestor.guardarUsuario(docente1);

        gestor.guardarCurso(curso1);
        gestor.guardarCurso(curso2);

        // 3) Crear inscripción (estado por defecto: PENDIENTE_PAGO)
        Inscripcion inscripcion1 = new Inscripcion(alumno1, curso1); // usa constructor sin pago
        gestor.guardarInscripcion(inscripcion1);

        System.out.println("\n📘 Inscripción creada con estado inicial: " + inscripcion1.getEstado());

        // 4) Crear pago (usar java.util.Date porque Pago espera Date)
        Pago pago1 = new Pago("P001", new Date(), 20000.0, alumno1);

        // 5) Registrar pago: esto asocia el pago a la inscripción y actualiza el estado a CURSANDO
        gestor.registrarPago(inscripcion1.getIdInscripcion(), pago1);

        // 6) Comprobar que el pago fue registrado y el estado cambió
        List<Inscripcion> inscripcionesGuardadas = gestor.cargarInscripciones();
        System.out.println("\n📂 Inscripciones guardadas (estado y pago):");
        for (Inscripcion ins : inscripcionesGuardadas) {
            System.out.println(" - ID: " + ins.getIdInscripcion()
                    + " | Alumno: " + ins.getAlumno().getNombre() + " " + ins.getAlumno().getApellido()
                    + " | Curso: " + (ins.getCurso() != null ? ins.getCurso().getTitulo() : "N/A")
                    + " | Estado: " + ins.getEstado()
                    + " | Pago: " + (ins.getPago() != null ? ins.getPago().getIdPago() + " ($" + ins.getPago().getMonto() + ")" : "Sin pago"));
        }
    }
}
*/
