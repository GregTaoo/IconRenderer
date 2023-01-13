package top.gregtao.iconr.api;

import com.google.gson.JsonObject;
import net.minecraft.recipe.*;
import top.gregtao.iconr.recipe.NoRecipeDumperException;

import java.util.HashMap;
import java.util.Map;

public class RecipeDumperGetter {
    private static final Map<Class<? extends Recipe<?>>, IRecipeDumper<Recipe<?>>> DUMPERS = new HashMap<>();

    // Add dumpers before the client side command being executed
    @SuppressWarnings("unchecked")
    public static void addDumper(Class<? extends Recipe<?>> recipeClass, IRecipeDumper<? extends Recipe<?>> dumper) {
        DUMPERS.put(recipeClass, (IRecipeDumper<Recipe<?>>) dumper);
    }

    static {
        addDumper(ShapedRecipe.class, IRecipeDumper.SHAPED_DUMPER);
        addDumper(ShapelessRecipe.class, IRecipeDumper.SHAPELESS_DUMPER);
        addDumper(StonecuttingRecipe.class, IRecipeDumper.STONE_CUTTING_DUMPER);
        addDumper(SmithingRecipe.class, IRecipeDumper.SMITHING_DUMPER);

        addDumper(SmeltingRecipe.class, IRecipeDumper.COOKING_DUMPER);
        addDumper(CampfireCookingRecipe.class, IRecipeDumper.COOKING_DUMPER);
        addDumper(SmokingRecipe.class, IRecipeDumper.COOKING_DUMPER);
        addDumper(BlastingRecipe.class, IRecipeDumper.COOKING_DUMPER);
    }

    public static JsonObject dumpRecipe(Recipe<?> recipe) throws NoRecipeDumperException {
        if (DUMPERS.containsKey(recipe.getClass())) {
            return DUMPERS.get(recipe.getClass()).dump(recipe);
        } else {
            throw new NoRecipeDumperException("You know the rules and SO DO I.");
        }
    }
}
