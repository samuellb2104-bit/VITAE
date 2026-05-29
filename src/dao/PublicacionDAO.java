package dao;

import modelos.Publicacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PublicacionDAO {

    public List<Publicacion> listarPorUsuario(int usuarioId, int limit) {
        String sql = """
            SELECT TOP (?) id, usuario_id, contenido, fecha_creacion,
                   COALESCE(likes,0) AS likes, COALESCE(donaciones,0) AS donaciones
            FROM dbo.Publicaciones
            WHERE usuario_id = ?
            ORDER BY fecha_creacion DESC
        """;

        List<Publicacion> out = new ArrayList<>();
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Publicacion p = new Publicacion(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getString("contenido"),
                        rs.getTimestamp("fecha_creacion").toLocalDateTime(),
                        rs.getInt("likes"),
                        rs.getInt("donaciones")
                    );
                    out.add(p);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    public int contarPorUsuario(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM dbo.Publicaciones WHERE usuario_id = ?";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public boolean insertar(int usuarioId, String contenido) {
        String sql = """
            INSERT INTO dbo.Publicaciones (usuario_id, contenido, fecha_creacion, likes, donaciones)
            VALUES (?, ?, SYSDATETIME(), 0, 0)
        """;
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setString(2, contenido);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
