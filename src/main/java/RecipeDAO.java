import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RecipeDAO {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/db/foodapp.db";

    public static Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to SQLite database!");
        } catch (SQLException e) {
            System.err.println("Connection to SQLite failed: " + e.getMessage());
        }
        return connection;
    }
}
