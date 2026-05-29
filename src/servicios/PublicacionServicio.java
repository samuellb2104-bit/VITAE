package servicios;

import dao.PublicacionDAO;
import modelos.Publicacion;
import java.util.List;

public class PublicacionServicio {
    private final PublicacionDAO dao = new PublicacionDAO();

    public List<Publicacion> listarFeedFundacion(int fundacionId, int limit) {
        // Por ahora el feed = publicaciones de la fundación
        return dao.listarPorUsuario(fundacionId, limit);
    }

    public List<Publicacion> listarPorUsuario(int usuarioId, int limit) {
        return dao.listarPorUsuario(usuarioId, limit);
    }

    public int contarPublicaciones(int usuarioId) {
        return dao.contarPorUsuario(usuarioId);
    }

    public boolean crear(int usuarioId, String contenido) {
        return dao.insertar(usuarioId, contenido);
    }
}
