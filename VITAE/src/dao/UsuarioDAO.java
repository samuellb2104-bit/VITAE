package dao;

import modelos.Usuario;
import java.sql.*;

public class UsuarioDAO {

    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuarios (nombre, correo, contraseña, tipo_usuario, telefono, direccion, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionSQL.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getContraseña());
            ps.setString(4, usuario.getTipo_usuario());
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getDireccion());
            ps.setString(7, usuario.getDescripcion());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    public Usuario login(String correo, String contraseña) {
        String sql = "SELECT * FROM Usuarios WHERE correo = ? AND contraseña = ?";
        try (Connection con = ConexionSQL.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, contraseña);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("contraseña"),
                        rs.getString("tipo_usuario"),
                        rs.getString("telefono"),
                        rs.getString("direccion"),
                        rs.getString("descripcion")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error en login: " + e.getMessage());
        }
        return null;
    }
}
