package top.gregtao.iconrenderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class IconRenderer implements ClientModInitializer {
	public static Logger logger = LogManager.getLogger("IconRenderer");
	public static KeyBinding recordMobKeyY;

	@Override
	public void onInitializeClient() {
		ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("exporticons")
				.then(ClientCommandManager.argument("modid", StringArgumentType.string())
						.executes((context) -> {
							String modId = context.getArgument("modid", String.class);
							try {
								new FileHelper(modId);
							} catch (IOException e) {
								e.printStackTrace();
							}
							return 1;
						})
				)
		);
		ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("shootgif")
				.then(ClientCommandManager.literal("frame").then(ClientCommandManager.argument("frames", IntegerArgumentType.integer(0, 1000))
						.executes(context -> {
							maxSize = IntegerArgumentType.getInteger(context, "frames");
							context.getSource().getPlayer().sendMessage(Text.of("Set max amount of frames: " + maxSize), false);
							return 1;
						})
				))
				.then(ClientCommandManager.literal("scale").then(ClientCommandManager.argument("scale", FloatArgumentType.floatArg(0.01f, 1000f))
						.executes(context -> {
							scale = FloatArgumentType.getFloat(context, "scale");
							context.getSource().getPlayer().sendMessage(Text.of("Set the scaling factor: " + scale), false);
							return 1;
						})
				))
				.then(ClientCommandManager.literal("transparent").then(ClientCommandManager.argument("boolean", IntegerArgumentType.integer(0, 1))
						.executes(context -> {
							transparent = IntegerArgumentType.getInteger(context, "boolean") == 1;
							context.getSource().getPlayer().sendMessage(Text.of("Set transparent value: " + transparent), false);
							return 1;
						})
				))
				.then(ClientCommandManager.literal("rotate").then(ClientCommandManager.argument("x", IntegerArgumentType.integer(-66, 66))
								.then(ClientCommandManager.argument("y", IntegerArgumentType.integer(-66, 66))
										.executes(context -> {
											gazeX = IntegerArgumentType.getInteger(context, "x");
											gazeY = IntegerArgumentType.getInteger(context, "y");
											context.getSource().getPlayer().sendMessage(Text.of("Set the rotation angle: " + gazeX + " - " + gazeY), false);
											return 1;
										})
				)))
				.then(ClientCommandManager.literal("setDefault").executes(context -> {
					maxSize = 60;
					scale = 1;
					gazeX = gazeY = 0;
					context.getSource().getPlayer().sendMessage(Text.of("Set all parameters to default"), false);
					return 1;
				}))
		);
		HudRenderCallback.EVENT.register(IconRenderer::renderer);
		recordMobKeyY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.iconr.recordmobkey_y",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_Y,
				"category.iconr.recordmob"
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (recordMobKeyY.wasPressed()) {
				recordMobPreset(client.player, client);
			}
		});
	}

	public static void recordMobPreset(PlayerEntity playerEntity, MinecraftClient client) {
		player = playerEntity;
		if (client.targetedEntity instanceof LivingEntity entity) {
			started = true;
			images = new BufferedImage[maxSize + 1];
			livingEntity = entity;
			Box box = entity.getVisibilityBoundingBox();
			double x = box.getXLength(), z = box.getZLength();
			realWidth = (int) ((x + z) / Math.sqrt(2));
			player.sendMessage(Text.of("Started to record entity " + entity.getDisplayName().getString()), false);
			return;
		}
		player.sendMessage(Text.of("You didn't target at any entities"), false);
	}

	public static BufferedImage[] images;
	public static LivingEntity livingEntity;
	public static PlayerEntity player;
	public static int size = 0, maxSize = 60, realWidth = 0, gazeX = 0, gazeY = 0;
	public static float scale = 1;
	public static boolean started = false, transparent = true;

	public static void renderer(MatrixStack matrices, float tickDelta) {
		if (!started) return;
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null) return;
		int scaledWidth = client.getWindow().getScaledWidth();
		int scaledHeight = client.getWindow().getScaledHeight();
		int sz = Math.min(scaledHeight, scaledWidth);
		if (livingEntity == null) return;
		if (size > maxSize || !started) {
			Gif.convert(images, FileHelper.filePath + "/" + livingEntity.getDisplayName().getString() + ".gif", 5, true, (int)(128 * scale), (int)(128 * scale));
			size = 0;
			started = false;
			livingEntity = null;
			player.sendMessage(Text.of("End record, render size: " + sz + "; Gif size: " + 128 * scale), false);
			return;
		}
		RenderSystem.clearColor(1, 1, 1, 0);
		Framebuffer framebuffer = new SimpleFramebuffer(scaledWidth, scaledHeight, true, MinecraftClient.IS_SYSTEM_MAC);
		framebuffer.beginWrite(true);
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(45f));
		matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(22.5f));
		matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(22.5f));
		int entitySz = (int) (120 / (Math.max(realWidth, livingEntity.getHeight()) / scale));
		RenderSystem.disableBlend();
		InventoryScreen.drawEntity(scaledWidth / 2 + realWidth / 2 - 10, sz / 2 - 10, entitySz, -gazeX, gazeY, livingEntity);
		framebuffer.endWrite();
		NativeImage image = new NativeImage(scaledWidth, scaledHeight, false);
		RenderSystem.bindTexture(framebuffer.getColorAttachment());
		image.loadFromTextureImage(0, !transparent);
		image.mirrorVertically();
		try {
			images[size] = ImageIO.read(new ByteArrayInputStream(image.getBytes())).getSubimage(0, 0, sz, sz);
			size++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
