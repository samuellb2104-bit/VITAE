package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQL {

    private static final String URL =
    "jdbc:sqlserver://localhost;"
    + "databaseName=VITAE_BD;"
    + "encrypt=false;trustServerCertificate=true;"
    + "loginTimeout=5;";

private static final String USER = "vitae_app";
private static final String PASSWORD = "Vitae#2024";

public static Connection getConexion() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
}


    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("[DB] Driver cargado correctamente.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC no encontrado", e);
        }
    }

}