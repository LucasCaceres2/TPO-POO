package main.vistas.menuDocente;

import main.vistas.menuPrincipal.formLogin;

import javax.swing.*;

public class formMenuDocente extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;

    private JButton misCursosButton;
    private JButton tomarAsistenciaButton;
    private JButton cargarCalificacionesButton;
    private JButton verHistorialDeAlumnosButton;
    private JButton miPerfilButton;
    private JButton cerrarButton;

    private final String emailDocente; // viene del login

    // ===== Constructor real =====
    public formMenuDocente(String emailDocente) {
        this.emailDocente = emailDocente;

        setContentPane(pnlPrincipal);
        setTitle("Menú Docente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();

        initListeners();

        setSize(600, 500);

// centrar en pantalla
        setLocationRelativeTo(null);
    }

    // SOLO para el diseñador de IntelliJ
    public formMenuDocente() {
        this(null);
    }

    // ===== Listeners de botones =====
    private void initListeners() {

        // Mis Cursos
        misCursosButton.addActionListener(e -> {
            if (!validarEmail()) return;
            new formMisCursosDocente(emailDocente).setVisible(true);
        });

        // Tomar asistencia
        tomarAsistenciaButton.addActionListener(e -> {
            if (!validarEmail()) return;
            new formTomarAsistenciaDocente(emailDocente).setVisible(true);
        });

        // Cargar calificaciones
        cargarCalificacionesButton.addActionListener(e -> {
            if (!validarEmail()) return;
            new formCargarCalificacionesDocente(emailDocente).setVisible(true);
        });

        // Ver historial de alumnos / rendimiento
        verHistorialDeAlumnosButton.addActionListener(e -> {
            if (!validarEmail()) return;
            new formHistorialAlumnosDocente(emailDocente).setVisible(true);
        });

        // Mi perfil
        miPerfilButton.addActionListener(e -> {
            if (!validarEmail()) return;
            new formMiPerfilDocente(emailDocente).setVisible(true);
        });

        // Cerrar sesión
        cerrarButton.addActionListener(e -> {
            dispose();
            new formLogin().setVisible(true);
        });
    }

    private boolean validarEmail() {
        if (emailDocente == null || emailDocente.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el email del docente logueado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // ===== Main de prueba rápida =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formMenuDocente("laura.doc@correo.com").setVisible(true)
        );
    }
}
