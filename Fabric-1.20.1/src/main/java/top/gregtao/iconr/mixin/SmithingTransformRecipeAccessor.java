package top.gregtao.iconr.mixin;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTransformRecipe.class)
public interface SmithingTransformRecipeAccessor {

    @Accessor
    Ingredient getBase();

    @Accessor
    Ingredient getAddition();

    @Accessor
    Ingredient getTemplate();
}
