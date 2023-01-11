package top.gregtao.iconr;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class IconRendererMain implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("iconr").then(
                        ClientCommandManager.literal("single").then(
                                ClientCommandManager.argument("namespace", StringArgumentType.word()).executes(context -> {
                                    String modId = context.getArgument("namespace", String.class);
                                    context.getSource().getPlayer().sendMessage(Text.literal("Trying to export mod '" + modId + "'..."));
                                    try {
                                        TasksExecutor.execute(List.of(new ExportItemTask(modId), new ExportEntityTask(modId)), context.getSource().getPlayer());
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
                                List<ExportTask> tasks = new ArrayList<>();
                                mods.forEach(mod -> {
                                    try {
                                        tasks.add(new ExportItemTask(mod.getMetadata().getId()));
                                        tasks.add(new ExportEntityTask(mod.getMetadata().getId()));
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
                )
        ));
    }
}