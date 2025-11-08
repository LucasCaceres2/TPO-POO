package main.modelo;

import main.interfaces.IUsuariosAcciones;
import main.dao.CursoDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Docente extends Usuario implements IUsuariosAcciones {
    private String matricula;
    private List<Curso> cursosDictados;

    // 🔹 Constructor para crear un docente nuevo (antes de insertarlo en BD)
    public Docente(String nombre, String apellido, String email, String contrasena, String matricula) {
        super(nombre, apellido, email, contrasena, TipoUsuario.DOCENTE);
        this.matricula = matricula;
        this.cursosDictados = new ArrayList<>();
    }

    // 🔹 Constructor para instanciar un docente que ya existe en BD
    public Docente(int idUsuario, String nombre, String apellido, String email, String contrasena, String matricula) {
        super(idUsuario, nombre, apellido, email, contrasena, TipoUsuario.DOCENTE);
        this.matricula = matricula;
        this.cursosDictados = new ArrayList<>();
    }

    // 🔹 Getters y Setters
    public String getMatricula() { 
        return matricula; 
    }
    
    public void setMatricula(String matricula) { 
        this.matricula = matricula; 
    }
    
    public List<Curso> getCursosDictados() { 
        return cursosDictados; 
    }
    
    public void setCursosDictados(List<Curso> cursosDictados) { 
        this.cursosDictados = cursosDictados; 
    }

    // 🔹 Cargar cursos desde BD
    public void cargarCursosDictados() {
        if (this.idUsuario <= 0) {
            return;
        }
        CursoDAO cursoDAO = new CursoDAO();
        this.cursosDictados = cursoDAO.listarCursosPorDocente(this.idUsuario);
    }

    // 🔹 Obtener títulos de cursos dictados (sin imprimir)
    public List<String> obtenerTitulosCursosDictados() {
        if (cursosDictados == null || cursosDictados.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> titulos = new ArrayList<>();
        for (Curso c : cursosDictados) {
            titulos.add(c.getTitulo());
        }
        return titulos;
    }

    // 🔹 Obtener cantidad de alumnos en un curso específico
    public int getCantidadAlumnosEnCurso(int idCurso) {
        if (cursosDictados == null) return 0;
        
        for (Curso curso : cursosDictados) {
            if (curso.getIdCurso() == idCurso) {
                return curso.getInscripciones() != null ? curso.getInscripciones().size() : 0;
            }
        }
        return 0;
    }

    // 🔹 Métodos de la interfaz (para futura GUI con Swing)
    
    @Override
    public void registrarse() {
        // No aplica: el docente es creado por el admin
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
        if (email != null && esEmailValido(email)) this.email = email;
        System.out.println("🔄 Perfil actualizado correctamente.");
    }

    // 🔹 Validación de email mejorada
    private boolean esEmailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // 🔹 toString() para debugging
    @Override
    public String toString() {
        return String.format("Docente{matricula='%s', nombre='%s %s', email='%s'}", 
            matricula, nombre, apellido, email);
    }

    // 🔹 equals() y hashCode() basados en matrícula
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Docente)) return false;
        Docente docente = (Docente) o;
        return Objects.equals(matricula, docente.matricula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matricula);
    }
}
