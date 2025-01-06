import java.sql.SQLException;
import java.util.Scanner;

public class MainController {
    private static RecipeDAO recipeDAO = new RecipeDAO();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Write ingredients: ");
        String input = scanner.nextLine();
        String[] ingredients = input.split(",");

        searchRecipes(ingredients);
    }

    public static void searchRecipes(String[] ingredients) {
        try {
            for (Recipe recipe : recipeDAO.getAllRecipes()) {
                System.out.println(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
