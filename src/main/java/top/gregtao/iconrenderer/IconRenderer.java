package top.gregtao.iconrenderer;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class IconRenderer implements ModInitializer {
	public static Logger logger = LogManager.getLogger("IconRenderer");

	@Override
	public void onInitialize() {
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
	}
}
