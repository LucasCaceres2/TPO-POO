package main.app;

import main.vistas.formBienvenido;
import main.vistas.formLogin;

import javax.swing.*;

public class Principal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formBienvenido().setVisible(true));
    }
}
