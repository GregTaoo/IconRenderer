package top.gregtao.iconr;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.gregtao.iconr.api.IExportTask;
import top.gregtao.iconr.util.IconRUtils;

import java.util.List;

public class TasksExecutor {

    public static final Logger LOGGER = LoggerFactory.getLogger("IconRenderer");


    public static void execute(List<IExportTask> tasks, ClientPlayerEntity player) {
        player.sendMessage(Text.literal("Trying to export data..."));
        LOGGER.info("Trying to export data...");

        player.sendMessage(Text.literal("Storing basic info"));
        LOGGER.info("Storing basic info");
        tasks.forEach(IExportTask::storeBasicInfo);

        player.sendMessage(Text.literal("Storing names"));
        LOGGER.info("Storing names");
        String curLang = IconRUtils.getCurrentLanguage();
        boolean isCurEnUs = curLang.equals("en_us");
        if (isCurEnUs || curLang.equals("zh_cn")) {
            tasks.forEach(task -> task.storeDisplayName(isCurEnUs));
            IconRUtils.resetLanguage(isCurEnUs ? "zh_cn" : "en_us");
            tasks.forEach(task -> task.storeDisplayName(!isCurEnUs));
        } else {
            IconRUtils.resetLanguage("zh_cn");
            tasks.forEach(task -> task.storeDisplayName(false));
            IconRUtils.resetLanguage("en_us");
            tasks.forEach(task -> task.storeDisplayName(true));
        }
        IconRUtils.resetLanguage(curLang);

        player.sendMessage(Text.literal("Rendering images"));
        LOGGER.info("Rendering images");
        tasks.forEach(IExportTask::storeImages);

        player.sendMessage(Text.literal("Exporting json files"));
        LOGGER.info("Exporting json files");
        tasks.forEach(IExportTask::export);

        player.sendMessage(Text.literal("Finished! If you want greater experience of rendering entities, even a GIF," +
                " please use another mod: AnimationRecorder, which could be found on MCMOD.CN."));
        LOGGER.info("Finished! If you want greater experience of rendering entities, even a GIF," +
                " please use another mod: AnimationRecorder, which could be found on MCMOD.CN.");
    }

}
