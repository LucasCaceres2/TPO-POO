package main.vistas.menuAdministrador;

import main.dao.DocenteDAO;
import main.modelo.Docente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formAdminDocentes extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;

    private JTable tablaDocentes;
    private JTextField txtMatricula;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtContrasena;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnRefrescar;
    private JButton btnCerrar;

    private final DocenteDAO docenteDAO = new DocenteDAO();

    // ===== Constructor real =====
    public formAdminDocentes() {
        setContentPane(pnlPrincipal);

        setTitle("Gestión de Docentes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        configurarTabla();
        cargarDocentes();
        initListeners();

        pack();
        setSize(1100, 700);
        setLocationRelativeTo(null);
    }

    // SOLO para diseñador
    public formAdminDocentes(boolean dummy) {
        this();
    }

    // ===== Config tabla =====
    private void configurarTabla() {
        String[] columnas = {"Matrícula", "Nombre", "Apellido", "Email"};

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaDocentes.setModel(model);
        tablaDocentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ===== Cargar =====
    private void cargarDocentes() {
        List<Docente> docentes = docenteDAO.listarDocentes();
        DefaultTableModel model = (DefaultTableModel) tablaDocentes.getModel();
        model.setRowCount(0);

        for (Docente d : docentes) {
            model.addRow(new Object[]{
                    d.getMatricula(),
                    d.getNombre(),
                    d.getApellido(),
                    d.getEmail()
            });
        }
    }

    // ===== Listeners =====
    private void initListeners() {

        // click en tabla → cargar datos
        tablaDocentes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaDocentes.getSelectedRow();
                if (row >= 0) {
                    DefaultTableModel m = (DefaultTableModel) tablaDocentes.getModel();
                    txtMatricula.setText(m.getValueAt(row, 0).toString());
                    txtNombre.setText(m.getValueAt(row, 1).toString());
                    txtApellido.setText(m.getValueAt(row, 2).toString());
                    txtEmail.setText(m.getValueAt(row, 3).toString());
                    txtContrasena.setText("");
                }
            }
        });

        btnNuevo.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarDocente());
        btnActualizar.addActionListener(e -> actualizarDocente());
        btnEliminar.addActionListener(e -> eliminarDocente());
        btnRefrescar.addActionListener(e -> cargarDocentes());
        btnCerrar.addActionListener(e -> dispose());
    }

    // ===== Helpers =====
    private void limpiarCampos() {
        tablaDocentes.clearSelection();
        txtMatricula.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtContrasena.setText("");
    }

    // Crear nuevo docente
    private void guardarDocente() {
        String matricula = txtMatricula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (matricula.isEmpty() || nombre.isEmpty() || apellido.isEmpty()
                || email.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios para crear un docente.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Docente docente = new Docente(nombre, apellido, email, contrasena, matricula);
        boolean ok = docenteDAO.agregarDocente(docente);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Docente creado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarDocentes();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo crear el docente (verificá matrícula/email únicos).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Actualizar datos básicos del docente
    private void actualizarDocente() {
        String matricula = txtMatricula.getText().trim();
        if (matricula.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná o ingresá la matrícula del docente a actualizar.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Docente existente = docenteDAO.obtenerDocentePorMatricula(matricula);
        if (existente == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró un docente con esa matrícula.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nuevoNombre = txtNombre.getText().trim();
        String nuevoApellido = txtApellido.getText().trim();
        String nuevoEmail = txtEmail.getText().trim();
        String nuevaContrasena = txtContrasena.getText().trim();

        boolean ok = true;

        if (!nuevoNombre.isEmpty() && !nuevoNombre.equals(existente.getNombre())) {
            ok &= docenteDAO.actualizarDocente(matricula, "nombre", nuevoNombre);
        }
        if (!nuevoApellido.isEmpty() && !nuevoApellido.equals(existente.getApellido())) {
            ok &= docenteDAO.actualizarDocente(matricula, "apellido", nuevoApellido);
        }
        if (!nuevoEmail.isEmpty() && !nuevoEmail.equals(existente.getEmail())) {
            ok &= docenteDAO.actualizarDocente(matricula, "email", nuevoEmail);
        }
        if (!nuevaContrasena.isEmpty()) {
            ok &= docenteDAO.actualizarDocente(matricula, "contrasena", nuevaContrasena);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Docente actualizado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarDocentes();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar el docente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Eliminar docente
    private void eliminarDocente() {
        String matricula = txtMatricula.getText().trim();
        if (matricula.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingresá o seleccioná la matrícula a eliminar.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int op = JOptionPane.showConfirmDialog(this,
                "¿Seguro que querés eliminar al docente con matrícula " + matricula + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (op != JOptionPane.YES_OPTION) return;

        boolean ok = docenteDAO.eliminarDocente(matricula);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Docente eliminado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarDocentes();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el docente (puede tener cursos asociados).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Main prueba rápida =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formAdminDocentes().setVisible(true));
    }
}
