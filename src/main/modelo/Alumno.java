package main.modelo;

import main.interfaces.IUsuariosAcciones;
import java.util.ArrayList;
import java.util.List;

public class Alumno extends Usuario implements IUsuariosAcciones {
    private String legajo;
    private transient List<Inscripcion> inscripciones; // transient → no se serializa

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
    public String getLegajo() { return legajo; }
    public void setLegajo(String legajo) { this.legajo = legajo; }
    public List<Inscripcion> getInscripciones() { return inscripciones; }
    public void setInscripciones(List<Inscripcion> inscripciones) { this.inscripciones = inscripciones; }

    // 🔹 Lógica de negocio simple
    public void inscribirse(Curso curso) {
        if (curso == null) {
            System.out.println("⚠️ El curso no puede ser nulo.");
            return;
        }

        if (curso.tieneCupo()) {
            Inscripcion inscripcion = new Inscripcion(this, curso);
            curso.agregarInscripcion(inscripcion);
            inscripciones.add(inscripcion);
            System.out.println(nombre + " se inscribió al curso: " + curso.getTitulo());
        } else {
            System.out.println("❌ No hay cupo disponible para el curso: " + curso.getTitulo());
        }
    }

    public void verCursosInscritos() {
        if (inscripciones == null || inscripciones.isEmpty()) {
            System.out.println(nombre + " no tiene cursos inscritos.");
            return;
        }

        System.out.println("📘 Cursos de " + nombre + ":");
        for (Inscripcion i : inscripciones) {
            System.out.println("- " + i.getCurso().getTitulo());
        }
    }

    // 🔹 Métodos de la interfaz
    @Override
    public void registrarse() {
        System.out.println("🟢 Registro exitoso del alumno " + nombre);
    }

    @Override
    public boolean iniciarSesion(String email, String contrasena) {
        if (this.email.equalsIgnoreCase(email) && this.contrasena.equals(contrasena)) {
            System.out.println("✅ Sesión iniciada para " + nombre);
            return true;
        }
        System.out.println("❌ Credenciales incorrectas para " + email);
        return false;
    }

    @Override
    public void cerrarSesion() {
        System.out.println("👋 Sesión cerrada para " + nombre);
    }

    @Override
    public void actualizarPerfil(String nombre, String apellido, String email) {
        if (nombre != null && !nombre.isBlank()) this.nombre = nombre;
        if (apellido != null && !apellido.isBlank()) this.apellido = apellido;
        if (email != null && email.contains("@")) this.email = email;
        System.out.println("🔄 Perfil actualizado correctamente.");
    }
}
