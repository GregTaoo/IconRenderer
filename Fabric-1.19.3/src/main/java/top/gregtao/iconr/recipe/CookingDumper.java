package top.gregtao.iconr.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.AbstractCookingRecipe;

public class CookingDumper implements RecipeDumper<AbstractCookingRecipe> {
    public JsonObject dumpInputs(JsonObject object, AbstractCookingRecipe recipe) {
        object.add("1", recipe.getIngredients().get(0).toJson());
        return object;
    }

    public JsonObject dumpOutputs(JsonObject object, AbstractCookingRecipe recipe) {
        object.add("1", RecipeDumper.dumpItemStack(recipe.getOutput()));
        return object;
    }

    public void dumpExtraInfo(JsonObject object, AbstractCookingRecipe recipe) {
        object.addProperty("experience", recipe.getExperience());
        object.addProperty("cookTime", recipe.getCookTime());
    }
}
