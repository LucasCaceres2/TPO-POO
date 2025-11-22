package main.modelo;

import java.util.Date;

public class Clase {
    private int idClase;
    private Curso curso;      // FK
    private Date fecha;
    private String titulo;    // título de la clase
    private String contenido; // descripción o desarrollo textual de la clase
}
