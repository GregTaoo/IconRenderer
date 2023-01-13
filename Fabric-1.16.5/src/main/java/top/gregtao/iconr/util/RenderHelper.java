package top.gregtao.iconr.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class RenderHelper {
    private final Framebuffer framebuffer;

    private RenderHelper(int size, ItemStack itemStack) {
        this.framebuffer = new Framebuffer(size, size, true, MinecraftClient.IS_SYSTEM_MAC);
        this.startRecord();
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        this.renderGuiItemIcon(itemStack, renderer);
        this.endRecord();
    }

    private RenderHelper(int size, LivingEntity entity) {
        this.framebuffer = new Framebuffer(size, size, true, MinecraftClient.IS_SYSTEM_MAC);
        this.startRecord();
        this.renderEntity(entity);
        this.endRecord();
    }

    private void startRecord() {
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();

        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.ortho(0, 16, 16, 0, -150, 150);

        this.framebuffer.beginWrite(true);
        this.framebuffer.beginRead();
    }

    private void endRecord() {
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.popMatrix();
        RenderSystem.popMatrix();

        this.framebuffer.endWrite();
        this.framebuffer.endRead();
    }

    private void renderEntity(LivingEntity entity) {
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);

        RenderSystem.pushMatrix();
        DiffuseLighting.enable();
        GL11.glLightModelfv(GL11.GL_LIGHT_MODEL_AMBIENT, new float[]{0.7f, 0.7f, 0.7f, 0.7f}); // FORCE LIGHTING
        RenderSystem.translatef(8, 14, 0);
        InventoryScreen.drawEntity(0, 0, (int) (11 / Math.max(entity.getWidth(), entity.getHeight())), 25, 0, entity);
        DiffuseLighting.disable();
        RenderSystem.popMatrix();
    }

    private void renderGuiItemIcon(ItemStack stack, ItemRenderer renderer) {
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        renderer.renderGuiItemIcon(stack, 0, 0);
    }

    public Framebuffer getFramebuffer() {
        return this.framebuffer;
    }

    public static NativeImage fromFrame(Framebuffer frame) {
        NativeImage img = new NativeImage(frame.textureWidth, frame.textureHeight, false);
        RenderSystem.bindTexture(frame.getColorAttachment());
        img.loadFromTextureImage(0, false);
        img.mirrorVertically();
        return img;
    }

    public static NativeImage renderItemStack(int size, ItemStack itemStack) {
        return fromFrame(new RenderHelper(size, itemStack).getFramebuffer());
    }

    public static NativeImage renderLivingEntity(int size, LivingEntity entity) {
        return fromFrame(new RenderHelper(size, entity).getFramebuffer());
    }
}
