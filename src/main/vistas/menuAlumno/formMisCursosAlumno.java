package main.vistas.menuAlumno;

import main.modelo.Inscripcion;
import main.modelo.Curso;
import main.controlador.Plataforma;
import main.dao.CursoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formMisCursosAlumno extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTable tablaMisCursos;
    private JButton cerrarButton;

    private final Plataforma plataforma = new Plataforma();
    private final String emailAlumno; // viene del login / menú alumno

    // 👇 Igual que en el historial
    private final CursoDAO cursoDAO = new CursoDAO();

    // --------- CONSTRUCTOR PRINCIPAL ----------
    public formMisCursosAlumno(String emailAlumno) {
        this.emailAlumno = emailAlumno;

        setContentPane(pnlPrincipal);
        setTitle("Mis cursos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        configurarTabla();
        cargarMisCursos();
        initListeners();

        pack();

        setLocationRelativeTo(null);
    }

    // para el diseñador (no usar en producción)
    public formMisCursosAlumno() {
        this(null);
    }

    // --------- TABLA ----------
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

        tablaMisCursos.setModel(model);
        tablaMisCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void cargarMisCursos() {
        DefaultTableModel model = (DefaultTableModel) tablaMisCursos.getModel();
        model.setRowCount(0);

        if (emailAlumno == null || emailAlumno.isBlank()) {
            System.out.println("⚠️ emailAlumno no seteado en formMisCursosAlumno");
            return;
        }

        // trae TODAS las inscripciones del alumno por email
        List<Inscripcion> inscripciones = plataforma.obtenerInscripcionesDeAlumnoPorEmail(emailAlumno);

        for (Inscripcion i : inscripciones) {
            // solo las que están CURSANDO
            if (i.getEstadoCurso() == null ||
                    !i.getEstadoCurso().name().equalsIgnoreCase("CURSANDO")) {
                continue;
            }

            Curso c = i.getCurso();

            // 👇 MISMO TRUCO QUE EN EL HISTORIAL:
            // si el curso viene sin docente, lo cargo completo desde la BD
            if (c != null && c.getDocente() == null) {
                Curso cursoCompleto = cursoDAO.obtenerCursoPorId(c.getIdCurso());
                if (cursoCompleto != null) {
                    c = cursoCompleto;
                    i.setCurso(c); // opcional, por si lo usás después
                }
            }

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

    // --------- LISTENERS ----------
    private void initListeners() {
        cerrarButton.addActionListener(e -> dispose());
    }

    // --------- MAIN DE PRUEBA ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formMisCursosAlumno("ana.gomez@example.com").setVisible(true)
        );
    }
}
