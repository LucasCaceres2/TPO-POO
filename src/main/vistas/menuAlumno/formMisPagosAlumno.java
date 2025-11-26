package main.vistas.menuAlumno;

import main.controlador.Plataforma;
import main.dao.InscripcionDAO;
import main.dao.PagoDAO;
import main.modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class formMisPagosAlumno extends JFrame {

    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JPanel pnlDatos;
    private JTable tablaPagos;
    private JButton pagarButton;
    private JButton cerrarButton;

    private final Plataforma plataforma = new Plataforma();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final PagoDAO pagoDAO = new PagoDAO();
    private final GestorPagos gestorPagos = new GestorPagos();
    private final String emailAlumno;

    // --------- CONSTRUCTOR PRINCIPAL ----------
    public formMisPagosAlumno(String emailAlumno) {
        this.emailAlumno = emailAlumno;

        setContentPane(pnlPrincipal);
        setTitle("Mis pagos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        configurarTabla();
        cargarPagos();
        initListeners();

        pack();
        setLocationRelativeTo(null);
    }

    // para el diseñador (no usar en producción real)
    public formMisPagosAlumno() {
        this(null);
    }

    // --------- CONFIG TABLA ----------
    private void configurarTabla() {
        String[] columnas = {
                "ID Inscripción",      // 0
                "Curso",               // 1
                "Precio curso",        // 2
                "Monto pagado",        // 3
                "Deuda",               // 4
                "Medio de pago",       // 5
                "Fecha inscripción",   // 6
                "Estado Pago",         // 7
                "Estado Curso"         // 8
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPagos.setModel(model);
        tablaPagos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // --------- CARGAR PAGOS ----------
    private void cargarPagos() {
        DefaultTableModel model = (DefaultTableModel) tablaPagos.getModel();
        model.setRowCount(0);

        if (emailAlumno == null || emailAlumno.isBlank()) {
            System.out.println(" emailAlumno no seteado en formMisPagosAlumno");
            return;
        }

        // Reutilizamos las inscripciones del alumno (por email)
        List<Inscripcion> inscripciones = plataforma.obtenerInscripcionesDeAlumnoPorEmail(emailAlumno);

        for (Inscripcion insc : inscripciones) {
            Curso curso = insc.getCurso();
            String nombreCurso = (curso != null) ? curso.getTitulo() : "";
            double precio = (curso != null) ? curso.getPrecio() : 0.0;

            // Datos del pago (si existe)
            Pago pago = insc.getPago();
            double montoPagado = (pago != null) ? pago.getMonto() : 0.0;
            double deuda = Math.max(precio - montoPagado, 0.0);

            String medioPagoStr = "-";
            if (pago != null && pago.getMedioPago() != null) {
                medioPagoStr = pago.getMedioPago().name();
            }

            model.addRow(new Object[]{
                    insc.getIdInscripcion(),                // 0
                    nombreCurso,                            // 1
                    String.format("$ %.2f", precio),        // 2
                    String.format("$ %.2f", montoPagado),   // 3
                    String.format("$ %.2f", deuda),         // 4
                    medioPagoStr,                           // 5
                    insc.getFecha(),                        // 6
                    insc.getEstadoPago(),                   // 7
                    insc.getEstadoCurso()                   // 8
            });
        }
    }

    // --------- LISTENERS ----------
    private void initListeners() {

        cerrarButton.addActionListener(e -> dispose());

        pagarButton.addActionListener(e -> {
            int fila = tablaPagos.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this,
                        "Seleccioná una inscripción para pagar.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            DefaultTableModel model = (DefaultTableModel) tablaPagos.getModel();

            // col 7 = Estado Pago (misma posición que definiste)
            Object valorEstado = model.getValueAt(fila, 7);
            String estadoPagoActual = (valorEstado != null) ? valorEstado.toString() : "";

            // Solo permitimos pagar si está pendiente
            if (!estadoPagoActual.equalsIgnoreCase("PENDIENTE")
                    && !estadoPagoActual.equalsIgnoreCase("PENDIENTE_PAGO")) {
                JOptionPane.showMessageDialog(this,
                        "Esta inscripción ya está paga o no se puede modificar.",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int idInscripcion = (int) model.getValueAt(fila, 0); // col 0 = ID Inscripción

            // Recuperamos la inscripción completa
            Inscripcion inscripcion = buscarInscripcionPorId(idInscripcion);
            if (inscripcion == null) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo recuperar la inscripción seleccionada.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Curso curso = inscripcion.getCurso();
            double precioCurso = (curso != null) ? curso.getPrecio() : 0.0;

            // Elegir medio de pago
            MedioPago medioSeleccionado = (MedioPago) JOptionPane.showInputDialog(
                    this,
                    "Seleccioná el medio de pago:",
                    "Medio de pago",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    MedioPago.values(),
                    MedioPago.EFECTIVO
            );

            if (medioSeleccionado == null) {
                // canceló el diálogo
                return;
            }

            int opc = JOptionPane.showConfirmDialog(this,
                    "¿Confirmás el pago de $" + precioCurso + " para el curso '" +
                            (curso != null ? curso.getTitulo() : "") + "'?",
                    "Confirmar pago",
                    JOptionPane.YES_NO_OPTION);

            if (opc != JOptionPane.YES_OPTION) {
                return;
            }


            boolean ok = gestorPagos.pagarInscripcion(inscripcion, medioSeleccionado);

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Pago registrado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                // Recargamos la tabla: se actualizan monto pagado, deuda, medio, estado, etc.
                cargarPagos();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo registrar el pago en la base de datos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // --------- HELPER: buscar inscripción por ID ----------
    private Inscripcion buscarInscripcionPorId(int idInscripcion) {
        if (emailAlumno == null || emailAlumno.isBlank()) return null;

        List<Inscripcion> inscripciones = plataforma.obtenerInscripcionesDeAlumnoPorEmail(emailAlumno);
        for (Inscripcion i : inscripciones) {
            if (i.getIdInscripcion() == idInscripcion) {
                return i;
            }
        }
        return null;
    }

    // --------- MAIN DE PRUEBA ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formMisPagosAlumno("ana.gomez@example.com").setVisible(true)
        );
    }

}
