package main.vistas.menuAdministrador;

import main.vistas.menuPrincipal.formLogin;
import main.vistas.menuAdministrador.formAdminAlumnos;
import main.vistas.menuAdministrador.formAdminDocentes;
import main.vistas.menuAdministrador.formGestionCursos;
import main.vistas.menuAdministrador.formVerInscripciones;

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

    private final String emailAdmin; // opcional

    // Constructor real
    public formMenuAdmin(String emailAdmin) {
        this.emailAdmin = emailAdmin;

        setContentPane(pnlPrincipal);
        setTitle("Menú Administrador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600, 500);
        setLocationRelativeTo(null);

        initListeners();
    }

    // SOLO para diseñador
    public formMenuAdmin() {
        this(null);
    }

    private void initListeners() {

        // 👉 Gestión de Alumnos
        gestionAlumnosButton.addActionListener(e -> {
            formAdminAlumnos frm = new formAdminAlumnos();
            frm.setVisible(true);
        });

        // 👉 Gestión de Docentes
        gestionDocentesButton.addActionListener(e -> {
            formAdminDocentes frm = new formAdminDocentes();
            frm.setVisible(true);
        });

        // 👉 Gestión de Cursos
        gestionCursosButton.addActionListener(e -> {
            formGestionCursos frm = new formGestionCursos();
            frm.setVisible(true);
        });

        // 👉 Ver Inscripciones
        verInscripcionesButton.addActionListener(e -> {
            formVerInscripciones frm = new formVerInscripciones();
            frm.setVisible(true);
        });

        // 👉 Cerrar sesión
        cerrarSesionButton.addActionListener(e -> {
            dispose();
            new formLogin().setVisible(true);
        });
    }

    // main de prueba
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formMenuAdmin("admin@correo.com").setVisible(true)
        );
    }
}
