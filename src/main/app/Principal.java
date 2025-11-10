package main.app;

import main.vistas.menuPrincipal.formBienvenido;

import javax.swing.*;

public class Principal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formBienvenido().setVisible(true));
    }
}
