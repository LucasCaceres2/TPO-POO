package main.vistas.menuAdministrador;

import main.modelo.Administrador;
import main.vistas.menuPrincipal.formLogin;

import javax.swing.*;

public class formMenuAdmin extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;

    private JButton gestionAlumnosButton;
    private JButton gestionDocentesButton;
    private JButton gestionAreasButton;
    private JButton gestionCursosButton;
    private JButton verInscripcionesButton;
    private JButton cerrarSesionButton;

    private Administrador admin; // 👈 ahora guardamos el admin (opcional)

    // Constructor real
    public formMenuAdmin(Administrador admin) {
        this.admin = admin;

        setContentPane(pnlPrincipal);
        if (admin != null) {
            setTitle("Menú Administrador - " + admin.getNombre());
        } else {
            setTitle("Menú Administrador");
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600, 500);
        setLocationRelativeTo(null);

        initListeners();
    }

    // SOLO para diseñador / pruebas
    public formMenuAdmin() {
        this(null);
    }

    private void initListeners() {
        gestionAlumnosButton.addActionListener(e -> new formAdminAlumnos().setVisible(true));
        gestionDocentesButton.addActionListener(e -> new formAdminDocentes().setVisible(true));
        gestionAreasButton.addActionListener(e -> new formGestionAreas().setVisible(true));
        gestionCursosButton.addActionListener(e -> new formGestionCursos().setVisible(true));
        verInscripcionesButton.addActionListener(e -> new formVerInscripciones().setVisible(true));

        cerrarSesionButton.addActionListener(e -> {
            dispose();
            new formLogin().setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formMenuAdmin().setVisible(true)
        );
    }
}
