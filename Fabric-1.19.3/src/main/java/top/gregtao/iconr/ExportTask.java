package top.gregtao.iconr;

public interface ExportTask {
    void storeBasicInfo();

    void storeDisplayName(boolean isEnglish);

    void storeImages();

    void export();

}
