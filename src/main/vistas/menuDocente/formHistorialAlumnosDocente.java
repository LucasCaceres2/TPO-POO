package main.vistas.menuDocente;

import main.dao.*;
import main.modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class formHistorialAlumnosDocente extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;

    private JComboBox<Curso> comboCursos;
    private JTable tablaRendimiento;
    private JButton cerrarButton;

    private final String emailDocente;

    private final DocenteDAO docenteDAO = new DocenteDAO();
    private final CursoDAO cursoDAO = new CursoDAO();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final CalificacionDAO calificacionDAO = new CalificacionDAO();

    // Para mapear filas con inscripciones (si después querés ver detalle)
    private List<Inscripcion> inscripcionesActuales = new ArrayList<>();

    // ===== CONSTRUCTORES =====
    public formHistorialAlumnosDocente(String emailDocente) {
        this.emailDocente = emailDocente;

        setContentPane(pnlPrincipal);
        setTitle("Historial de alumnos / Rendimiento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        configurarTabla();
        cargarCursosDelDocente();
        initListeners();

        pack();
        setSize(950, 450);
    }

    // solo para el diseñador
    public formHistorialAlumnosDocente() {
        this(null);
    }

    // ===== CONFIG TABLA =====
    private void configurarTabla() {
        String[] columnas = {
                "Legajo",
                "Alumno",
                "Asistencia (%)",
                "Promedio Nota",
                "Estado Curso",
                "Estado Pago"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // solo vista
            }
        };

        tablaRendimiento.setModel(model);
        tablaRendimiento.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ===== CURSOS DEL DOCENTE =====
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
            comboCursos.addItem(c); // se ve el título gracias al toString()
        }

        if (comboCursos.getItemCount() > 0) {
            comboCursos.setSelectedIndex(0);
            cargarRendimientoCursoSeleccionado();
        }
    }

    // ===== CARGAR RENDIMIENTO =====
    private void cargarRendimientoCursoSeleccionado() {
        DefaultTableModel model = (DefaultTableModel) tablaRendimiento.getModel();
        model.setRowCount(0);
        inscripcionesActuales.clear();

        Curso curso = (Curso) comboCursos.getSelectedItem();
        if (curso == null) return;

        // Traer inscripciones del curso
        List<Inscripcion> inscripciones = inscripcionDAO.listarInscripcionesPorCurso(curso.getIdCurso());

        for (Inscripcion ins : inscripciones) {
            if (ins.getAlumno() == null) continue;

            String legajo = ins.getAlumno().getLegajo();
            String alumnoNombre = ins.getAlumno().getNombre() + " " + ins.getAlumno().getApellido();

            double asistenciaPorc = calcularPorcentajeAsistencia(ins, curso);
            double promedioNota = calcularPromedioNotas(ins);

            String estadoCurso = (ins.getEstadoCurso() != null)
                    ? ins.getEstadoCurso().name()
                    : "";
            String estadoPago = (ins.getEstadoPago() != null)
                    ? ins.getEstadoPago().name()
                    : "";

            model.addRow(new Object[]{
                    legajo,
                    alumnoNombre,
                    String.format("%.1f", asistenciaPorc),
                    (promedioNota >= 0 ? String.format("%.2f", promedioNota) : "-"),
                    estadoCurso,
                    estadoPago
            });

            inscripcionesActuales.add(ins);
        }
    }

    // ===== HELPERS =====

    // Porcentaje de asistencia según asistencias del alumno en ese curso
    private double calcularPorcentajeAsistencia(Inscripcion inscripcion, Curso curso) {
        try {
            var asistencias = asistenciaDAO.obtenerAsistenciasPorInscripcion(inscripcion);
            if (asistencias == null || asistencias.isEmpty()) return 0.0;

            int presentes = 0;
            for (var a : asistencias) {
                if (a.isPresente()) presentes++;
            }

            int totalReferencia;
            if (curso.getCantidadClases() > 0) {
                totalReferencia = curso.getCantidadClases();
            } else {
                totalReferencia = asistencias.size();
            }

            if (totalReferencia == 0) return 0.0;
            return presentes * 100.0 / totalReferencia;

        } catch (Exception e) {
            System.out.println("❌ Error al calcular asistencia: " + e.getMessage());
            return 0.0;
        }
    }

    // Promedio de notas para la inscripción
    private double calcularPromedioNotas(Inscripcion inscripcion) {
        try {
            var calificaciones = calificacionDAO.obtenerCalificacionesPorInscripcion(inscripcion);
            if (calificaciones == null || calificaciones.isEmpty()) {
                return -1; // sin notas
            }

            double suma = 0;
            for (Calificacion c : calificaciones) {
                suma += c.getNota();
            }
            return suma / calificaciones.size();
        } catch (Exception e) {
            System.out.println("❌ Error al calcular promedio: " + e.getMessage());
            return -1;
        }
    }

    // ===== LISTENERS =====
    private void initListeners() {
        comboCursos.addActionListener(e -> cargarRendimientoCursoSeleccionado());
        cerrarButton.addActionListener(e -> dispose());
    }

    // ===== MAIN PRUEBA RÁPIDA =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formHistorialAlumnosDocente("laura.doc@correo.com").setVisible(true)
        );
    }
}
