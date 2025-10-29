package main.exception;

public class UsuarioDuplicadoException extends Exception {
    public UsuarioDuplicadoException(String mensaje) {
        super(mensaje);
    }
}