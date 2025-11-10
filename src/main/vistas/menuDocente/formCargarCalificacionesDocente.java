package main.vistas.menuDocente;

import main.dao.CalificacionDAO;
import main.dao.CursoDAO;
import main.dao.DocenteDAO;
import main.dao.InscripcionDAO;
import main.modelo.Calificacion;
import main.modelo.Curso;
import main.modelo.Docente;
import main.modelo.Inscripcion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class formCargarCalificacionesDocente extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;

    private JComboBox<Curso> comboCursos;
    private JTable tablaNotas;
    private JButton guardarButton;
    private JButton cerrarButton;

    private final String emailDocente;

    private final DocenteDAO docenteDAO = new DocenteDAO();
    private final CursoDAO cursoDAO = new CursoDAO();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final CalificacionDAO calificacionDAO = new CalificacionDAO();

    // Para mapear filas de la tabla con inscripciones reales
    private List<Inscripcion> inscripcionesActuales = new ArrayList<>();

    public formCargarCalificacionesDocente(String emailDocente) {
        this.emailDocente = emailDocente;

        setContentPane(pnlPrincipal);
        setTitle("Cargar calificaciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        configurarTabla();
        cargarCursosDelDocente();
        initListeners();

        pack();
        setSize(900, 450);
    }

    // constructor vacío SOLO para el diseñador
    public formCargarCalificacionesDocente() {
        this(null);
    }

    private void configurarTabla() {
        String[] columnas = {
                "Legajo",
                "Alumno",
                "Tipo calificación",
                "Nota (0-10)"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Permitimos edición solo en Tipo y Nota
                return column == 2 || column == 3;
            }
        };

        tablaNotas.setModel(model);
        tablaNotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void cargarCursosDelDocente() {
        comboCursos.removeAllItems();

        if (emailDocente == null || emailDocente.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el email del docente logueado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1) Buscar docente por email
        Docente docente = docenteDAO.obtenerDocentePorEmail(emailDocente);
        if (docente == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el docente en la base de datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idDocente = docente.getIdUsuario();

        // 2) Listar cursos de ese docente
        var cursos = cursoDAO.listarCursosPorDocente(idDocente);
        for (Curso c : cursos) {
            comboCursos.addItem(c); // muestra título gracias al toString() del Curso
        }

        if (comboCursos.getItemCount() > 0) {
            comboCursos.setSelectedIndex(0);
            cargarAlumnosDelCursoSeleccionado();
        }
    }

    private void cargarAlumnosDelCursoSeleccionado() {
        DefaultTableModel model = (DefaultTableModel) tablaNotas.getModel();
        model.setRowCount(0);
        inscripcionesActuales.clear();

        Curso curso = (Curso) comboCursos.getSelectedItem();
        if (curso == null) return;

        // 1) Traer inscripciones del curso
        List<Inscripcion> inscripciones = inscripcionDAO.listarInscripcionesPorCurso(curso.getIdCurso());

        for (Inscripcion ins : inscripciones) {
            if (ins.getAlumno() == null) continue;

            String legajo = ins.getAlumno().getLegajo();
            String nombreCompleto = ins.getAlumno().getNombre() + " " + ins.getAlumno().getApellido();

            // Tipo default sugerido, el docente puede editar
            String tipoDefault = "PARCIAL 1";

            model.addRow(new Object[]{
                    legajo,
                    nombreCompleto,
                    tipoDefault,
                    ""       // nota vacía para completar
            });

            inscripcionesActuales.add(ins);
        }
    }

    private void guardarCalificaciones() {
        Curso curso = (Curso) comboCursos.getSelectedItem();
        if (curso == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná un curso.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

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
            Inscripcion ins = inscripcionesActuales.get(i);

            String tipo = String.valueOf(model.getValueAt(i, 2)).trim();
            String notaStr = String.valueOf(model.getValueAt(i, 3)).trim();

            // Si no completó nota, saltamos esa fila
            if (notaStr.isEmpty()) {
                continue;
            }

            double nota;
            try {
                nota = Double.parseDouble(notaStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Nota inválida en la fila " + (i + 1) + ". Debe ser numérica.",
                        "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (nota < 0 || nota > 10) {
                JOptionPane.showMessageDialog(this,
                        "La nota en la fila " + (i + 1) + " debe estar entre 0 y 10.",
                        "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tipo.isEmpty()) {
                tipo = "EXAMEN"; // fallback simple
            }

            Calificacion calificacion = new Calificacion(ins, tipo, nota);

            boolean ok = calificacionDAO.agregarCalificacion(calificacion);
            if (ok) {
                guardadas++;
            }
        }

        if (guardadas > 0) {
            JOptionPane.showMessageDialog(this,
                    "Se guardaron " + guardadas + " calificaciones.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se guardó ninguna calificación. Completá las notas antes de guardar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void initListeners() {
        comboCursos.addActionListener(e -> cargarAlumnosDelCursoSeleccionado());
        guardarButton.addActionListener(e -> guardarCalificaciones());
        cerrarButton.addActionListener(e -> dispose());
    }

    // main de prueba rápida
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formCargarCalificacionesDocente("laura.doc@correo.com").setVisible(true)
        );
    }
}
