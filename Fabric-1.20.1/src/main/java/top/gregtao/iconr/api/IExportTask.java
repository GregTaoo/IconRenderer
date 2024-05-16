package top.gregtao.iconr.api;

public interface IExportTask {
    void storeBasicInfo();

    default void storeDisplayName(boolean isEnglish) {}

    default void storeImages() {}

    void export();

}
