package top.gregtao.iconr.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
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
    public static List<Pair<ItemStack, String>> getItemsFromMod(String modId) {
        List<Pair<ItemStack, String>> list = new ArrayList<>();
//        ItemGroups.getGroups().forEach(itemGroup -> {
//            if (itemGroup != ItemGroups.HOTBAR && itemGroup != ItemGroups.INVENTORY && itemGroup != ItemGroups.SEARCH) {
//                System.out.println(itemGroup.getId());
//                System.out.println(itemGroup.getDisplayStacks().size());
//                itemGroup.getDisplayStacks().forEach(itemStack -> {
//                    if (Registries.ITEM.getId(itemStack.getItem()).getNamespace().equals(modId)) {
//                        list.add(Pair.of(itemStack.copy(), itemGroup.getDisplayName().getString()));
//                    }
//                });
//            }
//        });
        Registries.ITEM.getIds().forEach(id -> {
            if (id.getNamespace().equals(modId)) {
                list.add(Pair.of(new ItemStack(Registries.ITEM.get(id)), ""));
            }
        });
        return list;
    }

    public static List<Recipe<?>> getRecipesFromMod(ClientPlayerEntity player, String modId) {
        if (player.world == null) return List.of();
        List<Recipe<?>> list = new ArrayList<>();
        player.world.getRecipeManager().values().forEach(recipe -> {
            if (recipe.getId().getNamespace().equals(modId)) list.add(recipe);
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

    public static String map2String(Map<String, String> map) {
        StringBuilder builder = new StringBuilder("{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append('\"').append(entry.getKey()).append('\"').append(':');
            builder.append('\"').append(entry.getValue()).append('\"').append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.append("}").toString();
    }

    public static <T> String tagKeyList2String(List<TagKey<T>> list) {
        StringBuilder builder = new StringBuilder("[");
        for (TagKey<T> entry : list) {
            builder.append(entry.id().toString()).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.append("]").toString();
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
}
