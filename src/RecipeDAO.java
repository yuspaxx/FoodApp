import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeDAO {
    private static final String URL = "jdbc:sqlite:resources/db/foodapp.db";  // Шлях до вашої бази даних

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public void addRecipe(Recipe recipe) throws SQLException {
        String sql = "INSERT INTO recipes (name, category) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getCategory());
            pstmt.executeUpdate();
        }
    }

    public List<Recipe> getAllRecipes() throws SQLException {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM recipes";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                String category = rs.getString("category");
                recipes.add(new Recipe(name, new ArrayList<>(), category));
            }
        }
        return recipes;
    }
}
