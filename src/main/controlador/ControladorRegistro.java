package main.controlador;

import main.servicios.Plataforma;

public class ControladorRegistro {

    private final Plataforma plataforma = new Plataforma();

    /**
     * Registra un alumno desde el formulario.
     *
     * @return "REGISTRO_OK" si se registró correctamente,
     *         o "ERROR: mensaje" si hubo algún problema.
     */
    public String registrarAlumno(
            String nombre,
            String apellido,
            String email,
            String contrasena
    ) {
        try {
            // ================== VALIDACIONES BÁSICAS ==================
            if (isBlank(nombre)) {
                return "ERROR: El nombre es obligatorio.";
            }
            if (isBlank(apellido)) {
                return "ERROR: El apellido es obligatorio.";
            }
            if (isBlank(email)) {
                return "ERROR: El email es obligatorio.";
            }
            if (!emailValido(email)) {
                return "ERROR: El email no tiene un formato válido.";
            }
            if (isBlank(contrasena)) {
                return "ERROR: La contraseña es obligatoria.";
            }
            if (contrasena.length() < 4) {
                return "ERROR: La contraseña debe tener al menos 4 caracteres.";
            }

            // ================== LÓGICA DE REGISTRO ==================
            boolean ok = plataforma.registrarAlumno(
                    nombre.trim(),
                    apellido.trim(),
                    email.trim(),
                    contrasena.trim()
            );

            if (!ok) {
                return "ERROR: No se pudo registrar el alumno. Verificá que el email no esté ya usado.";
            }

            return "REGISTRO_OK";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Ocurrió un problema inesperado al registrar el alumno.";
        }
    }

    // ================== HELPERS PRIVADOS ==================
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean emailValido(String email) {
        String c = email.trim();
        return c.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
