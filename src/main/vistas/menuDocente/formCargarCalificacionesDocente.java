package main.vistas.menuDocente;

import main.controlador.Plataforma;
import main.dao.CursoDAO;
import main.dao.DocenteDAO;
import main.dao.InscripcionDAO;
import main.modelo.Curso;
import main.modelo.Docente;
import main.modelo.Inscripcion;
import main.modelo.TipoEvaluacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class formCargarCalificacionesDocente extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;

    private JComboBox<Curso> comboCursos;
    private JComboBox<TipoEvaluacion> comboTipoEvaluacion;   // <-- combo del enum
    private JTable tablaNotas;
    private JButton guardarButton;
    private JButton cerrarButton;

    private final Plataforma plataforma     = new Plataforma();
    private final CursoDAO cursoDAO         = new CursoDAO();
    private final DocenteDAO docenteDAO     = new DocenteDAO();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();

    private final String emailDocente;
    private List<Inscripcion> inscripcionesActuales = new ArrayList<>();

    // ----- constructor principal -----
    public formCargarCalificacionesDocente(String emailDocente) {
        this.emailDocente = emailDocente;

        setContentPane(pnlPrincipal);
        setTitle("Cargar calificaciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        configurarTabla();
        configurarCombos();
        initListeners();
    }

    // constructor vacío solo para el diseñador
    public formCargarCalificacionesDocente() {
        this(null);
    }

    // ================= TABLA =================
    private void configurarTabla() {
        String[] columnas = {
                "Legajo",
                "Alumno",
                "Nota (0 - 10)"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo la columna de nota es editable
                return column == 2;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Double.class;
                return String.class;
            }
        };

        tablaNotas.setModel(model);
        tablaNotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ================= COMBOS =================
    private void configurarCombos() {
        // Combo de tipos de evaluación (enum)
        if (comboTipoEvaluacion != null) {
            comboTipoEvaluacion.removeAllItems();
            for (TipoEvaluacion t : TipoEvaluacion.values()) {
                comboTipoEvaluacion.addItem(t);
            }
            if (comboTipoEvaluacion.getItemCount() > 0) {
                comboTipoEvaluacion.setSelectedIndex(0);
            }
        }

        // Combo de cursos del docente
        comboCursos.removeAllItems();

        if (emailDocente == null || emailDocente.isBlank()) {
            return;
        }

        Docente docente = docenteDAO.obtenerDocentePorEmail(emailDocente);
        if (docente == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el perfil del docente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        var cursos = cursoDAO.listarCursosPorDocente(docente.getIdUsuario());
        for (Curso c : cursos) {
            comboCursos.addItem(c);
        }

        if (comboCursos.getItemCount() > 0) {
            comboCursos.setSelectedIndex(0);
            cargarAlumnosDelCurso((Curso) comboCursos.getSelectedItem());
        }
    }

    // ================= CARGA DE ALUMNOS =================
    private void cargarAlumnosDelCurso(Curso curso) {
        DefaultTableModel model = (DefaultTableModel) tablaNotas.getModel();
        model.setRowCount(0);
        inscripcionesActuales.clear();

        if (curso == null) return;

        inscripcionesActuales = inscripcionDAO.listarInscripcionesPorCurso(curso.getIdCurso());

        for (Inscripcion ins : inscripcionesActuales) {
            if (ins.getAlumno() == null) continue;

            String legajo = ins.getAlumno().getLegajo();
            String nombreCompleto = ins.getAlumno().getNombre() + " " + ins.getAlumno().getApellido();

            model.addRow(new Object[]{
                    legajo,
                    nombreCompleto,
                    null  // nota vacía
            });
        }
    }

    // ================= LISTENERS =================
    private void initListeners() {

        comboCursos.addActionListener(e -> {
            Curso seleccionado = (Curso) comboCursos.getSelectedItem();
            cargarAlumnosDelCurso(seleccionado);
        });

        guardarButton.addActionListener(e -> guardarCalificaciones());

        cerrarButton.addActionListener(e -> dispose());
    }

    // ================= GUARDAR =================
    private void guardarCalificaciones() {
        Curso curso = (Curso) comboCursos.getSelectedItem();
        if (curso == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un curso.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (comboTipoEvaluacion == null || comboTipoEvaluacion.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un tipo de evaluación.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        TipoEvaluacion tipo = (TipoEvaluacion) comboTipoEvaluacion.getSelectedItem();

        DefaultTableModel model = (DefaultTableModel) tablaNotas.getModel();
        int filas = model.getRowCount();

        if (filas == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay alumnos para calificar.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int guardadas = 0;

        for (int i = 0; i < filas; i++) {
            String legajo = String.valueOf(model.getValueAt(i, 0));
            Object notaObj = model.getValueAt(i, 2);

            if (notaObj == null) continue; // fila sin nota

            double nota;
            try {
                nota = Double.parseDouble(notaObj.toString());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "La nota de " + legajo + " no es válida.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (nota < 0 || nota > 10) {
                JOptionPane.showMessageDialog(this,
                        "La nota debe ser entre 0 y 10 (legajo " + legajo + ").",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            boolean ok = plataforma.registrarCalificacion(
                    legajo,
                    curso.getIdCurso(),
                    tipo,
                    nota
            );

            if (ok) guardadas++;
        }

        JOptionPane.showMessageDialog(this,
                "Se guardaron " + guardadas + " calificaciones.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Main de prueba opcional
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formCargarCalificacionesDocente("docente@example.com").setVisible(true)
        );
    }
}
