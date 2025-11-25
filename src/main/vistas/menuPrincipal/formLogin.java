package main.vistas.menuPrincipal;

import main.controlador.ControladorLogin;
import main.modelo.TipoUsuario;
import main.vistas.menuAlumno.formMenuAlumno;
import main.vistas.menuDocente.formMenuDocente;
import main.vistas.menuAdministrador.formMenuAdmin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static main.modelo.TipoUsuario.*;

public class formLogin extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTextField textField1;   // Email
    private JTextField textField2;   // Contraseña
    private JButton iniciarSesionButton;
    private JButton volverButton;

    private final ControladorLogin controlador = new ControladorLogin();

    public formLogin() {
        setContentPane(pnlPrincipal);
        setTitle("Login - Plataforma de Cursos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setLocationRelativeTo(null);

        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin();
            }
        });

        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();                     // cierro el login
                new formBienvenido().setVisible(true);  // vuelvo a la pantalla de inicio
            }
        });
    }


    private void onLogin() {
        String email = textField1.getText();
        String contrasena = textField2.getText();

        TipoUsuario tipo = controlador.login(email, contrasena);

        if (tipo == null) {
            JOptionPane.showMessageDialog(this,
                    "Email o contraseña incorrectos, o usuario inactivo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Inicio de sesión correcto (" + tipo + ").",
                "Login",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();

        switch (tipo) {
            case ALUMNO -> new formMenuAlumno(email).setVisible(true);
            case DOCENTE -> new formMenuDocente(email).setVisible(true);
            case ADMIN -> new formMenuAdmin(email).setVisible(true);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formLogin().setVisible(true));
    }
}
