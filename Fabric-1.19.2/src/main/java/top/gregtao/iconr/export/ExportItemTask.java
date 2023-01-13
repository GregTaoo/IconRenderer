package top.gregtao.iconr.export;

import com.google.gson.JsonObject;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import top.gregtao.iconr.api.IExportTask;
import top.gregtao.iconr.util.IconRUtils;
import top.gregtao.iconr.util.RenderHelper;

import java.util.ArrayList;
import java.util.List;

public class ExportItemTask extends ExportFile implements IExportTask {
    private final String modId;
    private final List<JsonObject> metas = new ArrayList<>();
    private final List<ItemStack> items; // Item, CreativeTabName

    public ExportItemTask(String modId) {
        super("Json/" + modId + "/" + modId + "-items.json");
        this.items = IconRUtils.getItemsFromMod(modId);
        this.modId = modId;
    }

    @Override
    public void storeBasicInfo() {
        for (ItemStack itemStack : this.items) {
            JsonObject map = new JsonObject();
            map.addProperty("registerName", Registry.ITEM.getId(itemStack.getItem()).toString());
            map.addProperty("type", "Item");
            map.addProperty("maxStackSize", String.valueOf(itemStack.getMaxCount()));
            map.addProperty("maxDurability", String.valueOf(itemStack.getMaxDamage()));
            map.add("TagList", IconRUtils.tagKeyList2Json(itemStack.streamTags().toList()));
            this.metas.add(map);
        }
    }

    @Override
    public void storeDisplayName(boolean isEnglish) {
        String key = isEnglish ? "englishName" : "name";
        int amount = this.items.size();
        for (int i = 0; i < amount; ++i) {
            ItemStack itemStack = this.items.get(i);
            JsonObject map = this.metas.get(i);
            map.addProperty(key, itemStack.getItem().getName().getString());
            if (!isEnglish) {
                ItemGroup group = itemStack.getItem().getGroup();
                if (group != null) {
                    map.addProperty("CreativeTabName", group.getDisplayName().getString());
                }
            }
        }
    }

    @Override
    public void storeImages() {
        try {
            int amount = this.items.size();
            for (int i = 0; i < amount; ++i) {
                ItemStack itemStack = this.items.get(i);
                NativeImage large = RenderHelper.renderItemStack(128, itemStack);
                this.metas.get(i).addProperty("largeIcon", IconRUtils.base64Encode(large));
                NativeImage small = RenderHelper.renderItemStack(32, itemStack);
                this.metas.get(i).addProperty("smallIcon", IconRUtils.base64Encode(small));
                large.writeTo(ExportFile.of("Images/" + this.modId + "/Items/" +
                        Registry.ITEM.getId(itemStack.getItem()).getPath() + "-128px.png", true));
                small.writeTo(ExportFile.of("Images/" + this.modId + "/Items/" +
                        Registry.ITEM.getId(itemStack.getItem()).getPath() + "-32px.png", true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export() {
        if (this.items.isEmpty()) return;
        try {
            this.start();
            for (JsonObject meta : this.metas) {
                this.write(meta.toString());
                this.write("\n");
            }
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
