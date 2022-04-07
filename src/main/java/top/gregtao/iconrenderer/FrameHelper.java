package top.gregtao.iconrenderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class FrameHelper {
    public Framebuffer framebuffer;
    private MatrixStack modelStack;

    public FrameHelper(int size, ItemStack itemStack) {
        this.framebuffer = new SimpleFramebuffer(size, size, true, MinecraftClient.IS_SYSTEM_MAC);
        this.startRecord();
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        this.renderGuiItemIcon(itemStack, 0, 0, renderer);
        this.endRecord();
    }

    public FrameHelper(int size, Entity entity) {
        this.framebuffer = new SimpleFramebuffer(size, size, true, MinecraftClient.IS_SYSTEM_MAC);
        this.startRecord();
        this.renderEntity(entity);
        this.endRecord();
    }

    public void startRecord() {
        this.modelStack = RenderSystem.getModelViewStack();
        this.modelStack.push();
        this.modelStack.loadIdentity();

        RenderSystem.backupProjectionMatrix();
        Matrix4f projectionMatrix = Matrix4f.projectionMatrix(0, 16, 16, 0, -150, 150);
        RenderSystem.setProjectionMatrix(projectionMatrix);

        this.framebuffer.beginWrite(true);
        this.framebuffer.beginRead();
    }

    public void endRecord() {
        RenderSystem.restoreProjectionMatrix();
        this.modelStack.pop();

        this.framebuffer.endWrite();
        this.framebuffer.endRead();
    }

    public void renderEntity(Entity spawnEntity) {
        MinecraftClient client = MinecraftClient.getInstance();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        this.modelStack = RenderSystem.getModelViewStack();
        this.modelStack.push();
        this.modelStack.loadIdentity();
        this.modelStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(112.5f));
        this.modelStack.scale(2.5f, -2.5f, -2.5f);
        this.modelStack.translate(0.75f, 1f, 1f);
        this.modelStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(45f));
        this.modelStack.translate(-0.75f, 0, 0);
        this.modelStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(22.5f));
        this.modelStack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(22.5f));
        this.modelStack.translate(0.75f, 0, 0);

        if (!(client.player == null)) {
            spawnEntity.setPos(client.player.getX(), client.player.getY(), client.player.getZ());
        }

        client.getEntityRenderDispatcher().render(spawnEntity, 0, 0, 0, 0,
                client.getTickDelta(), this.modelStack, immediate, 15728880);

    }

    public void renderGuiItemIcon(ItemStack stack, int x, int y, ItemRenderer renderer) {
        this.renderGuiItemModel(stack, x, y, renderer.getModel(stack, null, null, 0), renderer);
    }

    protected void renderGuiItemModel(ItemStack stack, int x, int y, BakedModel model, ItemRenderer renderer) {
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 100.0F + renderer.zOffset);
        matrixStack.translate(8.0D, 8.0D, 0.0D);
        //matrixStack.scale(1.0F, 1.0F, 1.0F);
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
}
