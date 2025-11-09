package main.app;

import main.dao.*;
import main.modelo.*;

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
        Docente docente = new Docente("Martín", "Gómez", "martin.gomez@test.com", "doc789", "MAT120");
        if (docenteDAO.agregarDocente(docente)) {
            System.out.println("✅ Docente creado con éxito");
        } else {
            docente = docenteDAO.obtenerDocentePorMatricula("MAT120");
            System.out.println("⚠️ Docente ya existente, se recupera de la BD.");
        }

        // ==== ÁREA ====
        System.out.println("\n--- Creando área ---");
        Area area = new Area(0, "Inteligencia Artificial Aplicada");
        if (areaDAO.agregarArea(area)) {
            System.out.println("✅ Área creada con éxito");
        } else {
            System.out.println("⚠️ El área ya existe, se recupera de la BD");
            List<Area> areas = areaDAO.listarAreas();
            for (Area a : areas) {
                if (a.getNombre().equalsIgnoreCase("Inteligencia Artificial Aplicada")) {
                    area = a;
                    break;
                }
            }
        }

        // ==== CURSO ====
        System.out.println("\n--- Creando curso ---");
        Curso curso = new Curso(
                0,
                "Introducción al Machine Learning",
                60,
                docente,
                area,
                "Curso teórico-práctico sobre aprendizaje automático, modelos supervisados y no supervisados.",
                20
        );

        if (cursoDAO.agregarCurso(curso)) {
            System.out.println("✅ Curso creado con éxito");
        } else {
            System.out.println("⚠️ Curso ya existente o error al insertar");
            // recuperar curso existente
            for (Curso c : cursoDAO.listarCursos()) {
                if (c.getTitulo().equalsIgnoreCase("Introducción al Machine Learning")) {
                    curso = c;
                    break;
                }
            }
        }

        // ==== ALUMNO ====
        System.out.println("\n--- Creando alumno ---");
        Alumno alumno = new Alumno("Tomás", "Rivas", "tomas.rivas@test.com", "alu789", "LEG120");
        if (alumnoDAO.agregarAlumno(alumno)) {
            System.out.println("✅ Alumno creado con éxito");
        } else {
            System.out.println("⚠️ Alumno ya existente, se recupera de la BD");
            alumno = alumnoDAO.obtenerAlumnoPorLegajo("LEG120");
        }

        // ==== INSCRIPCIÓN ====
        System.out.println("\n--- Creando inscripción ---");
        Inscripcion inscripcion = new Inscripcion(alumno, curso);

        if (inscripcionDAO.agregarInscripcion(inscripcion)) {
            System.out.println("✅ Inscripción registrada correctamente (ID: " + inscripcion.getIdInscripcion() + ")");
        } else {
            System.out.println("⚠️ El alumno ya estaba inscripto o hubo un error.");
        }

        // ==== PAGO ====
        System.out.println("\n--- Registrando pago ---");
        Pago pago = new Pago(0, new java.sql.Date(System.currentTimeMillis()), 35000.0, alumno);
        if (pagoDAO.agregarPago(pago)) {
            System.out.println("✅ Pago registrado correctamente (ID: " + pago.getIdPago() + ")");
            inscripcion.setPago(pago);
            inscripcionDAO.actualizarEstadoPago(inscripcion.getIdInscripcion(), EstadoInscripcion.PAGO);
            System.out.println("💰 Estado de pago actualizado a PAGADO");
        } else {
            System.out.println("⚠️ No se pudo registrar el pago");
        }

        // ==== CONSULTAS ====
        System.out.println("\n--- Inscripciones del alumno ---");
        List<Inscripcion> inscripciones = inscripcionDAO.listarInscripcionesPorLegajo(alumno.getLegajo());
        for (Inscripcion i : inscripciones) {
            System.out.println("➡️ Curso: " + i.getCurso().getTitulo() +
                    " | EstadoPago: " + i.getEstadoPago() +
                    " | EstadoCurso: " + i.getEstadoCurso());
        }

        System.out.println("\n===== FIN DE PRUEBA =====");
    }
}
