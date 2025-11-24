package main.modelo;

public enum TipoEvaluacion {
    PARCIAL("Parcial"),
    FINAL("Final"),
    TRABAJO_PRACTICO("Trabajo práctico"),
    RECUPERATORIO("Recuperatorio"),
    ACTIVIDAD_EN_CLASE("Actividad en clase");

    private final String etiqueta;

    TipoEvaluacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}