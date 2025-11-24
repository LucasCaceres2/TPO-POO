package main.vistas.menuAlumno;

import main.dao.AsistenciaDAO;
import main.dao.CursoDAO;
import main.modelo.Curso;

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
    private JLabel lblPorcentajeAsistencia;

    private final String emailAlumno;
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final CursoDAO cursoDAO = new CursoDAO();

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
        // -------- Tabla de cursos --------
        String[] columnasCursos = { "ID Curso", "Curso", "Docente" };

        DefaultTableModel modelCursos = new DefaultTableModel(columnasCursos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCursos.setModel(modelCursos);
        tablaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 👇 Ocultar la columna "ID Curso" (columna 0)
        if (tablaCursos.getColumnModel().getColumnCount() > 0) {
            tablaCursos.getColumnModel().getColumn(0).setMinWidth(0);
            tablaCursos.getColumnModel().getColumn(0).setMaxWidth(0);
            tablaCursos.getColumnModel().getColumn(0).setPreferredWidth(0);
        }

        // -------- Tabla de asistencia --------
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

        // 👇 Ocultar la columna "ID Asistencia" (columna 0)
        if (tablaAsistencia.getColumnModel().getColumnCount() > 0) {
            tablaAsistencia.getColumnModel().getColumn(0).setMinWidth(0);
            tablaAsistencia.getColumnModel().getColumn(0).setMaxWidth(0);
            tablaAsistencia.getColumnModel().getColumn(0).setPreferredWidth(0);
        }
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

        // 1) Cargar la tabla de asistencias como antes
        DefaultTableModel modelAsist = (DefaultTableModel) tablaAsistencia.getModel();
        modelAsist.setRowCount(0);

        List<Object[]> filas = asistenciaDAO.listarAsistenciasPorAlumnoYCurso(emailAlumno, idCurso);

        int totalRegistros = 0;
        int faltas = 0;

        for (Object[] fila : filas) {
            // fila = { idAsistencia, claseTitulo, fechaClase, presente }
            modelAsist.addRow(fila);

            totalRegistros++;

            Boolean presente = (Boolean) fila[3];
            if (presente != null && !presente) {
                faltas++;
            }
        }

        // 2) Obtener cantidad total de clases del curso
        int totalClases;

        // Intentamos usar el dato del curso (cantidadClases)
        Curso curso = cursoDAO.obtenerCursoPorId(idCurso);
        if (curso != null && curso.getCantidadClases() > 0) {
            totalClases = curso.getCantidadClases();
        } else {
            // Si por algún motivo no lo tenemos, usamos los registros de asistencia como fallback
            totalClases = totalRegistros;
        }

        // 3) Calcular y mostrar el porcentaje de asistencia
        if (totalClases <= 0) {
            lblPorcentajeAsistencia.setText("Porcentaje de asistencia: sin clases registradas.");
            return;
        }

        // Empieza en 100% y baja con las faltas
        double porcentaje = 100.0 - (faltas * 100.0 / totalClases);
        if (porcentaje < 0) porcentaje = 0; // por las dudas

        String texto = String.format(
                "Porcentaje de asistencia: %.1f%% (faltas: %d de %d clases)",
                porcentaje, faltas, totalClases
        );

        lblPorcentajeAsistencia.setText(texto);
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
