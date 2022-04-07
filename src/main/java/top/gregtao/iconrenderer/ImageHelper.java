package top.gregtao.iconrenderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;

import java.io.IOException;
import java.util.Base64;

public class ImageHelper {
    public FrameHelper frameHelper1, frameHelper2;
    public JsonMeta jsonMeta;
    public EntityJsonMeta entityJsonMeta;

    public ImageHelper(JsonMeta jsonMeta) {
        this.jsonMeta = jsonMeta;
        this.frameHelper1 = new FrameHelper(128, jsonMeta.itemStack);
        this.frameHelper2 = new FrameHelper(32, jsonMeta.itemStack);

        try (NativeImage image = fromFrame(this.frameHelper1.framebuffer)) {
            this.jsonMeta.largeIcon = Base64.getEncoder().encodeToString(image.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (NativeImage image = fromFrame(this.frameHelper2.framebuffer)) {
            this.jsonMeta.smallIcon = Base64.getEncoder().encodeToString(image.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImageHelper(EntityJsonMeta jsonMeta) {
        this.entityJsonMeta = jsonMeta;
        this.frameHelper1 = new FrameHelper(128, jsonMeta.entity);
        try (NativeImage image = fromFrame(this.frameHelper1.framebuffer)) {
            this.entityJsonMeta.icon = Base64.getEncoder().encodeToString(image.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NativeImage fromFrame(Framebuffer frame) {
        NativeImage img = new NativeImage(frame.textureWidth, frame.textureHeight, false);
        RenderSystem.bindTexture(frame.getColorAttachment());
        img.loadFromTextureImage(0, false);
        img.mirrorVertically();
        return img;
    }

}
