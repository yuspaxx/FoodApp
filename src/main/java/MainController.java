import java.awt.*;
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
        boolean exit = false;
        while (!exit) {
            System.out.println("Welcome to the Recipe Finder!");
            System.out.println("1. Random recipe");
            System.out.println("2. Search recipe by ingredients");
            System.out.println("3. Search recipe by category");
            System.out.println("4. Display Ingredients");
            System.out.println("5. Add new recipe");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.next();
            scanner.nextLine();

            int ichoice=0;
            try {
                ichoice = Integer.parseInt(choice);

            }
            catch(Exception e){
                System.out.print("Type numeric value!\n");
                ichoice = 0;
            }
            switch (ichoice) {
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
                    displayIngredients();
                    break;
                case 5:
                    addNewRecipe();
                    break;
                case 6:
                    System.out.println("Goodbye!");
                    exit = true;
                default:
                    System.out.println("Invalid option. Please try again.");
                    pressEnterToContinue();
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
        System.out.print("Enter ingredients (separated by commas, with a capital letter.): ");
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
        System.out.print("Enter recipe category (e.g., Vegetarian, Dessert) with a capital letter: ");
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

    private static void displayIngredients() { // TODO - Move modify ingredients to RecipeDAO
        System.out.print("Ingredients: \n");

        List<Ingredient> ingredients = recipeDAO.displayIngredients();
        if (ingredients.isEmpty()) {
            System.out.println("No ingredients.");
        } else {
            for (Ingredient ingredient : ingredients) {
                System.out.println(ingredient);
            }
        }

        System.out.print("Do you want to modify this list? (yes/no)\n");
        String response = scanner.nextLine();
        if(response.equals("y")||response.equals("yes")){
            System.out.print("Enter ingredient name: ");
            String name = scanner.nextLine();
            Ingredient ingredient = recipeDAO.displayIngredients(name);
            if(ingredient.getId()==0){
                System.out.print("This ingredient is not used in any recipe.\n" +
                        "Make sure you typed the right name before adding it to database.\n" +
                        String.format("Do you still want to add %s to ingredient list?(yes/no)",name));
                response = scanner.nextLine();
                if(response.equals("y")||response.equals("yes")){
                    System.out.print("Enter quantity and unit:");
                    String data = scanner.nextLine();
                    recipeDAO.insertIngredient(name, data);
                }

            }
            else {
                System.out.print("Enter quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine());
                recipeDAO.updateIngredient(name, quantity);
            }
        }

    }
    private static void pressEnterToContinue()
    {
        System.out.println("Press Enter key to continue...");
        scanner.nextLine();
    }

}
