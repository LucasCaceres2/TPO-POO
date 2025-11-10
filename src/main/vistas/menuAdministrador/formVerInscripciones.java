package main.vistas.menuAdministrador;

import main.dao.CursoDAO;
import main.dao.InscripcionDAO;
import main.modelo.Curso;
import main.modelo.Inscripcion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formVerInscripciones extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlFiltros;
    private JPanel pnlTabla;

    private JComboBox<Curso> comboCurso;
    private JTextField txtLegajo;
    private JTextField txtEmail;
    private JButton btnFiltrar;
    private JButton btnVerTodas;
    private JButton btnCerrar;

    private JTable tablaInscripciones;

    private final CursoDAO cursoDAO = new CursoDAO();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();

    public formVerInscripciones() {
        setContentPane(pnlPrincipal);
        setTitle("Ver Inscripciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setSize(1100, 600);
        setLocationRelativeTo(null);

        configurarTabla();
        cargarCursosEnCombo();
        cargarTodas();

        initListeners();
    }

    // ====== Config tabla ======
    private void configurarTabla() {
        String[] columnas = {
                "ID Inscripción",
                "Fecha",
                "Legajo",
                "Alumno",
                "Email",
                "ID Curso",
                "Curso",
                "Estado Pago",
                "Estado Curso",
                "ID Pago",
                "Monto"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaInscripciones.setModel(model);
        tablaInscripciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ====== Cargar cursos combo ======
    private void cargarCursosEnCombo() {
        comboCurso.removeAllItems();
        comboCurso.addItem(null); // opción "Todos"

        List<Curso> cursos = cursoDAO.listarCursos();
        for (Curso c : cursos) {
            comboCurso.addItem(c); // se ve el título gracias a toString()
        }
    }

    // ====== Listeners ======
    private void initListeners() {

        btnVerTodas.addActionListener(e -> {
            txtLegajo.setText("");
            txtEmail.setText("");
            comboCurso.setSelectedIndex(0);
            cargarTodas();
        });

        btnFiltrar.addActionListener(e -> aplicarFiltros());

        btnCerrar.addActionListener(e -> dispose());
    }

    // ====== Cargar datos ======

    private void cargarTodas() {
        List<Inscripcion> inscripciones = inscripcionDAO.listarTodasInscripciones();
        cargarEnTabla(inscripciones);
    }

    private void aplicarFiltros() {
        Curso curso = (Curso) comboCurso.getSelectedItem();
        String legajo = txtLegajo.getText().trim();
        String email = txtEmail.getText().trim();

        // Prioridad simple:
        // 1) si hay legajo -> por legajo
        // 2) sino si hay email -> por email (usando Plataforma/otro DAO si lo tenés)
        // 3) sino si hay curso -> por curso
        // 4) sino -> todas

        if (!legajo.isEmpty()) {
            var lista = inscripcionDAO.listarInscripcionesPorLegajo(legajo);
            cargarEnTabla(lista);
            return;
        }

        if (!email.isEmpty()) {
            // Si no tenés un método directo por email, podrías:
            // - obtener Alumno por email y luego listar por legajo/idUsuario
            // Para no romper nada, por ahora mostramos aviso.
            JOptionPane.showMessageDialog(this,
                    "Filtro por email: implementá la búsqueda de alumno por email si lo necesitás.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (curso != null) {
            var lista = inscripcionDAO.listarInscripcionesPorCurso(curso.getIdCurso());
            cargarEnTabla(lista);
            return;
        }

        cargarTodas();
    }

    private void cargarEnTabla(List<Inscripcion> inscripciones) {
        DefaultTableModel model = (DefaultTableModel) tablaInscripciones.getModel();
        model.setRowCount(0);

        for (Inscripcion i : inscripciones) {
            var alu = i.getAlumno();
            var cur = i.getCurso();
            var pago = i.getPago();

            model.addRow(new Object[]{
                    i.getIdInscripcion(),
                    i.getFecha(),
                    alu != null ? alu.getLegajo() : "",
                    alu != null ? alu.getNombre() + " " + alu.getApellido() : "",
                    alu != null ? alu.getEmail() : "",
                    cur != null ? cur.getIdCurso() : null,
                    cur != null ? cur.getTitulo() : "",
                    i.getEstadoPago() != null ? i.getEstadoPago().name() : "",
                    i.getEstadoCurso() != null ? i.getEstadoCurso().name() : "",
                    pago != null ? pago.getIdPago() : null,
                    pago != null ? pago.getMonto() : null
            });
        }
    }

    // main de prueba rápida
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new formVerInscripciones().setVisible(true));
    }
}
