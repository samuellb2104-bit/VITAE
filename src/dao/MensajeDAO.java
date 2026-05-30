package dao;

import modelos.Mensaje;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensajeDAO {

    public boolean enviar(int idEmisor, int idReceptor, String contenido) {
        String sql = "INSERT INTO Mensajes (id_emisor, id_receptor, contenido, fecha_envio) "
                   + "VALUES (?, ?, ?, SYSDATETIME())";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEmisor);
            ps.setInt(2, idReceptor);
            ps.setString(3, contenido);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public List<Mensaje> listarConversacion(int idUsuario1, int idUsuario2) {
        String sql = "SELECT m.id_mensaje, m.id_emisor, m.id_receptor, "
                   + "m.contenido, m.fecha_envio, u.nombre AS nombre_emisor "
                   + "FROM Mensajes m "
                   + "JOIN Usuarios u ON u.id_usuario = m.id_emisor "
                   + "WHERE (m.id_emisor = ? AND m.id_receptor = ?) "
                   + "OR (m.id_emisor = ? AND m.id_receptor = ?) "
                   + "ORDER BY m.fecha_envio ASC";
        List<Mensaje> lista = new ArrayList<>();
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario1); ps.setInt(2, idUsuario2);
            ps.setInt(3, idUsuario2); ps.setInt(4, idUsuario1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mensaje m = new Mensaje(
                        rs.getInt("id_mensaje"),
                        rs.getInt("id_emisor"),
                        rs.getInt("id_receptor"),
                        rs.getString("contenido"),
                        rs.getTimestamp("fecha_envio").toLocalDateTime()
                    );
                    m.setNombreEmisor(rs.getString("nombre_emisor"));
                    lista.add(m);
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }

    public List<Mensaje> listarMensajesRecibidos(int idReceptor) {
        String sql = "SELECT DISTINCT m.id_emisor, u.nombre AS nombre_emisor "
                   + "FROM Mensajes m "
                   + "JOIN Usuarios u ON u.id_usuario = m.id_emisor "
                   + "WHERE m.id_receptor = ?";
        List<Mensaje> lista = new ArrayList<>();
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idReceptor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mensaje m = new Mensaje(
                        0, rs.getInt("id_emisor"), idReceptor, "", null
                    );
                    m.setNombreEmisor(rs.getString("nombre_emisor"));
                    lista.add(m);
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }
}