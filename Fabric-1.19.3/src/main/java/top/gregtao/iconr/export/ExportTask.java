package top.gregtao.iconr.export;

public interface ExportTask {
    void storeBasicInfo();

    void storeDisplayName(boolean isEnglish);

    void storeImages();

    void export();

}
