package top.gregtao.iconr.export;

import com.google.gson.JsonObject;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.Registry;
import top.gregtao.iconr.api.IExportTask;
import top.gregtao.iconr.util.IconRUtils;
import top.gregtao.iconr.util.RenderHelper;

import java.util.ArrayList;
import java.util.List;

public class ExportEntityTask extends ExportFile implements IExportTask {
    private final String modId;
    private final List<JsonObject> metas = new ArrayList<>();
    private final List<LivingEntity> entities;

    public ExportEntityTask(String modId) {
        super("Json/" + modId + "/" + modId + "-entities.json");
        this.modId = modId;
        this.entities = IconRUtils.getEntitiesFromMod(modId);
    }

    @Override
    public void storeBasicInfo() {
        for (LivingEntity entity : this.entities) {
            JsonObject map = new JsonObject();
            map.addProperty("registerName", Registry.ENTITY_TYPE.getId(entity.getType()).toString());
            this.metas.add(map);
        }
    }

    @Override
    public void storeDisplayName(boolean isEnglish) {
        String key = isEnglish ? "englishName" : "name";
        int amount = this.entities.size();
        for (int i = 0; i < amount; ++i) {
            this.metas.get(i).addProperty(key, this.entities.get(i).getName().getString());
        }
    }

    @Override
    public void storeImages() {
        try {
            int amount = this.entities.size();
            for (int i = 0; i < amount; ++i) {
                LivingEntity entity = this.entities.get(i);
                NativeImage image = RenderHelper.renderLivingEntity(128, entity);
                this.metas.get(i).addProperty("Icon", IconRUtils.base64Encode(image));
                image.writeTo(ExportFile.of("Images/" + this.modId + "/Entities/" +
                        Registry.ENTITY_TYPE.getId(entity.getType()).getPath() + "-128px.png", true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void export() {
        if (this.entities.isEmpty()) return;
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
