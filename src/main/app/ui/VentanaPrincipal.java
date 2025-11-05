package main.app.ui;

import main.modelo.Alumno;
import main.modelo.Docente;

import javax.swing.*;

public class VentanaPrincipal {
    private JPanel panel1;
    private JTabbedPane tabPrincipal;
    private JTabbedPane ventanaPrincipal;
    private JTextField userMailL;
    private JPasswordField userPassL;
    private JButton ingresarButton;
    private JPasswordField userConfPassR;
    private JTabbedPane gestion;
    private JTabbedPane gestionCursos;
    private JTabbedPane gestionAreas;
    private JTextField userMailR;
    private JPasswordField userPassR;
    private JButton crearCuentaButton;
    private JLabel tituloR;
    private JLabel tituloL;
    private JLabel errorMensaje;

    private Alumno alumnoLogueado = null;
    private Docente docenteLogueado = null;

}
