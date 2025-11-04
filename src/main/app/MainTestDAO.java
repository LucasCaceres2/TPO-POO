package main.app;

import main.dao.*;
import main.modelo.*;

import java.util.Date;
import java.util.List;

public class MainTestDAO {
    public static void main(String[] args) {
        AlumnoDAO alumnoDAO = new AlumnoDAO();
        DocenteDAO docenteDAO = new DocenteDAO();
        AreaDAO areaDAO = new AreaDAO();
        CursoDAO cursoDAO = new CursoDAO();
        InscripcionDAO inscripcionDAO = new InscripcionDAO();
        PagoDAO pagoDAO = new PagoDAO(); // suponiendo que existe

        System.out.println("===== INICIO DE PRUEBA DE INSCRIPCIONES Y PAGOS =====");

        // ==== DOCENTE ====
        System.out.println("\n--- Creando docente ---");
        Docente docente = new Docente("Luciana", "Perez", "luciana.perez@test.com", "pass123", "MAT050");
        if (docenteDAO.agregarDocente(docente)) {
            System.out.println("✅ Docente creado con éxito");
        } else {
            docente = docenteDAO.obtenerDocentePorMatricula("MAT050");
            System.out.println("⚠️ Docente ya existente, se recupera de la BD.");
        }

        // ==== ÁREA ====
        System.out.println("\n--- Creando área ---");
        Area area = new Area(0, "Desarrollo Web Avanzado");
        if (areaDAO.agregarArea(area)) {
            System.out.println("✅ Área creada con éxito");
        } else {
            System.out.println("⚠️ El área ya existe, se recupera de la BD");
            List<Area> areas = areaDAO.listarAreas();
            for (Area a : areas) {
                if (a.getNombre().equalsIgnoreCase("Desarrollo Web Avanzado")) {
                    area = a;
                    break;
                }
            }
        }

        // ==== CURSO ====
        System.out.println("\n--- Creando curso ---");
        Curso curso = new Curso(
                0,
                "Backend con Spring Boot",
                40,
                docente,
                area,
                "Curso práctico de desarrollo backend con Spring Boot, REST API y JPA."
        );

        if (cursoDAO.agregarCurso(curso)) {
            System.out.println("✅ Curso creado con éxito");
        } else {
            System.out.println("⚠️ Curso ya existente o error al insertar");
            // recuperar curso existente
            for (Curso c : cursoDAO.listarCursos()) {
                if (c.getTitulo().equalsIgnoreCase("Backend con Spring Boot")) {
                    curso = c;
                    break;
                }
            }
        }

        // ==== ALUMNO ====
        System.out.println("\n--- Creando alumno ---");
        Alumno alumno = new Alumno("Valentina", "Mendez", "valentina.mendez@test.com", "pass456", "LEG050");
        if (alumnoDAO.agregarAlumno(alumno)) {
            System.out.println("✅ Alumno creado con éxito");
        } else {
            System.out.println("⚠️ Alumno ya existente, se recupera de la BD");
            alumno = alumnoDAO.obtenerAlumnoPorLegajo("LEG050");
        }

        // ==== INSCRIPCIÓN ====
        System.out.println("\n--- Creando inscripción ---");
        Inscripcion inscripcion = new Inscripcion(alumno, curso); // usa el constructor corto que te indiqué antes

        if (inscripcionDAO.agregarInscripcion(inscripcion)) {
            System.out.println("✅ Inscripción registrada correctamente (ID: " + inscripcion.getIdInscripcion() + ")");
        } else {
            System.out.println("⚠️ El alumno ya estaba inscripto o hubo un error.");
        }

        // ==== PAGO ====
        System.out.println("\n--- Registrando pago ---");
        Pago pago = new Pago(0, new Date(), 20000.0, alumno);
        if (pagoDAO.agregarPago(pago)) {
            System.out.println("✅ Pago registrado correctamente (ID: " + pago.getIdPago() + ")");
            // actualizar inscripción con el pago
            inscripcion.setPago(pago);
            inscripcionDAO.actualizarEstadoPago(inscripcion.getIdInscripcion(), EstadoInscripcion.PAGADO);
            System.out.println("💰 Estado de pago actualizado a PAGADO");
        } else {
            System.out.println("⚠️ No se pudo registrar el pago");
        }

        // ==== CONSULTAS ====
        System.out.println("\n--- Inscripciones del alumno ---");
        List<Inscripcion> inscripciones = inscripcionDAO.listarInscripcionesPorAlumnoIdUsuario(alumno.getIdUsuario());
        for (Inscripcion i : inscripciones) {
            System.out.println("➡️ Curso: " + i.getCurso().getTitulo() +
                    " | EstadoPago: " + i.getEstadoPago() +
                    " | EstadoCurso: " + i.getEstadoCurso());
        }

        System.out.println("\n===== FIN DE PRUEBA =====");
    }
}
