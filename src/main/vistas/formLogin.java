package main.vistas;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import main.controlador.ControladorLogin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class formLogin extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTextField textField1;   // Email
    private JTextField textField2;   // Contraseña (en tu .form es JTextField)
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
        String contrasena = textField2.getText(); // como es JTextField común

        String resultado = controlador.login(email, contrasena);

        switch (resultado) {
            case "ALUMNO": {
                String emailAlumno = email;

                JOptionPane.showMessageDialog(this,
                        "Inicio de sesión correcto (ALUMNO).",
                        "Login",
                        JOptionPane.INFORMATION_MESSAGE);

                formMenuAlumno menuAlumno = new formMenuAlumno(emailAlumno);
                menuAlumno.setVisible(true);

                dispose();
                break;
            }

            case "DOCENTE": {
                JOptionPane.showMessageDialog(this,
                        "Inicio de sesión correcto (DOCENTE).",
                        "Login",
                        JOptionPane.INFORMATION_MESSAGE);

                // cuando tengas el menú de docente:
                // formMenuDocente menuDocente = new formMenuDocente(email);
                // menuDocente.setVisible(true);
                // dispose();

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

    // para probar solo el login:
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formLogin().setVisible(true));
    }

}
