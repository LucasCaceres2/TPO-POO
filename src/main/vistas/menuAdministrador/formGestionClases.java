package main.vistas.menuAdministrador;

import main.controlador.Plataforma;
import main.modelo.Clase;
import main.modelo.Curso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.util.List;

public class formGestionClases extends JFrame {

    private JPanel panelPrincipal;
    private JComboBox<Curso> comboCursos;
    private JTable tblClase;
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JSpinner spinnerFecha;
    private JButton volverButton;
    private JTextField txtTituloClase;
    private JTextArea txtContenidoClase;

    private Plataforma plataforma = new Plataforma();
    private Integer idClaseSeleccionada = null;

    public formGestionClases() {
        setTitle("Gestión de Clases");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        configurarTabla();
        configurarSpinner();
        cargarCursos();
        initListeners();
    }

    // ================= CONFIGURACIONES =================

    private void configurarTabla() {
        tblClase.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{ "ID", "Título", "Fecha", "Contenido" }
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });

        tblClase.setRowHeight(28);
        tblClase.getColumnModel().getColumn(0).setMinWidth(0);
        tblClase.getColumnModel().getColumn(0).setMaxWidth(0);
        tblClase.getColumnModel().getColumn(0).setWidth(0);

    }

    private void configurarSpinner() {
        spinnerFecha.setModel(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(editor);
    }

    // ================= CARGA DE DATOS =================

    private void cargarCursos() {
        comboCursos.removeAllItems();
        List<Curso> cursos = plataforma.listarTodosLosCursos();

        for (Curso c : cursos) {
            comboCursos.addItem(c);
        }
    }

    private void cargarClases(Curso curso) {
        DefaultTableModel model = (DefaultTableModel) tblClase.getModel();
        model.setRowCount(0);

        List<Clase> clases = plataforma.obtenerClasesPorCurso(curso);

        for (Clase c : clases) {
            model.addRow(new Object[]{
                    c.getIdClase(),
                    c.getTitulo(),
                    c.getFecha(),
                    c.getContenido()
            });
        }
    }

    // ================= LISTENERS =================

    private void initListeners() {

        comboCursos.addActionListener(e -> {
            Curso curso = (Curso) comboCursos.getSelectedItem();
            if (curso != null) {
                cargarClases(curso);
                limpiarCampos();
            }
        });

        tblClase.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblClase.getSelectedRow();
            if (fila >= 0) {
                idClaseSeleccionada = (Integer) tblClase.getValueAt(fila, 0);
                txtTituloClase.setText((String) tblClase.getValueAt(fila, 1));
                txtContenidoClase.setText((String) tblClase.getValueAt(fila, 3));
                spinnerFecha.setValue(tblClase.getValueAt(fila, 2));
            }
        });

        agregarButton.addActionListener(e -> agregarClase());
        modificarButton.addActionListener(e -> modificarClase());
        eliminarButton.addActionListener(e -> eliminarClase());

        volverButton.addActionListener(e -> dispose());
    }

    // ================= ACCIONES =================

    private void agregarClase() {
        Curso curso = (Curso) comboCursos.getSelectedItem();
        if (curso == null) return;

        Clase clase = new Clase(
                0,
                curso,
                (Date) spinnerFecha.getValue(),
                txtTituloClase.getText(),
                txtContenidoClase.getText()
        );

        if (plataforma.agregarNuevaClase(clase)) {
            JOptionPane.showMessageDialog(this, "Clase agregada");
            cargarClases(curso);
            limpiarCampos();
        }
    }

    private void modificarClase() {
        if (idClaseSeleccionada == null) return;

        Curso curso = (Curso) comboCursos.getSelectedItem();

        Clase clase = new Clase(
                idClaseSeleccionada,
                curso,
                (Date) spinnerFecha.getValue(),
                txtTituloClase.getText(),
                txtContenidoClase.getText()
        );

        if (plataforma.modificarClase(clase)) {
            JOptionPane.showMessageDialog(this, "Clase modificada");
            cargarClases(curso);
        }
    }

    private void eliminarClase() {
        if (idClaseSeleccionada == null) return;

        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar esta clase?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (r == JOptionPane.YES_OPTION) {
            if (plataforma.eliminarClase(idClaseSeleccionada)) {
                JOptionPane.showMessageDialog(this, "Clase eliminada");
                cargarClases((Curso) comboCursos.getSelectedItem());
                limpiarCampos();
            }
        }
    }

    private void limpiarCampos() {
        txtTituloClase.setText("");
        txtContenidoClase.setText("");
        spinnerFecha.setValue(new Date());
        idClaseSeleccionada = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formGestionClases().setVisible(true));
    }
}
