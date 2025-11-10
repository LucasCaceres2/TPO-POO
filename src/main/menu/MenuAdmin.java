package main.menu;

import main.modelo.Alumno;
import main.modelo.Area;
import main.modelo.Curso;
import main.modelo.Docente;
import main.servicios.Plataforma;
import java.util.List;
import java.util.Scanner;

public class MenuAdmin {
    private final Plataforma plataforma;
    private final Scanner sc = new Scanner(System.in);

    public MenuAdmin(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n=== MENÚ ADMIN ===");
            System.out.println("1. Manejar usuarios");
            System.out.println("2. Manejar cursos");
            System.out.println("3. Manejar áreas");
            System.out.println("0. Cerrar sesion");
            System.out.print("Elegí una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> menuUsuarios();
                case 2 -> menuCursos();
                case 3 -> menuAreas();
            }

        } while (opcion != 0);
    }

    // ----------------- MENÚ USUARIOS -----------------
    private void menuUsuarios() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ USUARIOS ---");
            System.out.println("1. Crear Alumno");
            System.out.println("2. Listar Alumnos");
            System.out.println("3. Actualizar Alumno");
            System.out.println("4. Eliminar Alumno");
            System.out.println("5. Crear Docente");
            System.out.println("6. Listar Docentes");
            System.out.println("7. Actualizar Docente");
            System.out.println("8. Eliminar Docente");
            System.out.println("0. Volver");
            System.out.print("Elegí una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> crearAlumno();
                case 2 -> listarAlumnos();
                case 3 -> actualizarAlumno();
                case 4 -> eliminarAlumno();
                case 5 -> crearDocente();
                case 6 -> listarDocentes();
                case 7 -> actualizarDocente();
                case 8 -> eliminarDocente();
            }

        } while (opcion != 0);
    }

    // ----------------- MENÚ CURSOS -----------------
    private void menuCursos() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ CURSOS ---");
            System.out.println("1. Crear Curso");
            System.out.println("2. Listar Cursos");
            System.out.println("3. Actualizar Curso");
            System.out.println("4. Eliminar Curso");
            System.out.println("0. Volver");
            System.out.print("Elegí una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> crearCurso();
                case 2 -> listarCursos();
                case 3 -> actualizarCurso();
                case 4 -> eliminarCurso();
            }

        } while (opcion != 0);
    }

    // ----------------- MENÚ ÁREAS -----------------
    private void menuAreas() {
        int opcion;
        do {
            System.out.println("\n=== MENÚ ÁREAS ===");
            System.out.println("1. Listar áreas");
            System.out.println("2. Crear área");
            System.out.println("3. Actualizar área");
            System.out.println("4. Eliminar área");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elegí una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> listarAreasMenu();
                case 2 -> crearAreaMenu();
                case 3 -> actualizarAreaMenu();
                case 4 -> eliminarAreaMenu();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    // ----------------- FUNCIONES DE USUARIOS -----------------
    private void crearAlumno() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Apellido: ");
        String apellido = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();

        if (plataforma.registrarAlumno(nombre, apellido, email, pass)) {
            System.out.println("✅ Alumno creado correctamente.");
        } else {
            System.out.println("❌ No se pudo crear el alumno.");
        }
    }

    private void listarAlumnos() {
        List<Alumno> alumnos = plataforma.listarAlumnos(); // devuelve List<Alumno>
        if (alumnos.isEmpty()) {
            System.out.println("No hay alumnos registrados.");
        } else {
            System.out.println("📋 Alumnos:");
            for (Alumno a : alumnos) {
                System.out.println("Legajo: " + a.getLegajo() + " | Nombre: " + a.getNombre() + " " + a.getApellido() + " | Email: " + a.getEmail());
            }
        }
    }


    private void actualizarAlumno() {
        System.out.print("Legajo del alumno a modificar: ");
        String legajo = sc.nextLine();
        System.out.print("Campo a modificar (nombre/apellido/email/contrasena): ");
        String campo = sc.nextLine();
        System.out.print("Nuevo valor: ");
        String valor = sc.nextLine();

        if (plataforma.actualizarAlumno(legajo, campo, valor)) {
            System.out.println("✅ Alumno actualizado correctamente.");
        } else {
            System.out.println("❌ No se pudo actualizar el alumno.");
        }
    }

    private void eliminarAlumno() {
        System.out.print("Legajo del alumno a eliminar: ");
        String legajo = sc.nextLine();

        if (plataforma.eliminarAlumno(legajo)) {
            System.out.println("✅ Alumno eliminado correctamente.");
        } else {
            System.out.println("❌ No se pudo eliminar el alumno.");
        }
    }

    private void crearDocente() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Apellido: ");
        String apellido = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();
        System.out.print("Matrícula: ");
        String matricula = sc.nextLine();

        if (plataforma.registrarDocente(nombre, apellido, email, pass, matricula)) {
            System.out.println("✅ Docente creado correctamente.");
        } else {
            System.out.println("❌ No se pudo crear el docente.");
        }
    }

    private void listarDocentes() {
        List<Docente> docentes = plataforma.listarDocentes();
        if (docentes.isEmpty()) {
            System.out.println("No hay docentes registrados.");
        } else {
            System.out.println("📋 Docentes:");
            for (Docente d : docentes) {
                System.out.println("Matrícula: " + d.getMatricula() + " | Nombre: " + d.getNombre() + " " + d.getApellido());
            }
        }
    }


    private void actualizarDocente() {
        System.out.print("Matrícula del docente a modificar: ");
        String matricula = sc.nextLine();
        System.out.print("Campo a modificar (nombre/apellido/email/contrasena): ");
        String campo = sc.nextLine();
        System.out.print("Nuevo valor: ");
        String valor = sc.nextLine();

        if (plataforma.actualizarDocente(matricula, campo, valor)) {
            System.out.println("✅ Docente actualizado correctamente.");
        } else {
            System.out.println("❌ No se pudo actualizar el docente.");
        }
    }

    private void eliminarDocente() {
        System.out.print("Matrícula del docente a eliminar: ");
        String matricula = sc.nextLine();

        if (plataforma.eliminarDocente(matricula)) {
            System.out.println("✅ Docente eliminado correctamente.");
        } else {
            System.out.println("❌ No se pudo eliminar el docente.");
        }
    }

    // ----------------- FUNCIONES DE CURSOS -----------------
    private void crearCurso() {
        System.out.print("Título del curso: ");
        String titulo = sc.nextLine();
        System.out.print("Cupo máximo: ");
        int cupo = sc.nextInt();
        sc.nextLine();
        System.out.print("Matrícula docente: ");
        String matricula = sc.nextLine();
        System.out.print("Nombre del área: ");
        String area = sc.nextLine();
        System.out.print("Descripción: ");
        String desc = sc.nextLine();
        System.out.print("Cantidad de clases: ");
        int clases = sc.nextInt();
        sc.nextLine();

        if (plataforma.crearCurso(titulo, cupo, matricula, area, desc, clases)) {
            System.out.println("✅ Curso creado correctamente.");
        } else {
            System.out.println("❌ No se pudo crear el curso.");
        }
    }

    private void listarCursos() {
        List<Curso> cursos = plataforma.listarCursos(); // devuelve List<Curso>
        if (cursos.isEmpty()) {
            System.out.println("No hay cursos registrados.");
        } else {
            System.out.println("📋 Cursos:");
            for (Curso c : cursos) {
                System.out.println("ID: " + c.getIdCurso() + " | Título: " + c.getTitulo() + " | Cupo: " + c.getCupoMax() + " | Docente: " + c.getDocente().getNombre() + " " + c.getDocente().getApellido());
            }
        }
    }

    private void actualizarCurso() {
        // Similar: pedir título o id y campos, luego llamar a plataforma.actualizarCurso()
        System.out.println("Funcionalidad Actualizar Curso (pendiente de implementar).");
    }

    private void eliminarCurso() {
        // Similar: pedir título o id, luego llamar a plataforma.eliminarCurso()
        System.out.println("Funcionalidad Eliminar Curso (pendiente de implementar).");
    }

    // --- Listar áreas ---
    private void listarAreasMenu() {
        List<Area> areas = plataforma.listarAreas();
        if (areas.isEmpty()) {
            System.out.println("No hay áreas registradas.");
        } else {
            System.out.println("📋 Áreas:");
            for (Area a : areas) {
                System.out.println("ID: " + a.getIdArea() + " | Nombre: " + a.getNombre());
            }
        }
    }

    // --- Crear área ---
    private void crearAreaMenu() {
        System.out.print("Ingrese nombre de la nueva área: ");
        String nombre = sc.nextLine();
        if (plataforma.crearArea(nombre)) {
            System.out.println("✅ Área creada correctamente.");
        } else {
            System.out.println("❌ No se pudo crear el área.");
        }
    }

    // --- Actualizar área ---
    private void actualizarAreaMenu() {
        listarAreasMenu();
        System.out.print("Ingrese el ID del área a actualizar: ");
        int idArea = sc.nextInt();
        sc.nextLine();
        System.out.print("Ingrese el nuevo nombre del área: ");
        String nuevoNombre = sc.nextLine();

        if (plataforma.actualizarArea(idArea, nuevoNombre)) {
            System.out.println("✅ Área actualizada correctamente.");
        } else {
            System.out.println("❌ No se pudo actualizar el área.");
        }
    }

    // --- Eliminar área ---
    private void eliminarAreaMenu() {
        listarAreasMenu();
        System.out.print("Ingrese el ID del área a eliminar: ");
        int idArea = sc.nextInt();
        sc.nextLine();

        if (plataforma.eliminarArea(idArea)) {
            System.out.println("🗑️ Área eliminada correctamente.");
        } else {
            System.out.println("❌ No se pudo eliminar el área.");
        }
    }
}