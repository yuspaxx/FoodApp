import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeDAO {

    private static final String DB_URL = "jdbc:sqlite:src/main/resources/foodapp.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createTables() {
        String createRecipesTable = "CREATE TABLE IF NOT EXISTS recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "category TEXT, " +
                "instructions TEXT NOT NULL);";

        String createIngredientsTable = "CREATE TABLE IF NOT EXISTS ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity TEXT, " +
                "unit TEXT, " +
                "recipe_id INTEGER, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id));";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createRecipesTable);
            stmt.execute(createIngredientsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertRecipe(String name, String category, String instructions) {
        String query = "INSERT INTO recipes(name, category, instructions) VALUES(?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, instructions);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertIngredient(String name, String quantity, String unit, int recipeId) {
        String query = "INSERT INTO ingredients(name, quantity, unit, recipe_id) VALUES(?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, quantity);
            stmt.setString(3, unit);
            stmt.setInt(4, recipeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Ingredient> getIngredientsForRecipe(int recipeId) {
        List<Ingredient> ingredients = new ArrayList<>();
        String query = "SELECT * FROM ingredients WHERE recipe_id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String quantity = rs.getString("quantity");
                String unit = rs.getString("unit");
                ingredients.add(new Ingredient(id, name, quantity, unit));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    public static Recipe getRecipeWithIngredients(int recipeId) {
        Recipe recipe = null;
        String recipeQuery = "SELECT * FROM recipes WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(recipeQuery)) {
            stmt.setInt(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String category = rs.getString("category");
                String instructions = rs.getString("instructions");
                List<Ingredient> ingredients = getIngredientsForRecipe(recipeId);
                recipe = new Recipe(recipeId, name, category, instructions, ingredients);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipe;
    }

    public static List<Recipe> searchRecipesByIngredients(List<String> ingredientNames) {
        List<Recipe> recipes = new ArrayList<>();
        String ingredientPlaceholders = String.join(",", ingredientNames);
        String query = "SELECT DISTINCT r.id, r.name, r.category, r.instructions " +
                "FROM recipes r " +
                "JOIN ingredients i ON r.id = i.recipe_id " +
                "WHERE i.name IN (" + ingredientPlaceholders + ") GROUP BY r.id";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                String instructions = rs.getString("instructions");
                List<Ingredient> ingredients = getIngredientsForRecipe(id);
                recipes.add(new Recipe(id, name, category, instructions, ingredients));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    public static List<Recipe> getRecipesByCategory(String category) {
        List<Recipe> recipes = new ArrayList<>();
        String query = "SELECT * FROM recipes WHERE category = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String instructions = rs.getString("instructions");
                List<Ingredient> ingredients = getIngredientsForRecipe(id);
                recipes.add(new Recipe(id, name, category, instructions, ingredients));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    public static List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String query = "SELECT * FROM recipes";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                String instructions = rs.getString("instructions");
                List<Ingredient> ingredients = getIngredientsForRecipe(id);
                recipes.add(new Recipe(id, name, category, instructions, ingredients));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }
}
