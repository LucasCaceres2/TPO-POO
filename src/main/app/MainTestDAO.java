package main.app;

import main.dao.AlumnoDAO;
import main.dao.AreaDAO;
import main.dao.CursoDAO;
import main.dao.DocenteDAO;
import main.modelo.Alumno;
import main.modelo.Area;
import main.modelo.Curso;
import main.modelo.Docente;

import java.util.List;

public class MainTestDAO {
    public static void main(String[] args) {
        AlumnoDAO alumnoDAO = new AlumnoDAO();
        DocenteDAO docenteDAO = new DocenteDAO();
        AreaDAO areaDAO = new AreaDAO();
        CursoDAO cursoDAO = new CursoDAO();

        System.out.println("===== INICIO DE PRUEBA =====");

        // ==== DOCENTE ====
        System.out.println("\n--- Creando docente ---");
        Docente nuevoDocente = new Docente("Mariana", "Suarez", "mariana.suarez@test.com", "pass567", "MAT025");

        if (docenteDAO.agregarDocente(nuevoDocente)) {
            System.out.println("✅ Docente creado con éxito");
        } else {
            System.out.println("⚠️ El docente ya existe, se recupera de la BD");
            nuevoDocente = docenteDAO.obtenerDocentePorMatricula(nuevoDocente.getMatricula());
            if (nuevoDocente == null) {
                System.out.println("❌ No se pudo recuperar el docente de la BD");
                return; // sale del main para no seguir con null
            }
        }

        // ==== ÁREA ====
        System.out.println("\n--- Creando área ---");
        Area nuevaArea = new Area(0, "Inteligencia Artificial");
        if (areaDAO.agregarArea(nuevaArea)) {
            System.out.println("✅ Área creada con éxito");
        } else {
            System.out.println("⚠️ El área ya existe, se recupera de la BD");
            List<Area> areas = areaDAO.listarAreas();
            for (Area a : areas) {
                if (a.getNombre().equalsIgnoreCase("Inteligencia Artificial")) {
                    nuevaArea = a;
                    break;
                }
            }
        }

        // ==== CURSO ====
        System.out.println("\n--- Creando curso ---");
        Curso nuevoCurso = new Curso(
                0,
                "Machine Learning Aplicado",
                50,
                nuevoDocente,
                nuevaArea,
                "Curso sobre aprendizaje supervisado, redes neuronales y técnicas de IA."
        );

        if (cursoDAO.agregarCurso(nuevoCurso)) {
            System.out.println("✅ Curso creado con éxito");
        } else {
            System.out.println("⚠️ Error al crear el curso o ya existe en la BD");
        }

        // ==== ALUMNO ====
        System.out.println("\n--- Creando alumno ---");
        Alumno nuevoAlumno = new Alumno("Federico", "Gomez", "federico.gomez@test.com", "pass999", "LEG025");

        if (alumnoDAO.agregarAlumno(nuevoAlumno)) {
            System.out.println("✅ Alumno creado con éxito");
        } else {
            System.out.println("⚠️ El alumno ya existe, no se insertó.");
        }

        // ==== LISTADOS ====
        System.out.println("\n--- Listado de docentes ---");
        for (Docente d : docenteDAO.listarDocentes()) {
            System.out.println(d.getIdUsuario() + " - " + d.getNombre() + " " + d.getApellido() + " (" + d.getMatricula() + ")");
        }

        System.out.println("\n--- Listado de áreas ---");
        for (Area a : areaDAO.listarAreas()) {
            System.out.println(a.getIdArea() + " - " + a.getNombre());
        }

        System.out.println("\n--- Listado de cursos ---");
        for (Curso c : cursoDAO.listarCursos()) {
            System.out.println(c.getTitulo() + " | Docente: " + c.getDocente().getNombre() + " | Área: " + c.getArea().getNombre());
        }

        System.out.println("\n--- Listado de alumnos ---");
        for (Alumno a : alumnoDAO.listarAlumnos()) {
            System.out.println(a.getNombre() + " " + a.getApellido() + " - " + a.getLegajo());
        }

        System.out.println("\n===== FIN DE PRUEBA =====");
    }
}
