package main.vistas.menuAdministrador;

import main.dao.AlumnoDAO;
import main.modelo.Alumno;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formAdminAlumnos extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;

    private JTable tablaAlumnos;
    private JTextField txtLegajo;
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

    private final AlumnoDAO alumnoDAO = new AlumnoDAO();

    // ===== Constructor real =====
    public formAdminAlumnos() {
        setContentPane(pnlPrincipal);
        setTitle("Gestión de Alumnos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        configurarTabla();
        cargarAlumnos();
        initListeners();

        setSize(1100, 700);
        setLocationRelativeTo(null);
    }

    // SOLO para diseñador
    public formAdminAlumnos(boolean dummy) {
        this();
    }

    // ===== Configurar tabla =====
    private void configurarTabla() {
        String[] columnas = {"Legajo", "Nombre", "Apellido", "Email"};

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaAlumnos.setModel(model);
        tablaAlumnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ===== Cargar alumnos =====
    private void cargarAlumnosEnModelo(DefaultTableModel model, List<Alumno> alumnos) {
        model.setRowCount(0);
        for (Alumno a : alumnos) {
            model.addRow(new Object[]{
                    a.getLegajo(),
                    a.getNombre(),
                    a.getApellido(),
                    a.getEmail()
            });
        }
    }

    private void cargarAlumnos() {
        List<Alumno> alumnos = alumnoDAO.listarAlumnos();
        DefaultTableModel model = (DefaultTableModel) tablaAlumnos.getModel();
        cargarAlumnosEnModelo(model, alumnos);
    }

    // ===== Listeners =====
    private void initListeners() {

        // seleccionar fila → cargar datos en los textfields
        tablaAlumnos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaAlumnos.getSelectedRow();
                if (row >= 0) {
                    DefaultTableModel m = (DefaultTableModel) tablaAlumnos.getModel();
                    txtLegajo.setText(m.getValueAt(row, 0).toString());
                    txtNombre.setText(m.getValueAt(row, 1).toString());
                    txtApellido.setText(m.getValueAt(row, 2).toString());
                    txtEmail.setText(m.getValueAt(row, 3).toString());
                    txtContrasena.setText(""); // nunca mostramos la real
                }
            }
        });

        btnNuevo.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarAlumno());
        btnActualizar.addActionListener(e -> actualizarAlumno());
        btnEliminar.addActionListener(e -> eliminarAlumno());
        btnRefrescar.addActionListener(e -> cargarAlumnos());
        btnCerrar.addActionListener(e -> dispose());
    }

    // ===== Acciones =====
    private void limpiarCampos() {
        tablaAlumnos.clearSelection();
        txtLegajo.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtContrasena.setText("");
    }

    // Crear alumno nuevo
    private void guardarAlumno() {
        String legajo = txtLegajo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        if (legajo.isEmpty() || nombre.isEmpty() || apellido.isEmpty()
                || email.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios para crear un alumno.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Alumno alumno = new Alumno(nombre, apellido, email, contrasena, legajo);
        boolean ok = alumnoDAO.agregarAlumno(alumno);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Alumno creado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarAlumnos();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo crear el alumno (verificar legajo/email únicos).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Actualizar datos de un alumno existente
    private void actualizarAlumno() {
        String legajo = txtLegajo.getText().trim();
        if (legajo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná un alumno (o ingresá legajo) para actualizar.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Alumno alumno = alumnoDAO.obtenerAlumnoPorLegajo(legajo);
        if (alumno == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró un alumno con ese legajo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nuevoNombre = txtNombre.getText().trim();
        String nuevoApellido = txtApellido.getText().trim();
        String nuevoEmail = txtEmail.getText().trim();
        String nuevaContrasena = txtContrasena.getText().trim();

        if (!nuevoNombre.isEmpty()) alumno.setNombre(nuevoNombre);
        if (!nuevoApellido.isEmpty()) alumno.setApellido(nuevoApellido);
        if (!nuevoEmail.isEmpty()) alumno.setEmail(nuevoEmail);
        if (!nuevaContrasena.isEmpty()) alumno.setContrasena(nuevaContrasena);

        // Ajustá este nombre si tu DAO usa otro (por ejemplo actualizarAlumno(alumno))
        boolean ok = alumnoDAO.actualizarAlumno(alumno);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Alumno actualizado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarAlumnos();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar el alumno.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Eliminar alumno
    private void eliminarAlumno() {
        String legajo = txtLegajo.getText().trim();
        if (legajo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingresá o seleccioná un legajo para eliminar.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int op = JOptionPane.showConfirmDialog(this,
                "¿Seguro que querés eliminar al alumno con legajo " + legajo + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (op != JOptionPane.YES_OPTION) return;

        // Ajustá si tu DAO tiene otra firma (por ejemplo eliminarAlumnoPorLegajo)
        boolean ok = alumnoDAO.eliminarAlumno(legajo);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Alumno eliminado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarAlumnos();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el alumno (ver relaciones en BD).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Main de prueba rápida =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formAdminAlumnos().setVisible(true));
    }
}
