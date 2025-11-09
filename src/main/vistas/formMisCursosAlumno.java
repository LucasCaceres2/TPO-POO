package main.vistas;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import main.modelo.Inscripcion;
import main.modelo.Curso;
import main.servicios.Plataforma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class formMisCursosAlumno extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTable tablaMisCursos;
    private JButton cerrarButton;

    private final Plataforma plataforma = new Plataforma();
    private final String emailAlumno; // viene del login / menú alumno

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
        setSize(900, 400);
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
            // mostramos solo las que están CURSANDO
            if (i.getEstadoCurso() == null ||
                    !i.getEstadoCurso().name().equalsIgnoreCase("CURSANDO")) {
                continue;
            }

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


    // --------- LISTENERS ----------
    private void initListeners() {
        cerrarButton.addActionListener(e -> dispose());
    }

    // --------- MAIN DE PRUEBA ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                // probá con el email del alumno que exista en tu BD
                new formMisCursosAlumno("marcosezq@gmail.com").setVisible(true)
        );
    }

}
