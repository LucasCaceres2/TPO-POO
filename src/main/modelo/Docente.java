package main.modelo;

import main.interfaces.IUsuariosAcciones;
import java.util.ArrayList;
import java.util.List;

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

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public List<Curso> getCursosDictados() { return cursosDictados; }
    public void setCursosDictados(List<Curso> cursosDictados) { this.cursosDictados = cursosDictados; }

    @Override
    public void registrarse() {

    }

    @Override
    public boolean iniciarSesion(String email, String contrasena) {
        return false;
    }

    @Override
    public void cerrarSesion() {

    }

    @Override
    public void actualizarPerfil(String nombre, String apellido, String email) {

    }
}
