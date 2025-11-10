package main.vistas.menuPrincipal;

import main.controlador.ControladorRegistro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class formRegistro extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTextField textField1;      // Nombre
    private JTextField textField2;      // Apellido
    private JTextField textField3;      // Email
    private JTextField textField4;      // Contraseña
    private JTextField textField5;      // Legajo o Matrícula
    private JButton button1;            // Registrar
    private JCheckBox alumnoCheckBox;   // Es alumno
    private JCheckBox docenteCheckBox;  // Es docente
    private JLabel lblMensaje;          // (agregalo en el form para mostrar mensajes)

    private final ControladorRegistro controlador = new ControladorRegistro();

    public formRegistro() {
        setContentPane(pnlPrincipal);
        setTitle("Registro de Usuario - Plataforma de Cursos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();

        configurarEventos();
    }

    private void configurarEventos() {
        // Que solo se pueda seleccionar uno de los dos checkboxes
        alumnoCheckBox.addActionListener(e -> {
            if (alumnoCheckBox.isSelected()) {
                docenteCheckBox.setSelected(false);
            }
        });

        docenteCheckBox.addActionListener(e -> {
            if (docenteCheckBox.isSelected()) {
                alumnoCheckBox.setSelected(false);
            }
        });

        // Acción del botón Registrar
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRegistrar();
            }
        });
    }

    private void onRegistrar() {
        String nombre = textField1.getText();
        String apellido = textField2.getText();
        String email = textField3.getText();
        String contrasena = textField4.getText();
        String legajoOMatricula = textField5.getText();
        boolean esAlumno = alumnoCheckBox.isSelected();
        boolean esDocente = docenteCheckBox.isSelected();

        String resultado = controlador.registrarUsuario(
                nombre,
                apellido,
                email,
                contrasena,
                legajoOMatricula,
                esAlumno,
                esDocente
        );

        if ("REGISTRO_OK".equals(resultado)) {
            mostrarMensaje("✅ Registro realizado correctamente.", false);
            limpiarCampos();
        } else if (resultado != null && resultado.startsWith("ERROR:")) {
            mostrarMensaje("⚠ " + resultado.substring(6), true);
        } else {
            mostrarMensaje("⚠ Ocurrió un error desconocido.", true);
        }
    }

    private void mostrarMensaje(String msg, boolean esError) {
        if (lblMensaje != null) {
            lblMensaje.setText(msg);
            lblMensaje.setForeground(esError ? Color.RED : new Color(0, 128, 0));
        } else {
            // Por si todavía no agregaste el label en el diseñador
            JOptionPane.showMessageDialog(this, msg,
                    esError ? "Error" : "Información",
                    esError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void limpiarCampos() {
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        textField4.setText("");
        textField5.setText("");
        alumnoCheckBox.setSelected(false);
        docenteCheckBox.setSelected(false);
        textField1.requestFocus();
    }

    // Para probar el form solo
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formRegistro().setVisible(true));
    }

}
