package main.menu;

import main.servicios.Plataforma;
import java.util.Scanner;

public class MenuLogin {
    private final Plataforma plataforma;
    private final Scanner sc = new Scanner(System.in);

    public MenuLogin(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    public void mostrarMenuPrincipal() {
        while (true) {
            System.out.println("\n=== BIENVENIDO A LA PLATAFORMA ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrarse");
            System.out.println("0. Salir");
            System.out.print("Elegí una opción: ");
            int opcion = sc.nextInt();
            sc.nextLine(); // limpiar buffer

            switch (opcion) {
                case 1 -> login();
                case 2 -> registrarse();
                case 0 -> {
                    System.out.println("Saliendo...");
                    System.exit(0);
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void login() {
        System.out.print("Ingrese su email: ");
        String email = sc.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String password = sc.nextLine();

        String rol = plataforma.validarLogin(email, password);

        if (rol == null) {
            System.out.println("❌ Usuario o contraseña incorrectos.");
            return; // vuelve al menú principal
        }

        // Usuario válido: ir directamente a su menú
        switch (rol) {
            case "ALUMNO" -> new MenuAlumno(plataforma)
                    .mostrarMenu(plataforma.obtenerLegajoPorEmail(email));
            case "DOCENTE" -> new MenuDocente(plataforma)
                    .mostrarMenu(plataforma.obtenerMatriculaPorEmail(email));
            case "ADMIN" -> new MenuAdmin(plataforma)
                    .mostrarMenu();
            default -> System.out.println("⚠️ Rol no reconocido.");
        }

        System.exit(0); // <--- sale del login para que no vuelva al menú
    }

    private void registrarse() {
        new MenuRegistro(plataforma).mostrarMenuRegistro();
        // Después del registro, pasamos al login
        System.out.println("\n✅ Ahora podés iniciar sesión.");
        mostrarMenuPrincipal(); // opcional, para volver a login
    }
}
