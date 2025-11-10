package main.vistas.menuAdministrador;

import main.dao.AreaDAO;
import main.dao.CursoDAO;
import main.dao.DocenteDAO;
import main.modelo.Area;
import main.modelo.Curso;
import main.modelo.Docente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class formGestionCursos extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;

    private JTable tablaCursos;
    private JTextField txtTitulo;
    private JTextField txtCupoMax;
    private JTextField txtCantidadClases;
    private JComboBox<Docente> comboDocente;
    private JComboBox<Area> comboArea;
    private JTextArea txtContenido;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnRefrescar;
    private JButton btnCerrar;

    private final CursoDAO cursoDAO = new CursoDAO();
    private final DocenteDAO docenteDAO = new DocenteDAO();
    private final AreaDAO areaDAO = new AreaDAO();

    private Integer idCursoSeleccionado = null;

    public formGestionCursos() {
        setContentPane(pnlPrincipal);
        setTitle("Gestión de Cursos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // tamaño inicial cómodo
        setSize(1100, 700);
        setLocationRelativeTo(null);

        configurarTabla();
        cargarCombos();
        cargarCursosEnTabla();
        initListeners();
    }

    // ==== Configuración de tabla ====
    private void configurarTabla() {
        String[] columnas = {
                "ID",
                "Título",
                "Docente",
                "Área",
                "Cupo Max",
                "Clases",
                "Contenido"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCursos.setModel(model);
        tablaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ==== Cargar combos Docente / Área ====
    private void cargarCombos() {
        comboDocente.removeAllItems();
        comboArea.removeAllItems();

        List<Docente> docentes = docenteDAO.listarDocentes();
        for (Docente d : docentes) {
            comboDocente.addItem(d); // se ve lindo si Docente.toString() devuelve nombre
        }

        var areas = areaDAO.listarAreas();
        for (Area a : areas) {
            comboArea.addItem(a); // idem con Area.toString()
        }
    }

    // ==== Cargar cursos en tabla ====
    private void cargarCursosEnTabla() {
        DefaultTableModel model = (DefaultTableModel) tablaCursos.getModel();
        model.setRowCount(0);

        List<Curso> cursos = cursoDAO.listarCursos();
        for (Curso c : cursos) {
            model.addRow(new Object[]{
                    c.getIdCurso(),
                    c.getTitulo(),
                    c.getDocente() != null ? c.getDocente().getNombre() + " " + c.getDocente().getApellido() : "",
                    c.getArea() != null ? c.getArea().getNombre() : "",
                    c.getCupoMax(),
                    c.getCantidadClases(),
                    c.getContenido()
            });
        }
    }

    // ==== Listeners ====
    private void initListeners() {

        // tabla -> carga datos al formulario
        tablaCursos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaCursos.getSelectedRow();
                if (fila >= 0) {
                    cargarCursoDesdeFila(fila);
                }
            }
        });

        // Nuevo: limpia formulario
        btnNuevo.addActionListener(e -> limpiarFormulario());

        // Guardar: alta
        btnGuardar.addActionListener(e -> guardarCurso());

        // Actualizar: modifica curso seleccionado
        btnActualizar.addActionListener(e -> actualizarCurso());

        // Eliminar: baja
        btnEliminar.addActionListener(e -> eliminarCurso());

        // Refrescar: recarga tabla y combos
        btnRefrescar.addActionListener(e -> {
            cargarCombos();
            cargarCursosEnTabla();
            limpiarFormulario();
        });

        btnCerrar.addActionListener(e -> dispose());
    }

    // ==== Helpers de formulario ====

    private void cargarCursoDesdeFila(int fila) {
        DefaultTableModel model = (DefaultTableModel) tablaCursos.getModel();

        idCursoSeleccionado = (Integer) model.getValueAt(fila, 0);
        String titulo = (String) model.getValueAt(fila, 1);
        String docenteNombre = (String) model.getValueAt(fila, 2);
        String areaNombre = (String) model.getValueAt(fila, 3);
        Integer cupoMax = (Integer) model.getValueAt(fila, 4);
        Integer clases = (Integer) model.getValueAt(fila, 5);
        String contenido = (String) model.getValueAt(fila, 6);

        txtTitulo.setText(titulo);
        txtCupoMax.setText(String.valueOf(cupoMax));
        txtCantidadClases.setText(String.valueOf(clases));
        txtContenido.setText(contenido != null ? contenido : "");

        // seleccionar docente en combo
        if (docenteNombre != null) {
            for (int i = 0; i < comboDocente.getItemCount(); i++) {
                Docente d = comboDocente.getItemAt(i);
                String nom = d.getNombre() + " " + d.getApellido();
                if (nom.equals(docenteNombre)) {
                    comboDocente.setSelectedIndex(i);
                    break;
                }
            }
        }

        // seleccionar área en combo
        if (areaNombre != null) {
            for (int i = 0; i < comboArea.getItemCount(); i++) {
                Area a = comboArea.getItemAt(i);
                if (a.getNombre().equals(areaNombre)) {
                    comboArea.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void limpiarFormulario() {
        idCursoSeleccionado = null;
        txtTitulo.setText("");
        txtCupoMax.setText("");
        txtCantidadClases.setText("");
        txtContenido.setText("");
        if (comboDocente.getItemCount() > 0) comboDocente.setSelectedIndex(0);
        if (comboArea.getItemCount() > 0) comboArea.setSelectedIndex(0);
        tablaCursos.clearSelection();
    }

    private void guardarCurso() {
        String titulo = txtTitulo.getText().trim();
        String cupoStr = txtCupoMax.getText().trim();
        String clasesStr = txtCantidadClases.getText().trim();
        Docente docente = (Docente) comboDocente.getSelectedItem();
        Area area = (Area) comboArea.getSelectedItem();
        String contenido = txtContenido.getText().trim();

        if (titulo.isEmpty() || cupoStr.isEmpty() || clasesStr.isEmpty() || docente == null || area == null) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cupo, clases;
        try {
            cupo = Integer.parseInt(cupoStr);
            clases = Integer.parseInt(clasesStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cupo y clases deben ser números enteros.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Curso curso = new Curso(titulo, cupo, docente, area, contenido, clases);
        boolean ok = cursoDAO.agregarCurso(curso);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Curso creado correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
            cargarCursosEnTabla();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo crear el curso.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCurso() {
        if (idCursoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un curso de la tabla.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String titulo = txtTitulo.getText().trim();
        String cupoStr = txtCupoMax.getText().trim();
        String clasesStr = txtCantidadClases.getText().trim();
        Docente docente = (Docente) comboDocente.getSelectedItem();
        Area area = (Area) comboArea.getSelectedItem();
        String contenido = txtContenido.getText().trim();

        if (titulo.isEmpty() || cupoStr.isEmpty() || clasesStr.isEmpty() || docente == null || area == null) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cupo, clases;
        try {
            cupo = Integer.parseInt(cupoStr);
            clases = Integer.parseInt(clasesStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cupo y clases deben ser números enteros.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Podés hacer un método específico en CursoDAO para actualizar todos los campos.
        // Como tu DAO actual solo tiene actualizarCampo simple, lo ideal es agregar uno nuevo.
        // Por ahora, ejemplo simple:
        boolean okTitulo = cursoDAO.actualizarCurso(idCursoSeleccionado, "titulo", titulo);
        boolean okContenido = cursoDAO.actualizarCurso(idCursoSeleccionado, "contenido", contenido);
        // Si agregás columnas doc/area/cupo/clases al UPDATE, los sumás acá.

        if (okTitulo || okContenido) {
            JOptionPane.showMessageDialog(this, "Curso actualizado.", "OK", JOptionPane.INFORMATION_MESSAGE);
            cargarCursosEnTabla();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el curso.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCurso() {
        if (idCursoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un curso de la tabla.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int r = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar el curso ID " + idCursoSeleccionado + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (r == JOptionPane.YES_OPTION) {
            boolean ok = cursoDAO.eliminarCurso(idCursoSeleccionado);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Curso eliminado.", "OK", JOptionPane.INFORMATION_MESSAGE);
                cargarCursosEnTabla();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar (puede tener inscripciones asociadas).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // main de prueba
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formGestionCursos().setVisible(true));
    }
}
