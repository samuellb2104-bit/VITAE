package dao;

import modelos.Necesidad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NecesidadDAO {

    public List<Necesidad> listarPorFundacion(int idFundacion) {
        String sql = "SELECT n.id_necesidad, n.id_fundacion, n.titulo, n.descripcion, "
                   + "n.meta_monto, n.fecha_inicio, n.estado "
                   + "FROM dbo.Necesidades n "
                   + "WHERE n.id_fundacion = ? "
                   + "ORDER BY n.fecha_inicio DESC";
        List<Necesidad> out = new ArrayList<>();
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFundacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Necesidad(
                        rs.getInt("id_necesidad"),
                        rs.getInt("id_fundacion"),
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getLong("meta_monto"),
                        rs.getTimestamp("fecha_inicio").toLocalDateTime(),
                        rs.getString("estado")
                    ));
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return out;
    }

    public List<Necesidad> listarActivas() {
        String sql = "SELECT n.id_necesidad, n.id_fundacion, n.titulo, n.descripcion, "
                   + "n.meta_monto, n.fecha_inicio, n.estado, u.nombre AS nombre_fundacion "
                   + "FROM dbo.Necesidades n "
                   + "LEFT JOIN dbo.Usuarios u ON u.id_usuario = n.id_fundacion "
                   + "WHERE n.estado = 'ACTIVA' "
                   + "ORDER BY n.fecha_inicio DESC";
        List<Necesidad> out = new ArrayList<>();
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Necesidad n = new Necesidad(
                    rs.getInt("id_necesidad"),
                    rs.getInt("id_fundacion"),
                    rs.getString("titulo"),
                    rs.getString("descripcion"),
                    rs.getLong("meta_monto"),
                    rs.getTimestamp("fecha_inicio").toLocalDateTime(),
                    rs.getString("estado")
                );
                n.setNombreFundacion(rs.getString("nombre_fundacion"));
                out.add(n);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return out;
    }

    public boolean crear(int idFundacion, String titulo, String descripcion, long meta) {
        String sql = "INSERT INTO dbo.Necesidades (id_fundacion, titulo, descripcion, meta_monto) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFundacion);
            ps.setString(2, titulo);
            ps.setString(3, descripcion.isEmpty() ? null : descripcion);
            ps.setLong(4, meta);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean cerrar(int idNecesidad) {
        String sql = "UPDATE dbo.Necesidades SET estado = 'CERRADA' WHERE id_necesidad = ?";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idNecesidad);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }
}