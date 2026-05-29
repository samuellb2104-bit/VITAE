package dao;

import modelos.Donacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonacionDAO {

    public int contarPorFundacion(int fundacionId) {
        String sql = "SELECT COUNT(*) FROM dbo.Donaciones WHERE usuario_id_destino = ?";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, fundacionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public long sumarMontoMes(int fundacionId) {
        String sql = """
            SELECT COALESCE(SUM(monto),0)
            FROM dbo.Donaciones
            WHERE usuario_id_destino = ?
              AND YEAR(fecha) = YEAR(SYSDATETIME())
              AND MONTH(fecha) = MONTH(SYSDATETIME())
        """;
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, fundacionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0L;
    }

    public long sumarMontoTotal(int fundacionId) {
        String sql = "SELECT COALESCE(SUM(monto),0) FROM dbo.Donaciones WHERE usuario_id_destino = ?";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, fundacionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0L;
    }

    public int contarDonantesUnicos(int fundacionId) {
        String sql = "SELECT COUNT(DISTINCT usuario_id_donante) FROM dbo.Donaciones WHERE usuario_id_destino = ?";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, fundacionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public List<Donacion> ultimasPorFundacion(int fundacionId, int limit) {
        String sql = """
            SELECT TOP (?) d.id, d.usuario_id_destino, d.usuario_id_donante,
                   d.monto, d.fecha, d.concepto,
                   u.nombre AS nombre_donante
            FROM dbo.Donaciones d
            LEFT JOIN dbo.Usuarios u ON u.id = d.usuario_id_donante
            WHERE d.usuario_id_destino = ?
            ORDER BY d.fecha DESC
        """;
        List<Donacion> out = new ArrayList<>();
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, fundacionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Donacion d = new Donacion(
                        rs.getInt("id"),
                        rs.getInt("usuario_id_destino"),
                        rs.getInt("usuario_id_donante"),
                        rs.getLong("monto"),
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getString("concepto")
                    );
                    // Campo opcional para mostrar nombre del donante
                    d.setNombreDonante(rs.getString("nombre_donante"));
                    out.add(d);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }
}
