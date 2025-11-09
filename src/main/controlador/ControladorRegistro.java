package main.controlador;

import main.servicios.Plataforma;

public class ControladorRegistro {

    private final Plataforma plataforma = new Plataforma();

    /**
     * Registra un usuario desde el formulario.
     *
     * @return "REGISTRO_OK" si se registró correctamente,
     *         o "ERROR: mensaje" si hubo algún problema.
     */
    public String registrarUsuario(
            String nombre,
            String apellido,
            String email,
            String contrasena,
            String legajoOMatricula,
            boolean esAlumno,
            boolean esDocente
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

            // Tipo de usuario (similar a lo que hacías con los checkboxes)
            if (esAlumno == esDocente) {
                // true/true o false/false → no válido
                return "ERROR: Debes seleccionar solo Alumno o solo Docente.";
            }

            if (isBlank(legajoOMatricula)) {
                if (esAlumno) {
                    return "ERROR: El legajo es obligatorio para alumnos.";
                } else {
                    return "ERROR: La matrícula es obligatoria para docentes.";
                }
            }

            // ================== LÓGICA DE REGISTRO ==================
            boolean ok;

            if (esAlumno) {
                // Usa la lógica centralizada de Plataforma
                ok = plataforma.registrarAlumno(
                        nombre.trim(),
                        apellido.trim(),
                        email.trim(),
                        contrasena.trim(),
                        legajoOMatricula.trim()
                );

                if (!ok) {
                    return "ERROR: No se pudo registrar el alumno. Verificá que el legajo o el email no estén ya usados.";
                }

                return "REGISTRO_OK";
            } else {
                // esDocente == true
                ok = plataforma.registrarDocente(
                        nombre.trim(),
                        apellido.trim(),
                        email.trim(),
                        contrasena.trim(),
                        legajoOMatricula.trim()
                );

                if (!ok) {
                    return "ERROR: No se pudo registrar el docente. Verificá que la matrícula o el email no estén ya usados.";
                }

                return "REGISTRO_OK";
            }

        } catch (Exception e) {
            // Si hay alguna excepción de SQL, conexión, etc.
            e.printStackTrace();
            return "ERROR: Ocurrió un problema inesperado al registrar el usuario.";
        }
    }

    // ================== HELPERS PRIVADOS ==================

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean emailValido(String email) {
        String c = email.trim();
        // Validación simple estilo 23-jueves
        return c.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
