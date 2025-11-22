package main.modelo;

public class Contenido {
    private int idContenido;
    private Clase clase;      // FK
    private String texto;     // explicación detallada, ejemplo, etc.
    private String tipo;      // opcional: “teoría”, “ejercicio”, etc.
}
