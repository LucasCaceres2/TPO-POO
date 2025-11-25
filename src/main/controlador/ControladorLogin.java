package main.controlador;

import main.controlador.Plataforma;
import main.dao.UsuarioDAO;
import main.modelo.TipoUsuario;

public class ControladorLogin {

    public TipoUsuario login(String email, String contrasena) {

        if (email == null || email.isBlank() || contrasena == null || contrasena.isBlank()) {
            return null;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        return usuarioDAO.obtenerTipoUsuario(email, contrasena);
    }
}
