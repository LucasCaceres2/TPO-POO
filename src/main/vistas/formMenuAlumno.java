package main.vistas;

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

    // email del alumno logueado (opcional por ahora)
    private String emailAlumno;

    // 🔹 Constructor sin parámetros (lo usa el GUI Designer de IntelliJ)
    public formMenuAlumno() {
        this(null);
    }

    // 🔹 Constructor que usamos desde el login
    public formMenuAlumno(String emailAlumno) {
        this.emailAlumno = emailAlumno;

        setContentPane(pnlPrincipal);
        setTitle("Menú Alumno - Plataforma de Cursos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();

        initListeners();
    }

    private void initListeners() {

        cursosDisponiblesButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Aquí se mostrarán los cursos disponibles para inscribirte.",
                    "Cursos disponibles",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        misCursosButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Aquí se mostrarán tus cursos actuales. Alumno: " + emailAlumno,
                    "Mis cursos",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        historialButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Aquí se mostrará tu historial de inscripciones.",
                    "Historial",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        misPagosButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Aquí se mostrarán tus pagos.",
                    "Mis pagos",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        miPerfilButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Aquí se mostrarán tus datos de perfil. Email: " + emailAlumno,
                    "Mi perfil",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        cerrarSesionButton.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que querés cerrar sesión?",
                    "Cerrar sesión",
                    JOptionPane.YES_NO_OPTION);

            if (opcion == JOptionPane.YES_OPTION) {
                formBienvenido bienvenido = new formBienvenido();
                bienvenido.setVisible(true);
                dispose();
            }
        });
    }

    // Para probar solo este form
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formMenuAlumno("alumno@test.com").setVisible(true));
    }
}
