package top.gregtao.iconr.export;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import top.gregtao.iconr.util.IconRUtils;
import top.gregtao.iconr.util.RenderHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportEntityTask extends ExportFile implements ExportTask {
    private final String modId;
    private final List<Map<String, String>> metas = new ArrayList<>();
    private final List<LivingEntity> entities;

    public ExportEntityTask(String modId) {
        super("Json/" + modId + "/" + modId + "-entities.json");
        this.modId = modId;
        this.entities = IconRUtils.getEntitiesFromMod(modId);
    }

    public void storeBasicInfo() {
        for (LivingEntity entity : this.entities) {
            HashMap<String, String> map = new HashMap<>();
            map.put("registerName", Registries.ENTITY_TYPE.getId(entity.getType()).toString());
            this.metas.add(map);
        }
    }

    public void storeDisplayName(boolean isEnglish) {
        String key = isEnglish ? "englishName" : "name";
        int amount = this.entities.size();
        for (int i = 0; i < amount; ++i) {
            this.metas.get(i).put(key, entities.get(i).getName().getString());
        }
    }

    public void storeImages() {
        try {
            int amount = this.entities.size();
            for (int i = 0; i < amount; ++i) {
                LivingEntity entity = this.entities.get(i);
                NativeImage image = RenderHelper.renderLivingEntity(128, entity);
                this.metas.get(i).put("Icon", IconRUtils.base64Encode(image));
                image.writeTo(ExportFile.of("Images/" + this.modId + "/Entities/" +
                        Registries.ENTITY_TYPE.getId(entity.getType()).getPath() + "-128px.png", true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void export() {
        if (this.entities.isEmpty()) return;
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
