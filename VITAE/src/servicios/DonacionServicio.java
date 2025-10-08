package servicios;

import dao.DonacionDAO;
import modelos.Donacion;
import java.util.List;

public class DonacionServicio {
    private final DonacionDAO dao = new DonacionDAO();

    public int contarDonacionesRecibidas(int fundacionId) {
        return dao.contarPorFundacion(fundacionId);
    }

    public long sumarMontoRecibidoMes(int fundacionId) {
        return dao.sumarMontoMes(fundacionId);
    }

    public long sumarMontoTotal(int fundacionId) {
        return dao.sumarMontoTotal(fundacionId);
    }

    public List<Donacion> ultimasDonaciones(int fundacionId, int limit) {
        return dao.ultimasPorFundacion(fundacionId, limit);
    }
}
