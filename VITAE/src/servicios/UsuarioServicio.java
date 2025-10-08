package servicios;

import dao.UsuarioDAO;
import modelos.Usuario;
//import src.dao.DonacionDAO;

public class UsuarioServicio {
    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario login(String correo, String pass) {
        return dao.login(correo, pass);
    }

    public boolean registrar(Usuario u) {
        return dao.registrarUsuario(u);
    }

    // public int contarSeguidoresFundacion(int fundacionId) {
       // return new DonacionDAO().contarDonantesUnicos(fundacionId);
   // }
}
