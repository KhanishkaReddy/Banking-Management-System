import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DBConnection {

    private static final String CONFIG_FILE = "db.properties";

    public static Connection getConnection() {

        try {
            // Load properties file
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            props.load(fis);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Load JDBC driver (optional in modern Java, but good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection
            return DriverManager.getConnection(url, user, password);

        } catch (IOException e) {
            System.out.println("❌ Error reading db.properties file");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed");
            e.printStackTrace();
        }

        return null;
    }
}