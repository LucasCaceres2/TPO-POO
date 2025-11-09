package main.vistas;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import main.dao.InscripcionDAO;
import main.modelo.Curso;
import main.modelo.EstadoInscripcion;
import main.modelo.Inscripcion;
import main.servicios.Plataforma;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private final String emailAlumno; // viene del login / menú alumno

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
        setSize(900, 400);
        setLocationRelativeTo(null);
    }

    // para el diseñador (no usar en producción real)
    public formMisPagosAlumno() {
        this(null);
    }

    // --------- CONFIG TABLA ----------
    private void configurarTabla() {
        String[] columnas = {
                "ID Inscripción",
                "Curso",
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

        tablaPagos.setModel(model);
        tablaPagos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // --------- CARGAR PAGOS ----------
    private void cargarPagos() {
        DefaultTableModel model = (DefaultTableModel) tablaPagos.getModel();
        model.setRowCount(0);

        if (emailAlumno == null || emailAlumno.isBlank()) {
            System.out.println("⚠️ emailAlumno no seteado en formMisPagosAlumno");
            return;
        }

        // Reutilizamos las inscripciones del alumno (por email)
        List<Inscripcion> inscripciones = plataforma.obtenerInscripcionesDeAlumnoPorEmail(emailAlumno);

        for (Inscripcion insc : inscripciones) {
            Curso curso = insc.getCurso();
            String nombreCurso = (curso != null) ? curso.getTitulo() : "";

            // Mostramos todas: las PENDIENTES y las PAGADAS
            // (el botón decidirá qué se puede pagar)
            model.addRow(new Object[]{
                    insc.getIdInscripcion(),     // col 0
                    nombreCurso,                 // col 1
                    insc.getFecha(),             // col 2
                    insc.getEstadoPago(),        // col 3
                    insc.getEstadoCurso()        // col 4
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

            Object valorEstado = model.getValueAt(fila, 3); // col 3 = Estado Pago
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

            int opc = JOptionPane.showConfirmDialog(this,
                    "¿Confirmás el pago de esta inscripción?",
                    "Confirmar pago",
                    JOptionPane.YES_NO_OPTION);

            if (opc != JOptionPane.YES_OPTION) {
                return;
            }

            // Cambiamos el estado en BD usando el DAO
            boolean ok = inscripcionDAO.actualizarEstadoPago(idInscripcion, EstadoInscripcion.PAGO);

            if (ok) {
                // Actualizamos la tabla en pantalla
                model.setValueAt(EstadoInscripcion.PAGO, fila, 3);

                JOptionPane.showMessageDialog(this,
                        "Pago registrado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo actualizar el estado de pago.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // --------- MAIN DE PRUEBA ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new formMisPagosAlumno("marcosezq@gmail.com").setVisible(true)
        );
    }

}
