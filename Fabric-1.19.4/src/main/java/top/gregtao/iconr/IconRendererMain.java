package top.gregtao.iconr;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import top.gregtao.iconr.api.IExportTask;
import top.gregtao.iconr.export.ExportEntityTask;
import top.gregtao.iconr.export.ExportItemTask;
import top.gregtao.iconr.export.ExportRecipeTask;
import top.gregtao.iconr.util.ItemGroupHelper;

import java.util.ArrayList;
import java.util.List;

public class IconRendererMain implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("iconr").then(
                        ClientCommandManager.literal("single").then(
                                ClientCommandManager.argument("namespace", StringArgumentType.word()).executes(context -> {
                                    ClientPlayerEntity player = context.getSource().getPlayer();
                                    String modId = context.getArgument("namespace", String.class);
                                    player.sendMessage(Text.literal("Trying to export mod '" + modId + "'..."));
                                    try {
                                        TasksExecutor.execute(List.of(new ExportItemTask(modId), new ExportEntityTask(modId), new ExportRecipeTask(player, modId)), player);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                    return 0;
                                })
                        )
                ).then(
                        ClientCommandManager.literal("all").executes(context -> {
                            try {
                                ClientPlayerEntity player = context.getSource().getPlayer();
                                player.sendMessage(Text.literal("Trying to export all mods..."));
                                List<ModContainerImpl> mods = FabricLoaderImpl.INSTANCE.getModsInternal();
                                List<IExportTask> tasks = new ArrayList<>();
                                mods.forEach(mod -> {
                                    try {
                                        String modId = mod.getMetadata().getId();
                                        tasks.add(new ExportItemTask(modId));
                                        tasks.add(new ExportEntityTask(modId));
                                        tasks.add(new ExportRecipeTask(player, modId));
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                TasksExecutor.execute(tasks, player);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            return 0;
                        })
                ).then(
                        ClientCommandManager.literal("reload").executes(context -> {
                            ItemGroupHelper.INSTANCE.load();
                            context.getSource().getPlayer().sendMessage(Text.literal("Reloaded item groups."));
                            return 0;
                        })
                )
        ));

        TasksExecutor.LOGGER.info("Loaded IconRenderer.");
    }
}