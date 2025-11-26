package main.vistas.menuAlumno;

import main.dao.InscripcionDAO;
import main.modelo.Curso;
import main.controlador.Plataforma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formCursosDisponiblesAlumno extends JFrame {

    private JPanel pnlPrincipal;
    private JTable tablaCursos;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JButton inscribirmeButton;
    private JButton cerrarButton;

    private final Plataforma plataforma = new Plataforma();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final String legajoAlumno; // viene del login

    // =========== CONSTRUCTORES ===========

    public formCursosDisponiblesAlumno(String LegajoAlumno) {
        this.legajoAlumno = LegajoAlumno;

        setContentPane(pnlPrincipal);
        setTitle("Cursos disponibles");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        configurarTabla();
        cargarCursos();
        initListeners();

        pack();
        setSize(900, 400);
        setLocationRelativeTo(null);
    }

    // constructor vacío solo para el diseñador
    public formCursosDisponiblesAlumno() {
        this(null);
    }

    // =========== CONFIG TABLA ===========

    private void configurarTabla() {
        String[] columnas = {
                "ID",
                "Titulo",
                "Área",
                "Docente",
                "Cupo Max",
                "Inscriptos",
                "Clases",     // 👈 nueva columna
                "descripcion"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCursos.setModel(model);
        tablaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }


    private void cargarCursos() {
        DefaultTableModel model = (DefaultTableModel) tablaCursos.getModel();
        model.setRowCount(0);

        // usa tu metodo existente
        List<Curso> cursos = plataforma.listarCursos();

        for (Curso c : cursos) {
            int inscriptos = inscripcionDAO.contarInscriptosPorCurso(c.getIdCurso());

            Object[] fila = {
                    c.getIdCurso(),
                    c.getTitulo(),
                    c.getArea() != null ? c.getArea().getNombre() : "",
                    c.getDocente() != null
                            ? c.getDocente().getNombre() + " " + c.getDocente().getApellido()
                            : "",
                    c.getCupoMax(),
                    inscriptos,
                    c.getCantidadClases(),
                    c.getDescripcion()
            };
            model.addRow(fila);
        }
    }

    // =========== LISTENERS ===========

    private void initListeners() {
        cerrarButton.addActionListener(e -> dispose());

        inscribirmeButton.addActionListener(e -> {
            int fila = tablaCursos.getSelectedRow();

            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccioná un curso.");
                return;
            }

            String tituloCurso = (String) tablaCursos.getValueAt(fila, 1);

            boolean ok = plataforma.inscribirAlumnoEnCurso(legajoAlumno, tituloCurso);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Inscripción realizada con éxito.");
                cargarCursos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo inscribirte.");
            }
        });
    }

    // =========== MAIN DE PRUEBA ===========

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formCursosDisponiblesAlumno("ana.gomez@example.com").setVisible(true)
        );
    }


}
