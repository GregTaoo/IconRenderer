package top.gregtao.iconr.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import top.gregtao.iconr.mixin.SmithingRecipeAccessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class IconRUtils {
    public static List<ItemStack> getItemsFromMod(String modId) {
        List<ItemStack> list = new ArrayList<>();
        /* ?
        ItemGroups.getGroups().forEach(itemGroup -> {
            if (itemGroup != ItemGroups.HOTBAR && itemGroup != ItemGroups.INVENTORY && itemGroup != ItemGroups.SEARCH) {
                System.out.println(itemGroup.getId());
                System.out.println(itemGroup.getDisplayStacks().size());
                itemGroup.getDisplayStacks().forEach(itemStack -> {
                    if (Registries.ITEM.getId(itemStack.getItem()).getNamespace().equals(modId)) {
                        list.add(Pair.of(itemStack.copy(), itemGroup.getDisplayName().getString()));
                    }
                });
            }
        });
        */
        Registries.ITEM.getIds().forEach(id -> {
            if (id.getNamespace().equals(modId)) {
                list.add(new ItemStack(Registries.ITEM.get(id)));
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
        Registries.ENTITY_TYPE.forEach(entityType -> {
            if (Registries.ENTITY_TYPE.getId(entityType).getNamespace().equals(modId)) {
                Entity entity = entityType.create(MinecraftClient.getInstance().world);
                if (entity instanceof LivingEntity livingEntity) {
                    list.add(livingEntity);
                }
            }
        });
        return list;
    }

    public static <T> JsonElement tagKeyList2Json(List<TagKey<T>> list) {
        JsonArray array = new JsonArray();
        for (TagKey<T> entry : list) {
            array.add(entry.id().toString());
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
        Identifier identifier = Registries.RECIPE_SERIALIZER.getId(serializer);
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
