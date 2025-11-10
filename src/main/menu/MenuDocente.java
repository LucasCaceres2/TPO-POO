package main.menu;

import main.modelo.Alumno;
import main.modelo.Curso;
import main.modelo.TipoEvaluacion;
import main.servicios.Plataforma;

import java.util.List;
import java.util.Scanner;

public class MenuDocente {
    private final Plataforma plataforma;
    private final Scanner sc = new Scanner(System.in);

    public MenuDocente(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    public void mostrarMenu(String matriculaDocente) {
        int opcion;
        do {
            System.out.println("\n=== MENÚ DOCENTE ===");
            System.out.println("1. Ver cursos ");
            System.out.println("2. Gestionar curso (asistencia / notas)");
            System.out.println("3. Ver perfil");
            System.out.println("0. Salir");
            System.out.print("Elegí una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> listarCursos(matriculaDocente);
                case 2 -> seleccionarCurso(matriculaDocente);
                case 3 -> verPerfil(matriculaDocente);
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    private void listarCursos(String matricula) {
        List<Curso> cursos = plataforma.listarCursosPorDocente(matricula);
        if (cursos.isEmpty()) {
            System.out.println("No dictás ningún curso actualmente.");
        } else {
            System.out.println("📘 Cursos que dictás:");
            for (Curso c : cursos) {
                System.out.println("ID: " + c.getIdCurso() + " | Título: " + c.getTitulo());
            }
        }
    }

    private void seleccionarCurso(String matricula) {
        listarCursos(matricula);
        System.out.print("Ingrese el ID del curso que desea gestionar: ");
        int idCurso = sc.nextInt();
        sc.nextLine();

        int opcion;
        do {
            System.out.println("\n=== Curso ID: " + idCurso + " ===");
            System.out.println("1. Tomar asistencia");
            System.out.println("2. Registrar calificaciones");
            System.out.println("0. Volver");
            System.out.print("Elegí una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> tomarAsistencia(idCurso);
                case 2 -> registrarCalificaciones(idCurso);
                case 0 -> System.out.println("Volviendo al menú anterior...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    private void tomarAsistencia(int idCurso) {
        List<Alumno> alumnos = plataforma.listarAlumnosInscritos(idCurso);
        if (alumnos.isEmpty()) {
            System.out.println("No hay alumnos inscritos en este curso.");
            return;
        }

        for (Alumno a : alumnos) {
            System.out.print("¿Asistió " + a.getNombre() + " " + a.getApellido() + "? (s/n): ");
            String respuesta = sc.nextLine();
            boolean presente = respuesta.equalsIgnoreCase("s");
            plataforma.tomarAsistencia(a.getLegajo(), idCurso, new java.util.Date(), presente);
        }

        System.out.println("✅ Asistencia registrada correctamente.");
    }

    private void registrarCalificaciones(int idCurso) {
        List<Alumno> alumnos = plataforma.listarAlumnosInscritos(idCurso);
        if (alumnos.isEmpty()) {
            System.out.println("No hay alumnos inscritos en este curso.");
            return;
        }

        for (Alumno a : alumnos) {
            System.out.print("Ingrese nota para " + a.getNombre() + " " + a.getApellido() + ": ");
            double nota = sc.nextDouble();
            sc.nextLine();

            System.out.print("Tipo de evaluación (PARCIAL, FINAL, TRABAJO): ");
            String tipoStr = sc.nextLine().toUpperCase();
            TipoEvaluacion tipo = TipoEvaluacion.valueOf(tipoStr);

            plataforma.registrarCalificacion(a.getLegajo(), idCurso, tipo, nota);
        }

        System.out.println("✅ Calificaciones registradas correctamente.");
    }

    private void verPerfil(String matricula) {
        System.out.println(plataforma.obtenerDocentePorMatricula(matricula));
    }
}
