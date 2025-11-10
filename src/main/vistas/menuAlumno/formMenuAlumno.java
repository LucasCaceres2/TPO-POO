package main.vistas.menuAlumno;

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

    private final String emailAlumno;

    // Constructor “real”: recibe el email del login
    public formMenuAlumno(String emailAlumno) {
        this.emailAlumno = emailAlumno;

        setContentPane(pnlPrincipal);
        setTitle("Menú Alumno");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();

        initListeners();
    }

    // Constructor vacío SOLO para el diseñador
    public formMenuAlumno() {
        this(null);
    }

    private void initListeners() {


        // Cursos disponibles
        cursosDisponiblesButton.addActionListener(e -> {
            formCursosDisponiblesAlumno frm = new formCursosDisponiblesAlumno(emailAlumno);
            frm.setVisible(true);
        });

        // ✅ Mis cursos
        misCursosButton.addActionListener(e -> {
            formMisCursosAlumno frm = new formMisCursosAlumno(emailAlumno);
            frm.setVisible(true);
        });

        // 🔹 Historial (todas las inscripciones)
        historialButton.addActionListener(e ->
                new formHistorialAlumno(emailAlumno).setVisible(true)
        );

        misPagosButton.addActionListener(e ->
                new formMisPagosAlumno(emailAlumno).setVisible(true)
        );

        miPerfilButton.addActionListener(e ->
                new formMiPerfilAlumno(emailAlumno).setVisible(true)
        );


        // Cerrar sesión
        cerrarSesionButton.addActionListener(e -> {
            dispose(); // cierro menú
            new formLogin().setVisible(true); // vuelvo al login
        });

        // (historial, pagos, perfil: los implementás después igual que estos)
    }
}
