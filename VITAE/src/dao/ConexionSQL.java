package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQL {
    // Usa instanceName para SQLEXPRESS
    private static final String URL =
        "jdbc:sqlserver://DESKTOP-9G46OTS;instanceName=SQLEXPRESS;databaseName=VITAE_BD;encrypt=false;trustServerCertificate=true;";
    private static final String USUARIO = "samuelGivana";
    private static final String CONTRASENA = "Givana123";

    public static Connection getConexion() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection c = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("✅ Conexión exitosa a VITAE_BD");
            return c;
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver JDBC no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar: " + e.getMessage());
        }
        return null;
    }
}
