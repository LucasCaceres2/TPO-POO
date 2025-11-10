package main.vistas.menuAlumno;

import main.modelo.Curso;
import main.modelo.Inscripcion;
import main.servicios.Plataforma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formHistorialAlumno extends JFrame {

    private JPanel pnlPrincipal;
    private JTable table1;        // la tabla que creaste en el .form
    private JButton cerrarButton;

    private final Plataforma plataforma = new Plataforma();
    private final String emailAlumno; // viene del login / menú alumno

    // ---------- CONSTRUCTOR PRINCIPAL ----------
    public formHistorialAlumno(String emailAlumno) {
        this.emailAlumno = emailAlumno;

        setContentPane(pnlPrincipal);
        setTitle("Historial de cursos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        configurarTabla();
        cargarHistorial();
        initListeners();

        pack();
        setSize(900, 400);
        setLocationRelativeTo(null);
    }

    // constructor vacío SOLO para el diseñador
    public formHistorialAlumno() {
        this(null);
    }

    // ---------- CONFIG TABLA ----------
    private void configurarTabla() {
        String[] columnas = {
                "ID Inscripción",
                "ID Curso",
                "Curso",
                "Docente",
                "Fecha inscripción",
                "Estado Pago",
                "Estado Curso"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table1.setModel(model);
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ---------- CARGAR DATOS (TODAS LAS INSCRIPCIONES) ----------
    private void cargarHistorial() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.setRowCount(0);

        if (emailAlumno == null || emailAlumno.isBlank()) {
            System.out.println("⚠️ emailAlumno no seteado en formHistorialAlumno");
            return;
        }

        // Trae TODAS las inscripciones del alumno (sin filtrar estados)
        List<Inscripcion> inscripciones = plataforma.obtenerInscripcionesDeAlumnoPorEmail(emailAlumno);

        for (Inscripcion i : inscripciones) {
            Curso c = i.getCurso();
            String docenteNombre = "";
            if (c != null && c.getDocente() != null) {
                docenteNombre = c.getDocente().getNombre() + " " + c.getDocente().getApellido();
            }

            model.addRow(new Object[]{
                    i.getIdInscripcion(),
                    (c != null ? c.getIdCurso() : null),
                    (c != null ? c.getTitulo() : ""),
                    docenteNombre,
                    i.getFecha(),
                    i.getEstadoPago(),
                    i.getEstadoCurso()
            });
        }
    }

    // ---------- LISTENERS ----------
    private void initListeners() {
        cerrarButton.addActionListener(e -> dispose());
    }

    // ---------- MAIN DE PRUEBA ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formHistorialAlumno("marcosezq@gmail.com").setVisible(true)
        );
    }

}
