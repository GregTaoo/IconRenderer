package top.gregtao.iconr.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import top.gregtao.iconr.mixin.SmithingRecipeAccessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

public class IconRUtils {
    public static List<ItemStack> getItemsFromMod(String modId) {
        List<ItemStack> list = new ArrayList<>();
        Registry.ITEM.getIds().forEach(id -> {
            if (id.getNamespace().equals(modId)) {
                list.add(new ItemStack(Registry.ITEM.get(id)));
            }
        });
        return list;
    }

    public static List<Recipe<?>> getRecipesFromMod(ClientPlayerEntity player, String modId) {
        if (player.world == null) return List.of();
        List<Recipe<?>> list = new ArrayList<>();
        player.world.getRecipeManager().values().forEach(recipe -> {
            if (recipe.getId().getNamespace().equals(modId)) {
                list.add(recipe);
            }
        });
        return list;
    }

    public static List<LivingEntity> getEntitiesFromMod(String modId) {
        List<LivingEntity> list = new ArrayList<>();
        Registry.ENTITY_TYPE.forEach(entityType -> {
            if (Registry.ENTITY_TYPE.getId(entityType).getNamespace().equals(modId)) {
                Entity entity = entityType.create(MinecraftClient.getInstance().world);
                if (entity instanceof LivingEntity livingEntity) {
                    list.add(livingEntity);
                }
            }
        });
        return list;
    }

    public static JsonElement tagKeyList2Json(Collection<Identifier> list) {
        JsonArray array = new JsonArray();
        for (Identifier entry : list) {
            array.add(entry.toString());
        }
        return array;
    }

    public static String base64Encode(NativeImage image) throws IOException {
        return Base64.getEncoder().encodeToString(image.getBytes());
    }

    public static Ingredient getSmithingBase(SmithingRecipe recipe) {
        return ((SmithingRecipeAccessor) recipe).getBase();
    }

    public static Ingredient getSmithingAddition(SmithingRecipe recipe) {
        return ((SmithingRecipeAccessor) recipe).getAddition();
    }

    public static String getRecipeTypeId(RecipeSerializer<?> serializer) {
        Identifier identifier = Registry.RECIPE_SERIALIZER.getId(serializer);
        return identifier == null ? "" : identifier.toString();
    }

    public static String getCurrentLanguage() {
        return MinecraftClient.getInstance().options.language;
    }

    public static void resetLanguage(String lang) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.options.language.equals(lang)) {
            LanguageDefinition langDefinition = new LanguageDefinition(lang, "", "", false);
            client.getLanguageManager().setLanguage(langDefinition);
            client.options.language = langDefinition.getCode();
            client.reloadResources();
            client.options.write();
            client.getLanguageManager().reload(client.getResourceManager());
        }
    }
}
