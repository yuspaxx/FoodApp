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

        String createRecipeIngredientsTable = "CREATE TABLE IF NOT EXISTS recipes_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER, " +
                "ingredient_id INTEGER, " +
                "quantity INTEGER, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id));"+
                "FOREIGN KEY(ingredient_id) REFERENCES ingredients(id));";

        String createIngredientsTable = "CREATE TABLE IF NOT EXISTS ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity INTEGER, " +
                "unit TEXT); ";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createRecipesTable);
            stmt.execute(createRecipeIngredientsTable);
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

    public  static void insertRecipeIngredients(int recipe_ip, int ingredient_id, int quantity){
        String query = "INSERT INTO recipes_ingredients(recipe_id, ingredient_id, quantity) VALUES(?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recipe_ip);
            stmt.setInt(2, ingredient_id);
            stmt.setInt(3, quantity);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertIngredient(String name, int quantity, String unit) {
        String query = "insert into ingredients(name,quantity,unit) values(?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setInt(2, quantity);
            stmt.setString(3, unit);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateIngredient(String name, int quantity) {
        String query = "Update ingredients set quantity = ? where name = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setString(2, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Ingredient> getIngredientsForRecipe(int recipeId) {
        List<Ingredient> ingredients = new ArrayList<>();
        String query = "SELECT * FROM recipes_ingredients WHERE recipe_id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");

                String unit = "";
                String unit_query = "SELECT unit FROM ingredients WHERE name = ?";
                try (Connection unit_conn = connect(); PreparedStatement unit_stmt = conn.prepareStatement(unit_query)) {
                    unit_stmt.setString(1, name);
                    ResultSet unit_rs = unit_stmt.executeQuery();
                    unit = rs.getString("unit");
                } catch (SQLException unit_e) {
                    unit_e.printStackTrace();
                }

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
                "JOIN recipes_ingredients i ON r.id = i.recipe_id " +
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

    public static List<Ingredient> displayIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        String query = "SELECT * FROM ingredients where quantity > 0";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                String unit = rs.getString("unit");
                ingredients.add(new Ingredient(id, name, quantity, unit));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients;
    }
    public static Ingredient displayIngredients(String ingredientName) {
        Ingredient ingredient = new Ingredient(0, null, 0, null);;
        String query = String.format("SELECT * FROM ingredients where name = \"%s\"", ingredientName);
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            int id = rs.getInt("id");
            String name = rs.getString("name");
            int quantity = rs.getInt("quantity");
            String unit = rs.getString("unit");
            ingredient = new Ingredient(id, name, quantity, unit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredient;
    }

}
