package main.app;

import main.database.ConexionDB;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainConexionPrueba {
    public static void main(String[] args) {
        Connection conn = ConexionDB.conectar();

        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                // Simple consulta de prueba (no importa si no existe todavía)
                ResultSet rs = stmt.executeQuery("SELECT NOW();");
                if (rs.next()) {
                    System.out.println("🕒 Conexión exitosa. Fecha del servidor: " + rs.getString(1));
                }
                conn.close();
            } catch (Exception e) {
                System.out.println("Error ejecutando consulta");
                e.printStackTrace();
            }
        } else {
            System.out.println("No se pudo conectar a la base de datos.");
        }
    }
}
