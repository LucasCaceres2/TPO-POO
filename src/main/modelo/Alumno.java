package main.modelo;

import main.interfaces.IUsuariosAcciones;
import main.dao.InscripcionDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Alumno extends Usuario {
    private String legajo;
    private transient List<Inscripcion> inscripciones;

    // 🔹 Constructor para crear un alumno nuevo (antes de insertarlo en BD)
    public Alumno(String nombre, String apellido, String email, String contrasena, String legajo) {
        super(nombre, apellido, email, contrasena, TipoUsuario.ALUMNO);
        this.legajo = legajo;
        this.inscripciones = new ArrayList<>();
    }

    // 🔹 Constructor para instanciar un alumno que ya existe en BD
    public Alumno(int idUsuario, String nombre, String apellido, String email, String contrasena, String legajo) {
        super(idUsuario, nombre, apellido, email, contrasena, TipoUsuario.ALUMNO);
        this.legajo = legajo;
        this.inscripciones = new ArrayList<>();
    }

    // 🔹 Getters y Setters
    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(List<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }

    // 🔹 Setters adicionales para usar en Mi Perfil
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    // 🔹 Obtener títulos de cursos inscritos (sin imprimir)
    public List<String> obtenerTitulosCursosInscritos() {
        if (inscripciones == null || inscripciones.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> titulos = new ArrayList<>();
        for (Inscripcion i : inscripciones) {
            if (i.getCurso() != null) {
                titulos.add(i.getCurso().getTitulo());
            }
        }
        return titulos;
    }

    // 🔹 Validación: ¿Puede inscribirse a este curso?
    public boolean puedeInscribirseA(Curso curso) {
        if (curso == null) return false;
        if (!curso.tieneCupo()) return false;

        // Verificar si ya está inscrito
        if (inscripciones != null) {
            for (Inscripcion i : inscripciones) {
                if (i.getCurso() != null && i.getCurso().getIdCurso() == curso.getIdCurso()) {
                    return false;
                }
            }
        }

        return true;
    }

    // 🔹 Validación de email mejorada
    private boolean esEmailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // 🔹 toString() para debugging
    @Override
    public String toString() {
        return String.format("Alumno{legajo='%s', nombre='%s %s', email='%s'}",
                legajo, nombre, apellido, email);
    }

    // 🔹 equals() y hashCode() basados en legajo
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alumno)) return false;
        Alumno alumno = (Alumno) o;
        return Objects.equals(legajo, alumno.legajo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(legajo);
    }
}