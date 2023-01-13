package top.gregtao.iconr.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.collection.DefaultedList;
import top.gregtao.iconr.api.IRecipeDumper;

public class CraftingShapedDumper implements IRecipeDumper<ShapedRecipe> {

    @Override
    public JsonObject dumpInputs(JsonObject object, ShapedRecipe recipe) {
        DefaultedList<Ingredient> ingredients = recipe.getIngredients();
        int width = recipe.getWidth(), height = recipe.getHeight();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                Ingredient ingredient = Ingredient.EMPTY;
                if (i < width && j < height) {
                    ingredient = ingredients.get(i + j * width);
                }
                if (!ingredient.isEmpty()) {
                    object.add(String.valueOf(i + j * 3 + 1), ingredient.toJson());
                }
            }
        }
        return object;
    }

    @Override
    public JsonObject dumpOutputs(JsonObject object, ShapedRecipe recipe) {
        object.add("1", IRecipeDumper.fromItemStack(recipe.getOutput()));
        return object;
    }
}
