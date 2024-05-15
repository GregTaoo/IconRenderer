package top.gregtao.iconr.mixin;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTrimRecipe.class)
public interface SmithingTrimRecipeAccessor {

    @Accessor
    Ingredient getBase();

    @Accessor
    Ingredient getAddition();

    @Accessor
    Ingredient getTemplate();
}
