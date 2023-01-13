package top.gregtao.iconr.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.StonecuttingRecipe;
import top.gregtao.iconr.api.IRecipeDumper;

public class StoneCuttingDumper implements IRecipeDumper<StonecuttingRecipe> {
    public JsonObject dumpInputs(JsonObject object, StonecuttingRecipe recipe) {
        object.add("1", recipe.getIngredients().get(0).toJson());
        return object;
    }

    public JsonObject dumpOutputs(JsonObject object, StonecuttingRecipe recipe) {
        object.add("1", IRecipeDumper.dumpItemStack(recipe.getOutput()));
        return object;
    }
}
