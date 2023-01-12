package top.gregtao.iconr.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.StonecuttingRecipe;

public class StoneCuttingDumper implements RecipeDumper<StonecuttingRecipe> {
    public JsonObject dumpInputs(JsonObject object, StonecuttingRecipe recipe) {
        object.add("1", recipe.getIngredients().get(0).toJson());
        return object;
    }

    public JsonObject dumpOutputs(JsonObject object, StonecuttingRecipe recipe) {
        object.add("1", RecipeDumper.dumpItemStack(recipe.getOutput()));
        return object;
    }
}
