import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
            System.out.println("1. Random recipe");
            System.out.println("2. Search recipe by ingredients");
            System.out.println("3. Search recipe by category");
            System.out.println("4. Add new recipe");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    showRandomRecipe();
                    break;
                case 2:
                    searchRecipesByIngredients();
                    break;
                case 3:
                    searchRecipesByCategory();
                    break;
                case 4:
                    addNewRecipe();
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            clearConsole();
        }
    }

    private static void showRandomRecipe() {
        clearConsole();
        List<Recipe> recipes = recipeDAO.getAllRecipes();
        if (recipes.isEmpty()) {
            System.out.println("No recipes available.");
        } else {
            Random random = new Random();
            Recipe randomRecipe = recipes.get(random.nextInt(recipes.size()));
            displayRecipe(randomRecipe);
        }
        postOperationMenu();
    }

    private static void searchRecipesByIngredients() {
        clearConsole();
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
        } else {
            displayRecipes(recipes);
        }
        postOperationMenu();
    }

    private static void searchRecipesByCategory() {
        clearConsole();
        System.out.print("Enter recipe category (e.g., vegetarian, dessert): ");
        String category = scanner.nextLine();
        List<Recipe> recipes = recipeDAO.getRecipesByCategory(category);
        if (recipes.isEmpty()) {
            System.out.println("No recipes found in this category.");
        } else {
            displayRecipes(recipes);
        }
        postOperationMenu();
    }

    public static void addNewRecipe() {
        clearConsole();
        System.out.print("Enter recipe name (leave empty to cancel): ");
        String recipeName = scanner.nextLine();
        if (!recipeName.isEmpty()) {
            Recipe recipe = recipeDAO.getRecipe(recipeName);
            String response = "yes";
            if (recipe.getName() != null) {
                System.out.println("Recipe with this name already exists.");
                displayRecipe(recipe);
                System.out.println("Do you still want to add it? (yes/no)");
                response = scanner.nextLine();
            }
            if (response.equals("y") || response.equals("yes")) {
                System.out.print("Enter category: ");
                String category = scanner.nextLine();

                StringBuilder instructions = new StringBuilder();
                String input;
                System.out.print("Enter instructions step by step (leave empty to finish): ");
                do {
                    input = scanner.nextLine();
                    instructions.append(input);
                } while (!input.isEmpty());

                recipeDAO.insertRecipe(recipeName, category, instructions.toString());
            }
        }
        postOperationMenu();
    }

    private static void postOperationMenu() {
        System.out.println("1. Back to menu");
        System.out.println("2. Exit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice == 1) {
            clearConsole();
            runApplication();
        } else if (choice == 2) {
            System.out.println("Goodbye!");
            System.exit(0);
        }
    }

    private static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private static void displayRecipes(List<Recipe> recipes) {
        System.out.println("Found recipes:");
        for (Recipe recipe : recipes) {
            displayRecipe(recipe);
        }
    }

    private static void displayRecipe(Recipe recipe) {
        System.out.println(recipe);
    }
}
