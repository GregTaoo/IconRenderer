package top.gregtao.iconr;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.text.Text;
import top.gregtao.iconr.export.ExportTask;

import java.util.List;

public class TasksExecutor {
    public static void execute(List<ExportTask> tasks, ClientPlayerEntity player) {
        player.sendMessage(Text.literal("Trying to export data..."));

        player.sendMessage(Text.literal("Storing basic info"));
        tasks.forEach(ExportTask::storeBasicInfo);

        player.sendMessage(Text.literal("Storing names"));
        String curLang = MinecraftClient.getInstance().options.language;
        boolean isCurEnUs = curLang.equals("en_us");
        if (isCurEnUs || curLang.equals("zh_cn")) {
            tasks.forEach(task -> task.storeDisplayName(isCurEnUs));
            resetLanguage(isCurEnUs ? "zh_cn" : "en_us");
            tasks.forEach(task -> task.storeDisplayName(!isCurEnUs));
        } else {
            resetLanguage("zh_cn");
            tasks.forEach(task -> task.storeDisplayName(false));
            resetLanguage("en_us");
            tasks.forEach(task -> task.storeDisplayName(true));
        }
        resetLanguage(curLang);

        player.sendMessage(Text.literal("Rendering images"));
        tasks.forEach(ExportTask::storeImages);

        player.sendMessage(Text.literal("Exporting json files"));
        tasks.forEach(ExportTask::export);

        player.sendMessage(Text.literal("Finished! If you want greater experience of rendering entities, even a GIF," +
                " please use another mod: AnimationRecorder, which could be found on MCMOD.CN."));
    }

    private static void resetLanguage(String lang) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.options.language.equals(lang)) {
            LanguageDefinition langDefinition = new LanguageDefinition(lang, "", "", false);
            client.getLanguageManager().setLanguage(langDefinition);
            client.options.language = langDefinition.getCode();
            client.reloadResources();
            client.options.write();
            client.getLanguageManager().reload(client.getResourceManager());
        }
    }
}
