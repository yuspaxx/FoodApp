import java.util.List;

public class Recipe {
    private String name;
    private List<Ingredient> ingredients;
    private String category;

    public Recipe(String name, List<Ingredient> ingredients, String category) {
        this.name = name;
        this.ingredients = ingredients;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", ingredients=" + ingredients +
                ", category='" + category + '\'' +
                '}';
    }
}
