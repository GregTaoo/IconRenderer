package top.gregtao.iconr.export;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import top.gregtao.iconr.util.IconRUtils;
import top.gregtao.iconr.util.RenderHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportItemTask extends ExportFile implements ExportTask {
    private final String modId;
    private final List<Map<String, String>> metas = new ArrayList<>();
    private final List<Pair<ItemStack, String>> items; // Item, CreativeTabName

    public ExportItemTask(String modId) {
        super("Json/" + modId + "/" + modId + "-items.json");
        this.items = IconRUtils.getItemsFromMod(modId);
        this.modId = modId;
    }

    public void storeBasicInfo() {
        for (Pair<ItemStack, String> item : this.items) {
            HashMap<String, String> map = new HashMap<>();
            ItemStack itemStack = item.getFirst();
            map.put("registerName", Registries.ITEM.getId(itemStack.getItem()).toString());
            map.put("CreativeTabName", item.getSecond());
            map.put("type", "Item");
            map.put("maxStackSize", String.valueOf(itemStack.getMaxCount()));
            map.put("maxDurability", String.valueOf(itemStack.getMaxDamage()));
            map.put("TagList", IconRUtils.tagKeyList2String(itemStack.streamTags().toList()));
            this.metas.add(map);
        }
    }

    public void storeDisplayName(boolean isEnglish) {
        String key = isEnglish ? "englishName" : "name";
        int amount = this.items.size();
        for (int i = 0; i < amount; ++i) {
            ItemStack itemStack = this.items.get(i).getFirst();
            this.metas.get(i).put(key, itemStack.getItem().getName().getString());
        }
    }

    public void storeImages() {
        try {
            int amount = this.items.size();
            for (int i = 0; i < amount; ++i) {
                ItemStack itemStack = this.items.get(i).getFirst();
                NativeImage large = RenderHelper.renderItemStack(128, itemStack);
                this.metas.get(i).put("largeIcon", IconRUtils.base64Encode(large));
                NativeImage small = RenderHelper.renderItemStack(32, itemStack);
                this.metas.get(i).put("smallIcon", IconRUtils.base64Encode(small));
                large.writeTo(ExportFile.of("Images/" + this.modId + "/Items/" +
                        Registries.ITEM.getId(itemStack.getItem()).getPath() + "-128px.png", true));
                small.writeTo(ExportFile.of("Images/" + this.modId + "/Items/" +
                        Registries.ITEM.getId(itemStack.getItem()).getPath() + "-32px.png", true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void export() {
        if (this.items.isEmpty()) return;
        try {
            this.start();
            for (Map<String, String> meta : this.metas) {
                this.write(IconRUtils.map2String(meta));
            }
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
