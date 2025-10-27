package main.modelo;

public class Contenido {
    private String idContenido;
    private String tipo; // video, texto, quiz, etc.
    private String descripcion;
    private Curso curso;

    public Contenido(String idContenido, String tipo, String descripcion) {
        this.idContenido = idContenido;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public void mostrarContenido() {
        System.out.println("[" + tipo.toUpperCase() + "] " + descripcion);
    }

    public void actualizarContenido(String nuevaDescripcion) {
        this.descripcion = nuevaDescripcion;
        System.out.println("Contenido actualizado: " + nuevaDescripcion);
    }

    // Getters y Setters
    public String getIdContenido() { return idContenido; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public Curso getCurso() { return curso; }

    public void setCurso(Curso curso) { this.curso = curso; }
}
