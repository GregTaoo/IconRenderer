package top.gregtao.iconr.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.AbstractCookingRecipe;
import top.gregtao.iconr.api.IRecipeDumper;

public class CookingDumper implements IRecipeDumper<AbstractCookingRecipe> {

    @Override
    public JsonObject dumpInputs(JsonObject object, AbstractCookingRecipe recipe) {
        object.add("1", recipe.getIngredients().get(0).toJson());
        return object;
    }

    @Override
    public JsonObject dumpOutputs(JsonObject object, AbstractCookingRecipe recipe) {
        object.add("1", IRecipeDumper.fromItemStack(recipe.getOutput()));
        return object;
    }

    @Override
    public void dumpExtraInfo(JsonObject object, AbstractCookingRecipe recipe) {
        object.addProperty("experience", recipe.getExperience());
        object.addProperty("cookTime", recipe.getCookTime());
    }
}
