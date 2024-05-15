package top.gregtao.iconr.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.SmithingTransformRecipe;
import top.gregtao.iconr.api.IRecipeDumper;
import top.gregtao.iconr.mixin.SmithingTransformRecipeAccessor;
import top.gregtao.iconr.util.IconRUtils;

public class SmithingTransformRecipeDumper implements IRecipeDumper<SmithingTransformRecipe> {

    @Override
    public JsonObject dumpInputs(JsonObject object, SmithingTransformRecipe recipe) {
        SmithingTransformRecipeAccessor accessor = (SmithingTransformRecipeAccessor) recipe;
        object.add("1", accessor.getBase().toJson());
        object.add("2", accessor.getAddition().toJson());
        object.add("3", accessor.getTemplate().toJson());
        return object;
    }

    @Override
    public JsonObject dumpOutputs(JsonObject object, SmithingTransformRecipe recipe) {
        object.add("1", IRecipeDumper.fromItemStack(recipe.getOutput(IconRUtils.getRegistryManager())));
        return object;
    }
}
