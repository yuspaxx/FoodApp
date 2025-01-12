import java.net.InterfaceAddress;
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
            System.out.println("5. Display ingredients");
            System.out.println("6. Exit");
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
                    displayIngredients();
                    break;
                case 6:
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

    private static void displayIngredients() { // TODO - Move modify ingredients to RecipeDAO
        System.out.print("Ingredients: \n");

        List<Ingredient> ingredients = recipeDAO.displayIngredients();
        if (ingredients.isEmpty()) {
            System.out.println("No ingredients.");
        } else {
            for(int i = 0; i<ingredients.size();i++) {
                System.out.println(ingredients.get(i));
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

                    int quantity = Integer.parseInt(data.replaceAll("[^0-9]",""));
                    data = data.replaceAll("[0-9]","");
                    data = data.trim();
                    if(data.equals("ml")||data.equals("l")||data.equals("kg")||data.equals("g")||data.equals("pieces")||data.equals("pcs")) {
                        recipeDAO.insertIngredient(name, quantity, data);
                    }
                    else{
                        System.out.println(String.format("Unknown unit type %s",data)+"available units: ml, l, kg, g, pieces, pcs"); // TODO - automatic conversion to unit (e.g. pieces to pcs)
                    }
                }

            }
            else {
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
