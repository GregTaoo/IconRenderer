package top.gregtao.iconr.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.SmithingTrimRecipe;
import top.gregtao.iconr.api.IRecipeDumper;
import top.gregtao.iconr.mixin.SmithingTrimRecipeAccessor;
import top.gregtao.iconr.util.IconRUtils;

public class SmithingTrimRecipeDumper implements IRecipeDumper<SmithingTrimRecipe> {

    @Override
    public JsonObject dumpInputs(JsonObject object, SmithingTrimRecipe recipe) {
        SmithingTrimRecipeAccessor accessor = (SmithingTrimRecipeAccessor) recipe;
        object.add("1", accessor.getBase().toJson());
        object.add("2", accessor.getAddition().toJson());
        object.add("3", accessor.getTemplate().toJson());
        return object;
    }

    @Override
    public JsonObject dumpOutputs(JsonObject object, SmithingTrimRecipe recipe) {
        object.add("1", IRecipeDumper.fromItemStack(recipe.getOutput(IconRUtils.getRegistryManager())));
        return object;
    }
}
