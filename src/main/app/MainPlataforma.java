package main.app;

import main.servicios.Plataforma;
import main.modelo.Inscripcion;

import java.util.List;

public class MainPlataforma {
    public static void main(String[] args) {
        Plataforma plataforma = new Plataforma();

        System.out.println("===== INICIO DE PRUEBA DE PLATAFORMA =====");

        // ==== CREAR DOCENTE ====
        plataforma.registrarDocente("Martín", "Fernandez", "martin.fernandez@test.com", "1234", "MAT120");

        // ==== CREAR ÁREA ====
        plataforma.crearArea("Data Science");

        // ==== CREAR CURSO ====
        plataforma.crearCurso(
                "Introducción al Machine Learning",
                45,
                "MAT120",
                "Data Science",
                "Curso inicial de aprendizaje automático con Python y scikit-learn.",
                30
        );

        // ==== CREAR ALUMNO ====
        plataforma.registrarAlumno("Sofía", "Lopez", "sofia.lopez@test.com", "abcd", "LEG120");

        // ==== INSCRIBIR ALUMNO EN CURSO ====
        plataforma.inscribirAlumnoEnCurso("LEG120", "Introducción al Machine Learning");

        // ==== REGISTRAR PAGO ====
        plataforma.registrarPago("LEG120", 25000.0);

        // ==== LISTAR INSCRIPCIONES ====
        List<Inscripcion> inscripciones = plataforma.obtenerInscripcionesDeAlumno("LEG120");
        System.out.println("\n--- Inscripciones de Sofía Lopez ---");
        for (Inscripcion i : inscripciones) {
            System.out.println("➡️ " + i.getCurso().getTitulo() +
                    " | EstadoPago: " + i.getEstadoPago() +
                    " | EstadoCurso: " + i.getEstadoCurso());
        }

        System.out.println("\n===== FIN DE PRUEBA =====");
    }
}
