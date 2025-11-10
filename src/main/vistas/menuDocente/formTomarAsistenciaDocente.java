package main.vistas.menuDocente;

import main.dao.*;
import main.modelo.Curso;
import main.modelo.Docente;
import main.modelo.Inscripcion;
import main.modelo.Alumno;
import main.servicios.Plataforma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.util.List;

public class formTomarAsistenciaDocente extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;

    private JComboBox<Curso> comboCursos;
    private JTable tablaAlumnos;
    private JTextField txtFecha;
    private JButton guardarButton;
    private JButton cerrarButton;

    private final String emailDocente;
    private final DocenteDAO docenteDAO = new DocenteDAO();
    private final CursoDAO cursoDAO = new CursoDAO();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final Plataforma plataforma = new Plataforma();

    public formTomarAsistenciaDocente(String emailDocente) {
        this.emailDocente = emailDocente;

        setContentPane(pnlPrincipal);
        setTitle("Tomar asistencia");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        configurarTabla();
        cargarCursosDelDocente();
        initListeners();

        // fecha por defecto: hoy (texto simple)
        txtFecha.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        pack();
        setSize(900, 500);
    }

    // constructor vacío SOLO para el diseñador
    public formTomarAsistenciaDocente() {
        this(null);
    }

    // ================= CONFIG TABLA =================
    private void configurarTabla() {
        String[] columnas = {"Legajo", "Alumno", "Presente"};

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo la columna "Presente" editable (checkbox)
                return column == 2;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Boolean.class;
                return String.class;
            }
        };

        tablaAlumnos.setModel(model);
        tablaAlumnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ================= CARGAR CURSOS DEL DOCENTE =================
    private void cargarCursosDelDocente() {
        comboCursos.removeAllItems();

        if (emailDocente == null || emailDocente.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el email del docente logueado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Docente docente = docenteDAO.obtenerDocentePorEmail(emailDocente);
        if (docente == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el docente en la base de datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idDocente = docente.getIdUsuario();
        List<Curso> cursos = cursoDAO.listarCursosPorDocente(idDocente);

        for (Curso c : cursos) {
            comboCursos.addItem(c);
        }

        // Cuando haya al menos un curso, cargamos alumnos
        if (comboCursos.getItemCount() > 0) {
            comboCursos.setSelectedIndex(0);
            cargarAlumnosDelCursoSeleccionado();
        }
    }

    // ================= CARGAR ALUMNOS POR CURSO =================
    private void cargarAlumnosDelCursoSeleccionado() {
        DefaultTableModel model = (DefaultTableModel) tablaAlumnos.getModel();
        model.setRowCount(0);

        Curso curso = (Curso) comboCursos.getSelectedItem();
        if (curso == null) return;

        List<Inscripcion> inscripciones = inscripcionDAO.listarInscripcionesPorCurso(curso.getIdCurso());

        for (Inscripcion insc : inscripciones) {
            Alumno a = insc.getAlumno();
            if (a != null) {
                model.addRow(new Object[]{
                        a.getLegajo(),
                        a.getNombre() + " " + a.getApellido(),
                        Boolean.TRUE   // por defecto presente, podés poner FALSE si querés
                });
            }
        }
    }

    // ================= GUARDAR ASISTENCIA =================
    private void guardarAsistencia() {
        Curso curso = (Curso) comboCursos.getSelectedItem();
        if (curso == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná un curso.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fechaTexto = txtFecha.getText().trim();
        if (fechaTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingresá una fecha (yyyy-MM-dd).",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date fecha;
        try {
            fecha = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(fechaTexto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Usá yyyy-MM-dd.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tablaAlumnos.getModel();
        int filas = model.getRowCount();
        if (filas == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay alumnos para registrar asistencia.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCurso = curso.getIdCurso();
        boolean algunoOk = false;

        for (int i = 0; i < filas; i++) {
            String legajo = (String) model.getValueAt(i, 0);
            Boolean presente = (Boolean) model.getValueAt(i, 2);
            if (presente == null) presente = false;

            boolean ok = plataforma.tomarAsistencia(legajo, idCurso, fecha, presente);
            if (ok) algunoOk = true;
        }

        if (algunoOk) {
            JOptionPane.showMessageDialog(this,
                    "Asistencia guardada correctamente.",
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar la asistencia.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= LISTENERS =================
    private void initListeners() {
        comboCursos.addActionListener(e -> cargarAlumnosDelCursoSeleccionado());
        guardarButton.addActionListener(e -> guardarAsistencia());
        cerrarButton.addActionListener(e -> dispose());
    }

    // ================= MAIN TEST OPCIONAL =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formTomarAsistenciaDocente("laura.doc@correo.com").setVisible(true)
        );
    }
}
