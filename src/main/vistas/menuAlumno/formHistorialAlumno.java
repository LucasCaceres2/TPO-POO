package main.vistas.menuAlumno;

import main.dao.*;
import main.modelo.Alumno;
import main.modelo.Curso;
import main.modelo.Inscripcion;
import main.controlador.Plataforma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formHistorialAlumno extends JFrame {

    private JPanel pnlPrincipal;
    private JTable table1;
    private JButton cerrarButton;

    private final Plataforma plataforma = new Plataforma();
    private final String legajoAlumno;   // viene del login / menú alumno

    private final CursoDAO cursoDAO = new CursoDAO();

    // ---------- CONSTRUCTOR PRINCIPAL ----------
    public formHistorialAlumno(String emailAlumno) {
        this.legajoAlumno = emailAlumno;

        setContentPane(pnlPrincipal);
        setTitle("Historial de cursos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        configurarTabla();
        cargarHistorial();
        initListeners();

        pack();

        setLocationRelativeTo(null);
    }

    // constructor vacío SOLO para el diseñador
    public formHistorialAlumno() {
        this(null);
    }

    // ---------- CONFIG TABLA ----------
    private void configurarTabla() {
        String[] columnas = {
                "Curso",
                "Docente",
                "Fecha inscripción",
                "Estado Pago",
                "Estado de la cursada"
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

        if (legajoAlumno == null || legajoAlumno.isBlank()) {
            System.out.println("⚠️ legajoAlumno no seteado");
            return;
        }

        List<Inscripcion> inscripciones = plataforma.obtenerInscripcionesDeAlumno(legajoAlumno);

        for (Inscripcion i : inscripciones) {
            Curso c = i.getCurso();

            String nombreCurso = c != null ? c.getTitulo() : "";
            String docenteNombre = "";

            if (c != null && c.getDocente() != null) {
                docenteNombre = c.getDocente().getNombre() + " " + c.getDocente().getApellido();
            }

            model.addRow(new Object[]{
                    nombreCurso,
                    docenteNombre,
                    i.getFecha(),
                    i.getEstadoPago().name(),
                    i.getEstadoCurso().name()
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
                new formHistorialAlumno("ana.gomez@example.com").setVisible(true)
        );
    }
}
