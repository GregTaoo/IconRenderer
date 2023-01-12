package top.gregtao.iconr.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.SmithingRecipe;
import top.gregtao.iconr.util.IconRUtils;

public class SmithingRecipeDumper implements RecipeDumper<SmithingRecipe> {

    public JsonObject dumpInputs(JsonObject object, SmithingRecipe recipe) {
        object.add("1", IconRUtils.getSmithingBase(recipe).toJson());
        object.add("2", IconRUtils.getSmithingAddition(recipe).toJson());
        return object;
    }

    public JsonObject dumpOutputs(JsonObject object, SmithingRecipe recipe) {
        object.add("1", RecipeDumper.dumpItemStack(recipe.getOutput()));
        return object;
    }
}
