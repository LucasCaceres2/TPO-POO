package main.vistas.menuPrincipal;

import javax.swing.*;

public class formBienvenido extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JButton crearCuentaButton;
    private JButton iniciarSesionButton;

    public formBienvenido() {
        setContentPane(pnlPrincipal);
        setTitle("Bienvenido - Plataforma de Cursos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setLocationRelativeTo(null);

        // Botón CREAR CUENTA -> abre formulario de registro
        crearCuentaButton.addActionListener(e -> {
            formRegistro registro = new formRegistro();
            registro.setVisible(true);
            dispose(); // cierra esta ventana (opcional: usar setVisible(false) si querés volver después)
        });

        // Botón INICIAR SESIÓN -> abre formulario de login
        iniciarSesionButton.addActionListener(e -> {
            formLogin login = new formLogin();
            login.setVisible(true);
            dispose();
        });
    }

    // Para iniciar la app desde acá
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formBienvenido().setVisible(true));
    }

}