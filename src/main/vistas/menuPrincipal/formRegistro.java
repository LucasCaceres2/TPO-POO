package main.vistas.menuPrincipal;

import main.controlador.ControladorRegistro;

import javax.swing.*;

public class formRegistro extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTextField textNombre;
    private JTextField textApellido;
    private JTextField textEmail;
    private JTextField textContraseña;
    private JTextField textRepContraseña;
    private JButton crearButton;
    private JButton volverButton;

    private final ControladorRegistro controlador = new ControladorRegistro();

    public formRegistro() {
        setContentPane(pnlPrincipal);
        setTitle("Registro de Usuario - Plataforma de Cursos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setLocationRelativeTo(null);

        configurarEventos();
    }

    // ================== EVENTOS ==================
    private void configurarEventos() {

        // Botón CREAR
        crearButton.addActionListener(e -> onRegistrar());

        // Botón VOLVER -> regresar a la pantalla Bienvenido
        volverButton.addActionListener(e -> {
            dispose();                               // cierro este form
            new formBienvenido().setVisible(true);   // vuelvo a la pantalla de inicio
        });
    }

    // ================== LÓGICA DE REGISTRO ==================
    private void onRegistrar() {
        String nombre      = textNombre.getText().trim();
        String apellido    = textApellido.getText().trim();
        String email       = textEmail.getText().trim();
        String contrasena  = textContraseña.getText();
        String repContra   = textRepContraseña.getText();

        // 1) Validar que las contraseñas coincidan
        if (!contrasena.equals(repContra)) {
            mostrarMensaje("Las contraseñas no coinciden.", true);
            return;
        }

        // 2) Llamar al controlador
        String resultado = controlador.registrarAlumno(
                nombre,
                apellido,
                email,
                contrasena
        );

        // 3) Interpretar el resultado
        if ("REGISTRO_OK".equals(resultado)) {
            mostrarMensaje("Registro realizado correctamente.", false);

            new main.vistas.menuAlumno.formMenuAlumno(email).setVisible(true);
            dispose();

        } else if (resultado != null && resultado.startsWith("ERROR:")) {
            mostrarMensaje(resultado.substring(6), true);
        } else {
            mostrarMensaje("Ocurrió un error desconocido.", true);
        }
    }

    // ================== UTILIDADES ==================
    private void mostrarMensaje(String msg, boolean esError) {
        JOptionPane.showMessageDialog(
                this,
                msg,
                esError ? "Error" : "Información",
                esError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void limpiarCampos() {
        textNombre.setText("");
        textApellido.setText("");
        textEmail.setText("");
        textContraseña.setText("");
        textRepContraseña.setText("");
        textNombre.requestFocus();
    }

    // Para probar este form directamente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formRegistro().setVisible(true));
    }
}
