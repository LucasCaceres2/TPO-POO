package main.vistas.menuPrincipal;

import main.controlador.ControladorLogin;
import main.vistas.menuAlumno.formMenuAlumno;
import main.vistas.menuDocente.formMenuDocente;   // 👈 IMPORTANTE

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class formLogin extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTextField textField1;   // Email
    private JTextField textField2;   // Contraseña
    private JButton iniciarSesionButton;

    private final ControladorLogin controlador = new ControladorLogin();

    public formLogin() {
        setContentPane(pnlPrincipal);
        setTitle("Login - Plataforma de Cursos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();

        iniciarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin();
            }
        });
    }

    private void onLogin() {
        String email = textField1.getText();
        String contrasena = textField2.getText();

        String resultado = controlador.login(email, contrasena);

        switch (resultado) {
            case "ALUMNO": {
                JOptionPane.showMessageDialog(this,
                        "Inicio de sesión correcto (ALUMNO).",
                        "Login",
                        JOptionPane.INFORMATION_MESSAGE);

                new formMenuAlumno(email).setVisible(true);
                dispose();
                break;
            }

            case "DOCENTE": {
                JOptionPane.showMessageDialog(this,
                        "Inicio de sesión correcto (DOCENTE).",
                        "Login",
                        JOptionPane.INFORMATION_MESSAGE);

                new formMenuDocente(email).setVisible(true); // 👈 abre menú docente
                dispose();
                break;
            }

            case "ERROR_VACIO":
                JOptionPane.showMessageDialog(this,
                        "Ingresá email y contraseña.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;

            case "ERROR_CREDENCIALES":
                JOptionPane.showMessageDialog(this,
                        "Email o contraseña incorrectos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;

            case "ERROR_BD":
            default:
                JOptionPane.showMessageDialog(this,
                        "Error al conectar con la base de datos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formLogin().setVisible(true));
    }
}
