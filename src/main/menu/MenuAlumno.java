package main.menu;

import main.servicios.Plataforma;
import java.util.Scanner;

public class MenuAlumno {
    private final Plataforma plataforma;
    private final Scanner sc = new Scanner(System.in);

    public MenuAlumno(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    public void mostrarMenu(String legajoAlumno) {
        int opcion;
        do {
            System.out.println("\n=== MENÚ ALUMNO ===");
            System.out.println("1. Ver cursos");
            System.out.println("2. Ver asistencias");
            System.out.println("3. Ver calificaciones");
            System.out.println("4. Ver rendimiento");
            System.out.println("5. Inscribirse a un curso");
            System.out.println("6. Darse de baja de un curso");
            System.out.println("0. Salir");
            System.out.print("Elegí una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> plataforma.mostrarCursosDeAlumno(legajoAlumno);
                case 2 -> plataforma.mostrarAsistencias(legajoAlumno);
                case 3 -> plataforma.mostrarCalificaciones(legajoAlumno);
                case 4 -> plataforma.mostrarRendimiento(legajoAlumno);
                case 5 -> {
                    if (plataforma.inscribirAlumnoPorArea(legajoAlumno)) {
                        System.out.println("✅ Inscripción realizada con éxito.");
                    } else {
                        System.out.println("❌ No se pudo inscribir al curso.");
                    }
                }
                case 6 -> {
                    System.out.print("Ingrese título del curso para darse de baja: ");
                    String curso = sc.nextLine();
                    if (plataforma.darseDeBajaCurso(legajoAlumno, curso)) {
                        System.out.println("✅ Baja realizada con éxito.");
                    }
                }
            }
        } while (opcion != 0);
    }
}
