package main.vistas;

import main.dao.InscripcionDAO;
import main.modelo.Curso;
import main.servicios.Plataforma;

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
    private final String emailAlumno; // viene del login

    // =========== CONSTRUCTORES ===========

    public formCursosDisponiblesAlumno(String emailAlumno) {
        this.emailAlumno = emailAlumno;

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
        // 👇 ACÁ agregamos la columna "Inscriptos"
        String[] columnas = {
                "ID",
                "Titulo",
                "Área",
                "Docente",
                "Cupo Max",
                "Inscriptos",
                "Contenido"
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

        // usa tu método existente
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
                    inscriptos, // 👈 nueva columna
                    c.getContenido()
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
                JOptionPane.showMessageDialog(this,
                        "Seleccioná un curso de la tabla.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (emailAlumno == null || emailAlumno.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró el alumno logueado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idCurso = (int) tablaCursos.getValueAt(fila, 0);

            boolean ok = plataforma.inscribirAlumnoEnCurso(emailAlumno, idCurso);

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Inscripción realizada con éxito.",
                        "OK",
                        JOptionPane.INFORMATION_MESSAGE);
                // recargar tabla para ver inscriptos actualizados
                cargarCursos();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo inscribirte. Verificá si ya estás inscripto o si hay algún problema.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // =========== MAIN DE PRUEBA ===========

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formCursosDisponiblesAlumno("marcosezq@gmail.com").setVisible(true)
        );
    }
}
