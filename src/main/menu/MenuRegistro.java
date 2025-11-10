package main.menu;

import main.servicios.Plataforma;
import java.util.Scanner;

public class MenuRegistro {
    private final Plataforma plataforma;
    private final Scanner sc = new Scanner(System.in);

    public MenuRegistro(Plataforma plataforma) {
        this.plataforma = plataforma;
    }

    public void mostrarMenuRegistro() {
        System.out.println("\n=== REGISTRARSE (ALUMNOS) ===");

        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Apellido: ");
        String apellido = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = sc.nextLine();

        // Registrar alumno (solo alumnos se registran)
        boolean exito = plataforma.registrarAlumno(nombre, apellido, email, contrasena);

        if (exito) {
            System.out.println("✅ Registro completado con éxito.");
            System.out.println("Podés iniciar sesión ahora.");
        } else {
            System.out.println("❌ No se pudo registrar el alumno. Revisá que el email no esté repetido.");
        }

        // Opcional: volver al menú principal de login
        new MenuLogin(plataforma).mostrarMenuPrincipal();
    }
}
