package top.gregtao.iconr.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.collection.DefaultedList;
import top.gregtao.iconr.api.IRecipeDumper;

public class CraftingShapelessDumper implements IRecipeDumper<ShapelessRecipe> {

    public JsonObject dumpInputs(JsonObject object, ShapelessRecipe recipe) {
        DefaultedList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 1; i <= 9 && i <= ingredients.size(); ++i) {
            object.add(String.valueOf(i), ingredients.get(i - 1).toJson());
        }
        return object;
    }

    public JsonObject dumpOutputs(JsonObject object, ShapelessRecipe recipe) {
        object.add("1", IRecipeDumper.dumpItemStack(recipe.getOutput()));
        return object;
    }
}
