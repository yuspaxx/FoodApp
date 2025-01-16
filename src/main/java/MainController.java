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
            System.out.println("1. Show available recipes");
            System.out.println("2. Search recipes by ingredients");
            System.out.println("3. Search recipes by category");
            System.out.println("4. Search every recipe");
            System.out.println("5. Add new recipe");
            System.out.println("6. Display ingredients");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    searchRecipes();
                    break;
                case 2:
                    searchRecipesByIngredients();
                    break;
                case 3:
                    searchRecipesByCategory();
                    break;
                case 4:
                    searchRecipesByCategory();
                    break;
                case 5:
                    addNewRecipe();
                    break;
                case 6:
                    displayIngredients();
                    break;
                case 7:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void searchRecipes() {
        System.out.print("Recipes you can use: ");
        List<Recipe> recipes = recipeDAO.searchRecipes();
        if (recipes.isEmpty()) {
            System.out.println("No recipes found, \nhere are some recipes with some ingredients missing.");
        } else {
            displayRecipes(recipes);
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

    public static void addNewRecipe() {
        System.out.print("Enter recipe name (leave empty to cancel): ");
        String recipe_name = scanner.nextLine();
        if (!recipe_name.isEmpty()) {
            Recipe recipe = recipeDAO.getRecipe(recipe_name);
            String response = "yes";
            if (recipe.getName() != null) {
                System.out.println("Recipe with this name already exist.");
                displayRecipes(recipe);
                System.out.println("Do you still want to add it?(yes/no)");
                response = scanner.nextLine();
            }
            if (response.equals("y") || response.equals("yes")) {
                System.out.print("Enter category: ");
                String category = scanner.nextLine();
                String name;
                do {
                    System.out.println("Enter ingredients used in recipe (leave empty to stop): ");
                    name = scanner.nextLine();
                    Ingredient ingredient = recipeDAO.displayIngredients(name);
                    if (ingredient.getId() == 0) {
                        System.out.print("This ingredient is not used in any other recipe.\n" +
                                "Make sure you typed the right name before adding it to database.\n" +
                                String.format("Do you still want to add %s to ingredient list?(yes/no)", name));
                        response = scanner.nextLine();
                        if (response.equals("y") || response.equals("yes")) {
                            System.out.print("Enter quantity and unit:");
                            String data = scanner.nextLine();
                            String newIngredient = data.replaceAll("[0-9]", "0");
                            recipeDAO.insertIngredient(name, newIngredient);
                            data = data.replaceAll("[^0-9]", "");
                            int quantity = Integer.parseInt(data);
                            recipeDAO.insertRecipeIngredients(recipeDAO.getLastRecipeID() + 1, recipeDAO.displayIngredients(name).getId(), quantity);
                        }
                    } else {
                        System.out.print(String.format("Enter quantity in %s: ", ingredient.getQuantity()));
                        int quantity = Integer.parseInt(scanner.nextLine());
                        recipeDAO.insertRecipeIngredients(recipeDAO.getLastRecipeID() + 1, recipeDAO.displayIngredients(name).getId(), quantity);
                    }
                } while (!name.isEmpty());
                StringBuilder instructions = new StringBuilder();
                String input;
                System.out.print("Enter instructions step by step (leave empty to finish): ");
                do {
                    input = scanner.nextLine();
                    instructions.append(input);
                } while (!input.isEmpty());
                recipeDAO.insertRecipe(recipe_name, category, instructions.toString());
            }
        }
    }

    private static void displayIngredients() {
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
        if (response.equals("y") || response.equals("yes")) {
            System.out.print("Enter ingredient name: ");
            String name = scanner.nextLine();
            Ingredient ingredient = recipeDAO.displayIngredients(name);
            if (ingredient.getId() == 0) {
                System.out.print("This ingredient is not used in any recipe.\n" +
                        "Make sure you typed the right name before adding it to database.\n" +
                        String.format("Do you still want to add %s to ingredient list?(yes/no)", name));
                response = scanner.nextLine();
                if (response.equals("y") || response.equals("yes")) {
                    System.out.print("Enter quantity and unit:");
                    String data = scanner.nextLine();
                    recipeDAO.insertIngredient(name, data);
                }
            } else {
                System.out.print("Enter quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine());
                recipeDAO.updateIngredient(name, quantity);
            }
        }
    }

    private static void displayRecipes(List<Recipe> recipes) {
        System.out.println("Found recipes:");
        for (Recipe recipe : recipes) {
            System.out.println(recipe);
        }
    }

    private static void displayRecipes(Recipe recipe) {
        System.out.println(recipe);
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
