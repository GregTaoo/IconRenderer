package top.gregtao.iconr.export;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.recipe.Recipe;
import top.gregtao.iconr.api.IExportTask;
import top.gregtao.iconr.util.IconRUtils;
import top.gregtao.iconr.api.RecipeDumperGetter;
import top.gregtao.iconr.recipe.NoRecipeDumperException;

import java.util.List;

public class ExportRecipeTask extends ExportFile implements IExportTask {
    private final JsonObject jsonObject = new JsonObject();
    private final List<Recipe<?>> recipes;

    public ExportRecipeTask(ClientPlayerEntity player, String modId) {
        super("Json/" + modId + "/" + modId + "-recipes.json");
        this.recipes = IconRUtils.getRecipesFromMod(player, modId);
    }

    @Override
    public void storeBasicInfo() {
        JsonArray recipes = new JsonArray();
        JsonArray error = new JsonArray();
        this.recipes.forEach(recipe -> {
            try {
                recipes.add(RecipeDumperGetter.dumpRecipe(recipe));
            } catch (NoRecipeDumperException e) {
                error.add(recipe.getId().toString());
            }
        });
        this.jsonObject.add("recipes", recipes);
        this.jsonObject.add("error", error);
    }

    @Override
    public void export() {
        if (this.recipes.isEmpty()) return;
        try {
            this.start();
            this.write(this.jsonObject.toString());
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
