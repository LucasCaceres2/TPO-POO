package main.vistas.menuDocente;

import main.dao.CursoDAO;
import main.dao.DocenteDAO;
import main.dao.InscripcionDAO;
import main.modelo.Curso;
import main.modelo.Docente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formMisCursosDocente extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTable tablaCursos;      // Asegurate que este es el nombre del JTable en el .form
    private JButton cerrarButton;

    private final String emailDocente;
    private final DocenteDAO docenteDAO = new DocenteDAO();
    private final CursoDAO cursoDAO = new CursoDAO();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();

    public formMisCursosDocente(String emailDocente) {
        this.emailDocente = emailDocente;

        setContentPane(pnlPrincipal);
        setTitle("Mis cursos - Docente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        configurarTabla();
        cargarCursosDelDocente();
        initListeners();

        pack();
        setSize(900, 400);
    }

    // constructor vacío SOLO para el diseñador
    public formMisCursosDocente() {
        this(null);
    }

    private void configurarTabla() {
        String[] columnas = {
                "ID",
                "Título",
                "Área",
                "Cupo Max",
                "Inscriptos",
                "Clases",
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


    private void cargarCursosDelDocente() {
        DefaultTableModel model = (DefaultTableModel) tablaCursos.getModel();
        model.setRowCount(0);

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

        // 2) Traer cursos del docente
        List<Curso> cursos = cursoDAO.listarCursosPorDocente(idDocente);

        // 3) Cargar filas
        for (Curso c : cursos) {
            int inscriptos = inscripcionDAO.contarInscriptosPorCurso(c.getIdCurso());

            model.addRow(new Object[]{
                    c.getIdCurso(),
                    c.getTitulo(),
                    (c.getArea() != null ? c.getArea().getNombre() : ""),
                    c.getCupoMax(),
                    inscriptos,
                    c.getCantidadClases(),
                    c.getContenido()
            });
        }
    }

    private void initListeners() {
        cerrarButton.addActionListener(e -> dispose());
    }

    // main de prueba
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formMisCursosDocente("laura.doc@correo.com").setVisible(true)
        );
    }
}
