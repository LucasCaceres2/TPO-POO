package main.vistas;

import main.dao.AlumnoDAO;
import main.modelo.Alumno;

import javax.swing.*;

public class formMiPerfilAlumno extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtLegajo;
    private JButton guardarButton;
    private JButton cerrarButton;

    private final String emailAlumno;     // email con el que se logueó
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private Alumno alumno;               // alumno cargado desde BD

    // --------- CONSTRUCTOR PRINCIPAL ----------
    public formMiPerfilAlumno(String emailAlumno) {
        this.emailAlumno = emailAlumno;

        setContentPane(pnlPrincipal);
        setTitle("Mi Perfil");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cargarDatosAlumno();
        initListeners();

        pack();
        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    // SOLO para el diseñador (no usar en producción real)
    public formMiPerfilAlumno() {
        this(null);
    }

    // --------- CARGAR DATOS DESDE BD ----------
    private void cargarDatosAlumno() {
        if (emailAlumno == null || emailAlumno.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el email del alumno logueado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        alumno = alumnoDAO.obtenerAlumnoPorEmail(emailAlumno);

        if (alumno == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el perfil del alumno en la base de datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Seteamos los campos con los datos actuales
        txtNombre.setText(alumno.getNombre());
        txtApellido.setText(alumno.getApellido());
        txtEmail.setText(alumno.getEmail());
        txtLegajo.setText(alumno.getLegajo());

        // Si no querés que el legajo se modifique:
        txtLegajo.setEnabled(false);
    }

    // --------- GUARDAR CAMBIOS ----------
    private void guardarCambios() {
        if (alumno == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay datos de alumno cargados.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nuevoNombre = txtNombre.getText().trim();
        String nuevoApellido = txtApellido.getText().trim();
        String nuevoEmail = txtEmail.getText().trim();

        if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty() || nuevoEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre, Apellido y Email no pueden estar vacíos.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Actualizamos el objeto
        alumno.setNombre(nuevoNombre);
        alumno.setApellido(nuevoApellido);
        alumno.setEmail(nuevoEmail);
        // Legajo no se toca (o lo lees de txtLegajo si permitís edición)

        boolean ok = alumnoDAO.actualizarAlumno(alumno);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Perfil actualizado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar el perfil.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // --------- LISTENERS ----------
    private void initListeners() {
        guardarButton.addActionListener(e -> guardarCambios());
        cerrarButton.addActionListener(e -> dispose());
    }

    // --------- MAIN TEST ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formMiPerfilAlumno("marcosezq@gmail.com").setVisible(true)
        );
    }
}
