package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQL {

    private static final String URL =
        "jdbc:sqlserver://10.10.18.211:1433;"
      + "databaseName=VITAE_BD;"
      + "encrypt=false;trustServerCertificate=true;"
      + "loginTimeout=5;";

    private static final String USER = "vitae_user";      // o "sa"
    private static final String PASSWORD = "Vitae#12345"; // su clave real

    /** Devuelve una conexión abierta o null si falla. */
    public static Connection getConexion() {
        try {
            System.out.println("[DB] Cargando driver...");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            long t0 = System.currentTimeMillis();
            System.out.println("[DB] Conectando a: " + URL);
            Connection c = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ [DB] Conexión exitosa (" + (System.currentTimeMillis() - t0) + " ms)");
            return c;
        } catch (ClassNotFoundException e) {
            System.out.println("❌ [DB] Driver no encontrado: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ [DB] SQLState=" + e.getSQLState()
                + " | Code=" + e.getErrorCode()
                + " | Msg=" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        // Ojo: usa el nombre completamente calificado (aunque estemos en la misma clase)
        Connection test = ConexionSQL.getConexion(); // <-- esto evita ambigüedades con caracteres raros
        if (test == null) System.exit(1);
    }
}