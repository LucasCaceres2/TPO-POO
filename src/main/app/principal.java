package main.app;

import main.vistas.formLogin;
import main.vistas.formRegistro;

import javax.swing.*;

public class principal {
    public static void main(String[] args) {



        SwingUtilities.invokeLater(() -> new formLogin().setVisible(true));
    }

}
