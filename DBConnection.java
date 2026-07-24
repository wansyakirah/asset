import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {

    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Tukar localhost kepada IP Address
    private static final String URL =
    	    "jdbc:mysql://192.168.0.125:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static Connection getConnection() throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver");

        // Step 1: Connect ke MySQL server
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

        // Step 2: Create database kalau belum ada
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS weatherdb");
        stmt.close();
        conn.close();

        // Step 3: Connect ke weatherdb
        Connection dbConn = DriverManager.getConnection(
        	    "jdbc:mysql://192.168.0.125:3306/weatherdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
        	
                USER,
                PASSWORD
        );

        // Step 4: Create table kalau belum ada
        Statement stmt2 = dbConn.createStatement();

        String createTable =
                "CREATE TABLE IF NOT EXISTS weather_data (" +
                "rowID VARCHAR(20) PRIMARY KEY, " +
                "location VARCHAR(50), " +
                "minTemp DOUBLE, " +
                "maxTemp DOUBLE, " +
                "rainfall DOUBLE, " +
                "windSpeed9am DOUBLE, " +
                "windSpeed3pm DOUBLE, " +
                "humidity9am DOUBLE, " +
                "humidity3pm DOUBLE, " +
                "temp9am DOUBLE, " +
                "temp3pm DOUBLE, " +
                "rainToday VARCHAR(5)" +
                ")";

        stmt2.executeUpdate(createTable);
        stmt2.close();

        System.out.println("✅ Database 'weatherdb' and table 'weather_data' ready!");

        return dbConn;
    }

    public static void main(String[] args) {
        try {
            getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}