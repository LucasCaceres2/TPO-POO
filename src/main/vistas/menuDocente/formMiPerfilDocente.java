package main.vistas.menuDocente;

import main.dao.DocenteDAO;
import main.modelo.Docente;

import javax.swing.*;

public class formMiPerfilDocente extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtMatricula;
    private JButton guardarButton;
    private JButton cerrarButton;

    private final String emailDocente;     // email con el que se logueó
    private final DocenteDAO docenteDAO = new DocenteDAO();
    private Docente docente;               // docente cargado desde BD

    // --------- CONSTRUCTOR PRINCIPAL ----------
    public formMiPerfilDocente(String emailDocente) {
        this.emailDocente = emailDocente;

        setContentPane(pnlPrincipal);
        setTitle("Mi Perfil - Docente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cargarDatosDocente();
        initListeners();

        pack();
        setSize(400, 300);
    }

    // SOLO para el diseñador (no usar en producción real)
    public formMiPerfilDocente() {
        this(null);
    }

    // --------- CARGAR DATOS DESDE BD ----------
    private void cargarDatosDocente() {
        if (emailDocente == null || emailDocente.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el email del docente logueado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        docente = docenteDAO.obtenerDocentePorEmail(emailDocente);

        if (docente == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el perfil del docente en la base de datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        txtNombre.setText(docente.getNombre());
        txtApellido.setText(docente.getApellido());
        txtEmail.setText(docente.getEmail());
        txtMatricula.setText(docente.getMatricula());

        // La matrícula no se modifica
        txtMatricula.setEnabled(false);
    }

    // --------- GUARDAR CAMBIOS ----------
    private void guardarCambios() {
        if (docente == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay datos del docente cargados.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nuevoNombre = txtNombre.getText().trim();
        String nuevoApellido = txtApellido.getText().trim();
        String nuevoEmail = txtEmail.getText().trim();
        String matricula = docente.getMatricula();

        if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty() || nuevoEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre, Apellido y Email no pueden estar vacíos.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = true;

        if (!nuevoNombre.equals(docente.getNombre())) {
            ok &= docenteDAO.actualizarDocente(matricula, "nombre", nuevoNombre);
            if (ok) docente.setNombre(nuevoNombre);
        }

        if (!nuevoApellido.equals(docente.getApellido())) {
            ok &= docenteDAO.actualizarDocente(matricula, "apellido", nuevoApellido);
            if (ok) docente.setApellido(nuevoApellido);
        }

        if (!nuevoEmail.equals(docente.getEmail())) {
            ok &= docenteDAO.actualizarDocente(matricula, "email", nuevoEmail);
            if (ok) docente.setEmail(nuevoEmail);
        }

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

    // --------- MAIN TEST OPCIONAL ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formMiPerfilDocente("laura.doc@correo.com").setVisible(true)
        );
    }
}
