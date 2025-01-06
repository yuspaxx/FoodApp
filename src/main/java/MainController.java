import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainController {

    private static final Scanner scanner = new Scanner(System.in);
    private static final RecipeDAO recipeDAO = new RecipeDAO();

    public static void main(String[] args) {
        initializeDatabase();
        runApplication();
    }

    private static void initializeDatabase() {
        recipeDAO.createTables();
    }

    private static void runApplication() {
        while (true) {
            System.out.println("Welcome to the Recipe Finder!");
            System.out.println("1. Search recipes by ingredients");
            System.out.println("2. Search recipes by category");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    searchRecipesByIngredients();
                    break;
                case 2:
                    searchRecipesByCategory();
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void searchRecipesByIngredients() {
        System.out.print("Enter ingredients (separated by commas): ");
        String input = scanner.nextLine();
        String[] ingredientsArray = input.split(",");
        List<String> ingredientsList = new ArrayList<>();
        for (String ingredient : ingredientsArray) {
            ingredientsList.add(ingredient.trim());
        }

        List<Recipe> recipes = recipeDAO.searchRecipesByIngredients(ingredientsList);
        if (recipes.isEmpty()) {
            System.out.println("No recipes found with these ingredients.");
            suggestAlternativeRecipes(ingredientsList);
        } else {
            displayRecipes(recipes);
        }
    }

    private static void searchRecipesByCategory() {
        System.out.print("Enter recipe category (e.g., vegetarian, dessert): ");
        String category = scanner.nextLine();

        List<Recipe> recipes = recipeDAO.getRecipesByCategory(category);
        if (recipes.isEmpty()) {
            System.out.println("No recipes found in this category.");
        } else {
            displayRecipes(recipes);
        }
    }

    private static void displayRecipes(List<Recipe> recipes) {
        System.out.println("Found recipes:");
        for (Recipe recipe : recipes) {
            System.out.println(recipe);
        }
    }

    private static void suggestAlternativeRecipes(List<String> ingredientsList) {
        System.out.println("Here are some suggestions with missing ingredients:");
        List<Recipe> recipes = recipeDAO.getAllRecipes();
        for (Recipe recipe : recipes) {
            if (!containsIngredients(recipe, ingredientsList)) {
                System.out.println(recipe);
            }
        }
    }

    private static boolean containsIngredients(Recipe recipe, List<String> ingredientsList) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (!ingredientsList.contains(ingredient.getName())) {
                return false;
            }
        }
        return true;
    }
}
