package main.vistas.menuAdministrador;

import main.dao.AreaDAO;
import main.modelo.Administrador;
import main.modelo.Area;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formGestionAreas extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;

    private JTable tablaAreas;
    private JTextField txtNombreArea;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnRefrescar;
    private JButton btnCerrar;

    private final AreaDAO areaDAO = new AreaDAO();

    // Usamos también la clase Administrador, como en Gestión de Cursos
    private final Administrador administrador = new Administrador(
            0,
            "Admin",
            "Sistema",
            "admin@sistema.com",
            "admin"
    );

    private Integer idAreaSeleccionada = null;

    // ===== CONSTRUCTOR =====
    public formGestionAreas() {
        setContentPane(pnlPrincipal);
        setTitle("Gestión de Áreas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setSize(700, 500);
        setLocationRelativeTo(null);

        configurarTabla();
        cargarAreasEnTabla();
        initListeners();
    }

    // ===== CONFIG TABLA =====
    private void configurarTabla() {
        String[] columnas = { "ID", "Nombre área" };

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaAreas.setModel(model);
        tablaAreas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ===== CARGAR ÁREAS =====
    private void cargarAreasEnTabla() {
        DefaultTableModel model = (DefaultTableModel) tablaAreas.getModel();
        model.setRowCount(0);

        List<Area> areas = areaDAO.listarAreas();
        for (Area a : areas) {
            model.addRow(new Object[]{
                    a.getIdArea(),
                    a.getNombre()
            });
        }
    }

    // ===== LISTENERS =====
    private void initListeners() {

        // Cuando selecciono una fila, cargo el nombre en el textfield
        tablaAreas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaAreas.getSelectedRow();
                if (fila >= 0) {
                    cargarAreaDesdeFila(fila);
                }
            }
        });

        btnNuevo.addActionListener(e -> limpiarFormulario());

        // Alta usando Administrador.crearArea(...)
        btnGuardar.addActionListener(e -> guardarArea());

        // Actualizar usando AreaDAO
        btnActualizar.addActionListener(e -> actualizarArea());

        // Eliminar usando AreaDAO
        btnEliminar.addActionListener(e -> eliminarArea());

        // Refrescar tabla
        btnRefrescar.addActionListener(e -> {
            cargarAreasEnTabla();
            limpiarFormulario();
        });

        btnCerrar.addActionListener(e -> dispose());
    }

    // ===== HELPERS =====

    private void cargarAreaDesdeFila(int fila) {
        DefaultTableModel model = (DefaultTableModel) tablaAreas.getModel();

        idAreaSeleccionada = (Integer) model.getValueAt(fila, 0);
        String nombre = (String) model.getValueAt(fila, 1);

        txtNombreArea.setText(nombre != null ? nombre : "");
    }

    private void limpiarFormulario() {
        idAreaSeleccionada = null;
        txtNombreArea.setText("");
        tablaAreas.clearSelection();
    }

    // --- GUARDAR (ALTA) ---
    private void guardarArea() {
        String nombre = txtNombreArea.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un nombre de área.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = administrador.crearArea(nombre);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Área creada correctamente.",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarAreasEnTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo crear el área (ver consola, puede estar duplicada).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- ACTUALIZAR ---
    private void actualizarArea() {
        if (idAreaSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un área de la tabla.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nombre = txtNombreArea.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre del área no puede estar vacío.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = areaDAO.actualizarArea(idAreaSeleccionada, nombre);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Área actualizada correctamente.",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarAreasEnTabla();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo actualizar el área.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- ELIMINAR ---
    private void eliminarArea() {
        if (idAreaSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un área de la tabla.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int r = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar el área ID " + idAreaSeleccionada + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (r != JOptionPane.YES_OPTION) return;

        boolean ok = areaDAO.eliminarArea(idAreaSeleccionada);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Área eliminada correctamente.",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarAreasEnTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el área (puede tener cursos asociados).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // main de prueba
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formGestionAreas().setVisible(true));
    }
}
