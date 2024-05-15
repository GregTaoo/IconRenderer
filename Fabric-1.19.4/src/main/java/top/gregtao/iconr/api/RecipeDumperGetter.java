package top.gregtao.iconr.api;

import com.google.gson.JsonObject;
import net.minecraft.recipe.*;
import top.gregtao.iconr.recipe.*;

import java.util.HashMap;
import java.util.Map;

public class RecipeDumperGetter {

    static IRecipeDumper<ShapedRecipe> SHAPED_DUMPER = new CraftingShapedDumper();
    static IRecipeDumper<ShapelessRecipe> SHAPELESS_DUMPER = new CraftingShapelessDumper();
    static IRecipeDumper<StonecuttingRecipe> STONE_CUTTING_DUMPER = new StoneCuttingDumper();
    static IRecipeDumper<SmithingTrimRecipe> SMITHING_TRIM_DUMPER = new SmithingTrimRecipeDumper();
    static IRecipeDumper<SmithingTransformRecipe> SMITHING_TRANSFORM_DUMPER = new SmithingTransformRecipeDumper();
    static IRecipeDumper<AbstractCookingRecipe> COOKING_DUMPER = new CookingDumper();

    private static final Map<Class<? extends Recipe<?>>, IRecipeDumper<Recipe<?>>> DUMPERS = new HashMap<>();

    // Add dumpers before the client side command being executed
    @SuppressWarnings("unchecked")
    public static void addDumper(Class<? extends Recipe<?>> recipeClass, IRecipeDumper<? extends Recipe<?>> dumper) {
        DUMPERS.put(recipeClass, (IRecipeDumper<Recipe<?>>) dumper);
    }

    static {
        addDumper(ShapedRecipe.class, SHAPED_DUMPER);
        addDumper(ShapelessRecipe.class, SHAPELESS_DUMPER);
        addDumper(StonecuttingRecipe.class, STONE_CUTTING_DUMPER);
        addDumper(SmithingTrimRecipe.class, SMITHING_TRIM_DUMPER);
        addDumper(SmithingTransformRecipe.class, SMITHING_TRANSFORM_DUMPER);

        addDumper(SmeltingRecipe.class, COOKING_DUMPER);
        addDumper(CampfireCookingRecipe.class, COOKING_DUMPER);
        addDumper(SmokingRecipe.class, COOKING_DUMPER);
        addDumper(BlastingRecipe.class, COOKING_DUMPER);
    }

    public static JsonObject dumpRecipe(Recipe<?> recipe) throws NoRecipeDumperException {
        if (DUMPERS.containsKey(recipe.getClass())) {
            return DUMPERS.get(recipe.getClass()).dump(recipe);
        } else {
            throw new NoRecipeDumperException("You know the rules and SO DO I.");
        }
    }
}
