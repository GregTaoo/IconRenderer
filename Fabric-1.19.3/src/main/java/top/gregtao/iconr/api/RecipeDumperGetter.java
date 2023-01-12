package top.gregtao.iconr.api;

import com.google.gson.JsonObject;
import net.minecraft.recipe.*;
import top.gregtao.iconr.recipe.NoRecipeDumperException;
import top.gregtao.iconr.recipe.RecipeDumper;

import java.util.HashMap;
import java.util.Map;

public class RecipeDumperGetter {
    private static final Map<Class<? extends Recipe<?>>, RecipeDumper<Recipe<?>>> DUMPERS = new HashMap<>();

    // WTF
    @SuppressWarnings("unchecked")
    public static void addDumper(Class<? extends Recipe<?>> recipeClass, RecipeDumper<? extends Recipe<?>> dumper) {
        DUMPERS.put(recipeClass, (RecipeDumper<Recipe<?>>) dumper);
    }

    static {
        addDumper(ShapedRecipe.class, RecipeDumper.SHAPED_DUMPER);
        addDumper(ShapelessRecipe.class, RecipeDumper.SHAPELESS_DUMPER);
        addDumper(StonecuttingRecipe.class, RecipeDumper.STONE_CUTTING_DUMPER);
        addDumper(SmithingRecipe.class, RecipeDumper.SMITHING_DUMPER);

        addDumper(SmeltingRecipe.class, RecipeDumper.COOKING_DUMPER);
        addDumper(CampfireCookingRecipe.class, RecipeDumper.COOKING_DUMPER);
        addDumper(SmokingRecipe.class, RecipeDumper.COOKING_DUMPER);
        addDumper(BlastingRecipe.class, RecipeDumper.COOKING_DUMPER);
    }

    public static JsonObject dumpRecipe(Recipe<?> recipe) throws NoRecipeDumperException {
        if (DUMPERS.containsKey(recipe.getClass())) {
            return DUMPERS.get(recipe.getClass()).dump(recipe);
        } else {
            throw new NoRecipeDumperException("You know the rules and SO DO I.");
        }
    }
}
