package top.gregtao.iconr.api;

public interface IExportTask {
    void storeBasicInfo();

    void storeDisplayName(boolean isEnglish);

    void storeImages();

    void export();

}
