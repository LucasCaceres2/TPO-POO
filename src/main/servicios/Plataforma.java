package main.servicios;

import main.modelo.*;
import java.util.ArrayList;
import java.util.List;

public class Plataforma {
    private String nombre;
    private List<Pago> pagos;
    private List<Inscripcion> inscripciones;
    private List<Area> areas;

    public Plataforma(String nombre) {
        this.nombre = nombre;
        this.pagos = new ArrayList<>();
        this.inscripciones = new ArrayList<>();
        this.areas = new ArrayList<>();
    }

    public void registrarPago(Pago pago) {
        pagos.add(pago);
        System.out.println("Pago registrado: " + pago.getMonto() + " de " + pago.getAlumno().getNombre());
    }

    public void registrarInscripcion(Inscripcion inscripcion) {
        inscripciones.add(inscripcion);
        System.out.println("Inscripción registrada para: " + inscripcion.getAlumno().getNombre());
    }

    public void agregarArea(Area area) {
        areas.add(area);
        System.out.println("Área agregada: " + area.getNombre());
    }

    public void listarAreas() {
        System.out.println("Áreas registradas en la plataforma " + nombre + ":");
        for (Area area : areas) {
            System.out.println("- " + area.getNombre());
        }
    }

    // Getters
    public String getNombre() { return nombre; }
    public List<Pago> getPagos() { return pagos; }
    public List<Inscripcion> getInscripciones() { return inscripciones; }
    public List<Area> getAreas() { return areas; }
}
