package top.gregtao.iconrenderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public static File filePath = new File("./IconRendererOutput/");
    public static File imagePath = new File(filePath + "/images/");
    public static File imagePathBI = new File(imagePath + "/items&blocks/");
    public static File imagePathEntity = new File(imagePath + "/entities/");
    public File file, entityFile;
    public String modId;
    public List<JsonMeta> jsonMetas = new ArrayList<>();
    public List<EntityJsonMeta> entityJsonMetas = new ArrayList<>();

    public FileHelper(String modId) throws IOException {
        this.modId = modId;
        this.file = new File(filePath.toString() + "/" + modId + ".json");
        this.entityFile = new File(filePath.toString() + "/" + modId + "_entity.json");
        if (!filePath.exists() && !filePath.mkdir()) {
            IconRenderer.logger.error("Could not mkdir " + filePath);
        } else if (!imagePath.exists() && !imagePath.mkdir()) {
            IconRenderer.logger.error("Could not mkdir " + imagePath);
        } else if (!imagePathBI.exists() && !imagePathBI.mkdir()) {
            IconRenderer.logger.error("Could not mkdir " + imagePathBI);
        } else if (!imagePathEntity.exists() && !imagePathEntity.mkdir()) {
            IconRenderer.logger.error("Could not mkdir " + imagePathEntity);
        } else if (createFile(this.file.toString()) && createFile(this.entityFile.toString())) {
            IconRenderer.logger.info("Exporting data of " + this.modId);
            this.fromModId();
            this.readNamesByLang();
            this.writeToFile();
            IconRenderer.logger.info("Exported data of " + this.modId);
        }
    }

    public void fromModId() {
        for (ItemGroup group : ItemGroup.GROUPS) {
            if (group != ItemGroup.HOTBAR && group != ItemGroup.INVENTORY && group != ItemGroup.SEARCH) {
                DefaultedList<ItemStack> itemStacks = DefaultedList.of();
                group.appendStacks(itemStacks);
                for (ItemStack itemStack : itemStacks) {
                    //if (Registry.ITEM.getId(itemStack.getItem()).getNamespace().equals(this.modId)) {
                    //    this.jsonMetas.add(new JsonMeta(itemStack, group));
                    //}
                    /* Debug
                    if (itemStack.isOf(Items.DIAMOND_AXE)) {
                        this.jsonMetas.add(new JsonMeta(itemStack, group));
                        break;
                    }
                    */
                }
            }
        }
        Registry.ENTITY_TYPE.forEach(this::putEntity);
    }

    public void putEntity(EntityType<? extends Entity> type) {
        if (!type.getLootTableId().getNamespace().equals(this.modId)) return;
        Entity entity = type.create(MinecraftClient.getInstance().world);
        if (!(entity instanceof LivingEntity)) return;
        this.entityJsonMetas.add(new EntityJsonMeta((LivingEntity) entity));
    }

    public void readNamesByLang() {
        resetLanguage("en_us");
        for (JsonMeta meta : this.jsonMetas) {
            meta.enName = meta.itemStack.getItem().getName().getString();
        }
        for (EntityJsonMeta meta : this.entityJsonMetas) {
            meta.enName = meta.entity.getDisplayName().getString();
            meta.mod = meta.entity.getType().getLootTableId().getNamespace();
        }
        resetLanguage("zh_cn");
        for (JsonMeta meta : this.jsonMetas) {
            meta.zhName = meta.itemStack.getItem().getName().getString();
            meta.creativeTab = meta.itemGroup.getDisplayName().getString();
        }
        for (EntityJsonMeta meta : this.entityJsonMetas) {
            meta.zhName = meta.entity.getDisplayName().getString();
        }
    }

    public void writeToFile() throws IOException {
        FileWriter writer = new FileWriter(this.file, StandardCharsets.UTF_8);
        for (JsonMeta meta : this.jsonMetas) {
            writer.write(meta.toJsonObject().toString() + "\n");
        }
        writer.close();

        writer = new FileWriter(this.entityFile, StandardCharsets.UTF_8);
        for (EntityJsonMeta meta : this.entityJsonMetas) {
            writer.write(meta.toJsonObject().toString() + "\n");
        }
        writer.close();
    }

    private static void resetLanguage(String lang) {
        MinecraftClient client = MinecraftClient.getInstance();
        System.out.println(lang);
        if (!client.options.language.equals(lang)) {
            LanguageDefinition langDefinition = new LanguageDefinition(lang, "", "", false);
            client.getLanguageManager().setLanguage(langDefinition);
            client.options.language = langDefinition.getCode();
            client.reloadResources();
            client.options.write();
            client.getLanguageManager().reload(client.getResourceManager());
        }
    }

    public static boolean createFile(String path) {
        File file = new File(path);
        try {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdir()) {
                IconRenderer.logger.error("Could not mkdir " + file.getParentFile());
                return false;
            }

            if (!file.exists() && !file.createNewFile()) {
                IconRenderer.logger.error("Could not create file " + file);
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            IconRenderer.logger.error("Could not create file " + file);
        }
        return false;
    }

}
