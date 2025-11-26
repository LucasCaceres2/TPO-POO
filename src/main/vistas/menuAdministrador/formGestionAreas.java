package main.vistas.menuAdministrador;

import main.dao.AreaDAO;
import main.modelo.Area;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formGestionAreas extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTable tablaAreas;
    private JTextField txtNombre;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnRefrescar;
    private JButton btnActualizar;
    private JButton btnDesactivar;
    private JButton btnReactivar;
    private JButton btnCerrar;

    private final AreaDAO areaDAO = new AreaDAO();
    private Integer idAreaSeleccionada = null;

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

    // ================= TABLA =================
    private void configurarTabla() {
        String[] columnas = {"ID", "Nombre", "Activa"};

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaAreas.setModel(model);
    }

    private void cargarAreasEnTabla() {
        DefaultTableModel model = (DefaultTableModel) tablaAreas.getModel();
        model.setRowCount(0);

        List<Area> areas = areaDAO.listarTodasLasAreas(); // ✅ ahora sí existe

        for (Area a : areas) {
            model.addRow(new Object[]{
                    a.getIdArea(),
                    a.getNombre(),
                    a.isActivo() ? "Sí" : "No"
            });
        }
    }

    // ================= LISTENERS =================
    private void initListeners() {

        tablaAreas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaAreas.getSelectedRow();
                if (fila >= 0) cargarAreaDesdeFila(fila);
            }
        });

        btnNuevo.addActionListener(e -> limpiarFormulario());

        btnGuardar.addActionListener(e -> guardarArea());

        btnActualizar.addActionListener(e -> actualizarArea());

        btnDesactivar.addActionListener(e -> cambiarEstado(false));

        btnReactivar.addActionListener(e -> cambiarEstado(true));

        btnRefrescar.addActionListener(e -> { cargarAreasEnTabla(); limpiarFormulario(); });

        btnCerrar.addActionListener(e -> dispose());
    }

    // ================= FUNCIONES =================

    private void cargarAreaDesdeFila(int fila) {
        DefaultTableModel model = (DefaultTableModel) tablaAreas.getModel();

        idAreaSeleccionada = (Integer) model.getValueAt(fila, 0);
        String nombre = (String) model.getValueAt(fila, 1);

        txtNombre.setText(nombre);
    }

    private void limpiarFormulario() {
        idAreaSeleccionada = null;
        txtNombre.setText("");
        tablaAreas.clearSelection();
    }

    private void guardarArea() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese nombre de área.");
            return;
        }

        Area area = new Area(0, nombre);
        boolean ok = areaDAO.agregarArea(area);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Área creada correctamente.");
            cargarAreasEnTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo crear el área.");
        }
    }

    private void actualizarArea() {
        if (idAreaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un área.");
            return;
        }

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre inválido.");
            return;
        }

        boolean ok = areaDAO.actualizarArea(idAreaSeleccionada, nombre);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Área actualizada.");
            cargarAreasEnTabla();
        }
    }

    private void cambiarEstado(boolean activar) {
        if (idAreaSeleccionada == null) return;

        boolean ok = activar
                ? areaDAO.reactivarArea(idAreaSeleccionada)
                : areaDAO.desactivarArea(idAreaSeleccionada);

        if (ok) {
            cargarAreasEnTabla();
            limpiarFormulario();
        }
    }

    // ================= MAIN TEST =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formGestionAreas().setVisible(true));
    }
}