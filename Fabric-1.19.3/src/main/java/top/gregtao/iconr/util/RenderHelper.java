package top.gregtao.iconr.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;

public class RenderHelper {
    private final Framebuffer framebuffer;
    private MatrixStack modelStack;

    private RenderHelper(int size, ItemStack itemStack) {
        this.framebuffer = new SimpleFramebuffer(size, size, true, MinecraftClient.IS_SYSTEM_MAC);
        this.startRecord();
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        this.renderGuiItemIcon(itemStack, 0, 0, renderer);
        this.endRecord();
    }

    private RenderHelper(int size, LivingEntity entity) {
        this.framebuffer = new SimpleFramebuffer(size, size, true, MinecraftClient.IS_SYSTEM_MAC);
        this.startRecord();
        this.renderEntity(entity);
        this.endRecord();
    }

    private void startRecord() {
        this.modelStack = RenderSystem.getModelViewStack();
        this.modelStack.push();
        this.modelStack.loadIdentity();

        RenderSystem.backupProjectionMatrix();
        Matrix4f projectionMatrix = new Matrix4f().ortho(0, 16, 16, 0, -150, 150);
        RenderSystem.setProjectionMatrix(projectionMatrix);

        this.framebuffer.beginWrite(true);
        this.framebuffer.beginRead();
    }

    private void endRecord() {
        RenderSystem.restoreProjectionMatrix();
        this.modelStack.pop();

        this.framebuffer.endWrite();
        this.framebuffer.endRead();
    }

    private void renderEntity(LivingEntity entity) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.translate(8, 14, 0);
        InventoryScreen.drawEntity(0, 0, (int) (11 / Math.max(entity.getWidth(), entity.getHeight())), 25, 0, entity);
    }

    private void renderGuiItemIcon(ItemStack stack, int x, int y, ItemRenderer renderer) {
        this.renderGuiItemModel(stack, x, y, renderer.getModel(stack, null, null, 0), renderer);
    }

    protected void renderGuiItemModel(ItemStack stack, int x, int y, BakedModel model, ItemRenderer renderer) {
        MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate((float)x, (float)y, 100.0F + renderer.zOffset);
        matrixStack.translate(8.0F, 8.0F, 0.0F);
        matrixStack.scale(1.0F, -1.0F, 1.0F);
        matrixStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }

        renderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
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
