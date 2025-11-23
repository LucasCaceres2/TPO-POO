package main.vistas.menuAlumno;

import main.dao.AsistenciaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formMiAsistenciaAlumno extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTable tablaCursos;
    private JTable tablaAsistencia;
    private JButton verAsistenciaButton;
    private JButton cerrarButton;

    private final String emailAlumno;
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();

    // ======= CONSTRUCTORES =======

    public formMiAsistenciaAlumno(String emailAlumno) {
        this.emailAlumno = emailAlumno;

        setContentPane(pnlPrincipal);
        setTitle("Mi asistencia");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        configurarTablas();
        cargarCursosCursando();
        initListeners();

        pack();
        setSize(900, 500);
        setLocationRelativeTo(null);
    }

    // constructor vacío para el diseñador
    public formMiAsistenciaAlumno() {
        this(null);
    }

    // ======= CONFIG TABLAS =======

    private void configurarTablas() {
        // Tabla de cursos
        String[] columnasCursos = { "ID Curso", "Curso", "Docente" };

        DefaultTableModel modelCursos = new DefaultTableModel(columnasCursos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCursos.setModel(modelCursos);
        tablaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Tabla de asistencia
        String[] columnasAsistencia = { "ID Asistencia", "Clase", "Fecha", "Presente" };

        DefaultTableModel modelAsistencia = new DefaultTableModel(columnasAsistencia, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) { // columna "Presente"
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        tablaAsistencia.setModel(modelAsistencia);
        tablaAsistencia.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ======= CARGA DE DATOS =======

    private void cargarCursosCursando() {
        DefaultTableModel model = (DefaultTableModel) tablaCursos.getModel();
        model.setRowCount(0);

        if (emailAlumno == null || emailAlumno.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el alumno logueado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Object[]> filas = asistenciaDAO.listarCursosCursandoPorAlumno(emailAlumno);
        for (Object[] fila : filas) {
            model.addRow(fila);
        }
    }

    private void cargarAsistenciaDelCursoSeleccionado() {
        int filaSel = tablaCursos.getSelectedRow();
        if (filaSel == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná un curso de la lista.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCurso = (int) tablaCursos.getValueAt(filaSel, 0);

        DefaultTableModel modelAsist = (DefaultTableModel) tablaAsistencia.getModel();
        modelAsist.setRowCount(0);

        List<Object[]> filas = asistenciaDAO.listarAsistenciasPorAlumnoYCurso(emailAlumno, idCurso);
        for (Object[] fila : filas) {
            modelAsist.addRow(fila);
        }
    }

    // ======= LISTENERS =======

    private void initListeners() {
        cerrarButton.addActionListener(e -> dispose());

        verAsistenciaButton.addActionListener(e -> cargarAsistenciaDelCursoSeleccionado());

        // Opcional: doble click en la tabla de cursos
        tablaCursos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    cargarAsistenciaDelCursoSeleccionado();
                }
            }
        });
    }

    // ======= MAIN PRUEBA =======

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formMiAsistenciaAlumno("ana.gomez@example.com").setVisible(true)
        );
    }
}
