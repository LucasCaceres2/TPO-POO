package main.vistas.menuAlumno;

import main.dao.CalificacionDAO;
import main.modelo.Calificacion;
import main.modelo.Curso;
import main.modelo.Inscripcion;
import main.controlador.Plataforma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formMisNotasAlumno extends JFrame {

    // ⚠️ Estos nombres tienen que coincidir con los del .form
    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTable tablaNotas;
    private JButton cerrarButton;

    private final String legajoAlumno;

    private final Plataforma plataforma = new Plataforma();
    private final CalificacionDAO calificacionDAO = new CalificacionDAO();

    // Constructor “real”: recibe el email desde el menú / login
    public formMisNotasAlumno(String emailAlumno) {
        this.legajoAlumno = emailAlumno;

        setContentPane(pnlPrincipal);
        setTitle("Mis notas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        configurarTabla();
        cargarNotas();
        initListeners();

        pack();
        setLocationRelativeTo(null);
    }

    // Constructor vacío SOLO para el diseñador
    public formMisNotasAlumno() {
        this(null);
    }

    private void configurarTabla() {
        String[] columnas = {
                "ID Inscripción",
                "Curso",
                "Tipo calificación",
                "Nota",
                "Fecha"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo consulta → no se edita nada
                return false;
            }
        };

        tablaNotas.setModel(model);
        tablaNotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void cargarNotas() {
        DefaultTableModel model = (DefaultTableModel) tablaNotas.getModel();
        model.setRowCount(0);

        if (legajoAlumno == null || legajoAlumno.isBlank()) {
            System.out.println("⚠ emailAlumno no seteado en formMisNotasAlumno");
            return;
        }

        // 1) Traer todas las inscripciones del alumno
        List<Inscripcion> inscripciones =
                plataforma.obtenerInscripcionesDeAlumno(legajoAlumno);

        // 2) Por cada inscripción, traer sus calificaciones
        for (Inscripcion ins : inscripciones) {
            Curso curso = ins.getCurso();
            String nombreCurso = (curso != null) ? curso.getTitulo() : "";

            List<Calificacion> calificaciones =
                    calificacionDAO.obtenerCalificacionesPorInscripcion(ins);

            for (Calificacion cal : calificaciones) {
                model.addRow(new Object[]{
                        ins.getIdInscripcion(),
                        nombreCurso,
                        cal.getTipoEvaluacion().getEtiqueta(),
                        cal.getNota(),
                        cal.getFecha()
                });
            }

        }
    }

    private void initListeners() {
        cerrarButton.addActionListener(e -> dispose());
    }

    // MAIN de prueba rápida (podés borrarlo si no lo usás)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formMisNotasAlumno("ana.gomez@example.com").setVisible(true)
        );
    }
}
