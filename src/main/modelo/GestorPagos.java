package main.modelo;

import main.dao.InscripcionDAO;
import main.dao.PagoDAO;

import java.util.List;

public class GestorPagos {

    private final PagoDAO pagoDAO = new PagoDAO();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();


    public boolean pagarInscripcion(Inscripcion inscripcion, MedioPago medioPago) {
        if (inscripcion == null ||
                inscripcion.getCurso() == null ||
                inscripcion.getAlumno() == null) {
            return false;
        }

        double monto = inscripcion.getCurso().getPrecio();

        // 1) Crear Pago y guardarlo en BD
        Pago pago = new Pago(monto, inscripcion.getAlumno(), medioPago);
        boolean okPago = pagoDAO.agregarPago(pago);

        if (!okPago || pago.getIdPago() <= 0) {
            return false;
        }

        // 2) Vincular pago a la inscripción y marcarla como PAGO
        return inscripcionDAO.vincularPagoAInscripcion(
                inscripcion.getIdInscripcion(),
                pago.getIdPago(),
                EstadoInscripcion.PAGO
        );
    }
}
