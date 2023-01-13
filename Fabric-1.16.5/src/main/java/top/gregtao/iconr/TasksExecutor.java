package top.gregtao.iconr;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.gregtao.iconr.api.IExportTask;
import top.gregtao.iconr.util.IconRUtils;

import java.util.List;

public class TasksExecutor {

    public static final Logger LOGGER = LogManager.getLogger("IconRenderer");


    public static void execute(List<IExportTask> tasks, ClientPlayerEntity player) {
        player.sendMessage(Text.of("Trying to export data..."), false);
        LOGGER.info("Trying to export data...");

        player.sendMessage(Text.of("Storing basic info"), false);
        LOGGER.info("Storing basic info");
        tasks.forEach(IExportTask::storeBasicInfo);

        player.sendMessage(Text.of("Storing names"), false);
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

        player.sendMessage(Text.of("Rendering images"), false);
        LOGGER.info("Rendering images");
        tasks.forEach(IExportTask::storeImages);

        player.sendMessage(Text.of("Exporting json files"), false);
        LOGGER.info("Exporting json files");
        tasks.forEach(IExportTask::export);

        player.sendMessage(Text.of("Finished! If you want greater experience of rendering entities, even a GIF," +
                " please use another mod: AnimationRecorder, which could be found on MCMOD.CN."), false);
        LOGGER.info("Finished! If you want greater experience of rendering entities, even a GIF," +
                " please use another mod: AnimationRecorder, which could be found on MCMOD.CN.");
    }

}
