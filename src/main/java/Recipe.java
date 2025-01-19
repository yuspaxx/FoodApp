import java.util.List;

public class Recipe {

    private int id;
    private String name;
    private String category;
    private String instructions;
    private List<Ingredient> ingredients;

    public Recipe(int id, String name, String category, String instructions, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("        "+name + " , " + category + "\n" + "Ingredients:\n");
        for(Ingredient ingredients: ingredients) {
            string.append(ingredients.toString()).append('\n');
        }
        string.append("\nInstructions:\n'");
        String[] instructionList = instructions.split("[.]");
        for(String instruction: instructionList){
            string.append(instruction).append('.').append("\n");
        }
        string.append('\n');
        return string.toString();
    }
}
