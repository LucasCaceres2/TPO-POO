package main.app;

import main.menu.MenuLogin;
import main.servicios.Plataforma;

public class Main {
    public static void main(String[] args) {

        // Crear una instancia de Plataforma (maneja todos los servicios)
        Plataforma plataforma = new Plataforma();

        // Lanzar el menú principal (login/registro)
        MenuLogin menuLogin = new MenuLogin(plataforma);
        menuLogin.mostrarMenuPrincipal();

        System.out.println("👋 Gracias por usar la plataforma. ¡Hasta luego!");
    }
}
