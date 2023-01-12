package top.gregtao.iconr.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.Registries;
import top.gregtao.iconr.util.IconRUtils;

public interface RecipeDumper<T extends Recipe<?>> {

    RecipeDumper<ShapedRecipe> SHAPED_DUMPER = new CraftingShapedDumper();
    RecipeDumper<ShapelessRecipe> SHAPELESS_DUMPER = new CraftingShapelessDumper();
    RecipeDumper<StonecuttingRecipe> STONE_CUTTING_DUMPER = new StoneCuttingDumper();
    RecipeDumper<SmithingRecipe> SMITHING_DUMPER = new SmithingRecipeDumper();
    RecipeDumper<AbstractCookingRecipe> COOKING_DUMPER = new CookingDumper();

    default JsonObject dump(T recipe) {
        JsonObject object = new JsonObject();
        object.addProperty("type", IconRUtils.getRecipeTypeId(recipe.getSerializer()));
        object.addProperty("name", recipe.getId().toString());
        object.add("input", this.dumpInputs(new JsonObject(), recipe));
        object.add("output", this.dumpOutputs(new JsonObject(), recipe));
        dumpExtraInfo(object, recipe);
        return object;
    }

    JsonObject dumpInputs(JsonObject object, T recipe);

    JsonObject dumpOutputs(JsonObject object, T recipe);

    default void dumpExtraInfo(JsonObject object, T recipe) {}

    static JsonObject dumpItemStack(ItemStack itemStack) {
        JsonObject object = new JsonObject();
        object.addProperty("item", Registries.ITEM.getId(itemStack.getItem()).toString());
        object.addProperty("count", itemStack.getCount());
        return object;
    }
}
