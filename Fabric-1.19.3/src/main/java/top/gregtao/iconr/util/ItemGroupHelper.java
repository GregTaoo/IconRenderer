package top.gregtao.iconr.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import top.gregtao.iconr.TasksExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemGroupHelper {

    public static ItemGroupHelper INSTANCE = new ItemGroupHelper();

    private final Map<Item, List<String>> itemListMap = new HashMap<>();

    private boolean loaded = false;

    protected ItemGroupHelper() {
        // QwQ
    }

    public void load() {
        String curLang = IconRUtils.getCurrentLanguage();
        IconRUtils.resetLanguage("zh_cn");

        ItemGroups.updateDisplayParameters(FeatureFlags.DEFAULT_ENABLED_FEATURES, true);
        ItemGroups.getGroups().forEach(itemGroup -> {
            if (!itemGroup.isSpecial()) {
                itemGroup.getDisplayStacks().forEach(itemStack -> {
                    Item item = itemStack.getItem();
                    if (!this.itemListMap.containsKey(item)) {
                        this.itemListMap.put(item, new ArrayList<>());
                    }
                    this.itemListMap.get(item).add(itemGroup.getDisplayName().getString());
                });
            }
        });
        this.loaded = true;
        TasksExecutor.LOGGER.info("Loaded item groups.");

        IconRUtils.resetLanguage(curLang);
    }

    public List<String> get(Item item) {
        if (!this.loaded) this.load();
        return this.itemListMap.getOrDefault(item, List.of());
    }
}
