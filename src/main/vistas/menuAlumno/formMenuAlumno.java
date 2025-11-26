package main.vistas.menuAlumno;

import main.controlador.Plataforma;
import main.vistas.menuPrincipal.formLogin;

import javax.swing.*;

public class formMenuAlumno extends JFrame {
    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JButton cursosDisponiblesButton;
    private JButton misCursosButton;
    private JButton historialButton;
    private JButton misPagosButton;
    private JButton miPerfilButton;
    private JButton cerrarSesionButton;
    private JButton miAsistenciaButton;
    private JButton misNotasButton;
    private final Plataforma plataforma = new Plataforma();
    private final String legajoAlumno;

    // Constructor “real”: recibe el email del login
    public formMenuAlumno(String legajoAlumno) {
        this.legajoAlumno = plataforma.obtenerLegajoPorEmail(legajoAlumno);

        if (this.legajoAlumno == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo obtener el legajo del alumno logueado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setContentPane(pnlPrincipal);
        setTitle("Menú Alumno");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setLocationRelativeTo(null);

        initListeners();
    }

    // Constructor vacío SOLO para el diseñador
    public formMenuAlumno() {
        this(null);
    }

    private void initListeners() {


        // Cursos disponibles
        cursosDisponiblesButton.addActionListener(e -> {
            formCursosDisponiblesAlumno frm = new formCursosDisponiblesAlumno(legajoAlumno);
            frm.setVisible(true);
        });

        // ✅ Mis cursos
        misCursosButton.addActionListener(e -> {
            formMisCursosAlumno frm = new formMisCursosAlumno(legajoAlumno);
            frm.setVisible(true);
        });

        // 🔹 Historial (todas las inscripciones)
        historialButton.addActionListener(e ->
                new formHistorialAlumno(legajoAlumno).setVisible(true)
        );

        misPagosButton.addActionListener(e ->
                new formMisPagosAlumno(legajoAlumno).setVisible(true)
        );

        miPerfilButton.addActionListener(e ->
                new formMiPerfilAlumno(legajoAlumno).setVisible(true)
        );

        misNotasButton.addActionListener(e ->
                new formMisNotasAlumno(legajoAlumno).setVisible(true)
        );

        // 🔹 Mi Asistencia
        miAsistenciaButton.addActionListener(e ->
                new formMiAsistenciaAlumno(legajoAlumno).setVisible(true)
        );

        // Cerrar sesión
        cerrarSesionButton.addActionListener(e -> {
            dispose(); // cierro menú
            new formLogin().setVisible(true); // vuelvo al login
        });

    }
}
