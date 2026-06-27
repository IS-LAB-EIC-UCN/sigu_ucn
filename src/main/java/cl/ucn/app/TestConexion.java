package cl.ucn.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConexion {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://127.0.0.1:5432/sigu_ucn";
            try (Connection conn = DriverManager.getConnection(url, "postgres", "postgres")) {
                System.out.println("Conexión exitosa!");
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM ticket");
                rs.next();
                System.out.println("Cantidad de tickets: " + rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}