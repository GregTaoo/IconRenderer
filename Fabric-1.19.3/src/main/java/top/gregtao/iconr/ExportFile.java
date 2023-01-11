package top.gregtao.iconr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ExportFile {
    private final File path;
    private FileWriter fileWriter;

    public ExportFile(String filePath) {
        this.path = new File("IconRenderer/" + filePath);
    }

    public ExportFile(String filePath, boolean createAtOnce) throws Exception {
        this(filePath);
        if (createAtOnce) {
            File parent = this.path.getParentFile();
            if ((!parent.exists() && !parent.mkdirs()) || (!this.path.exists() && !this.path.createNewFile())) {
                throw new Exception("Cannot create the target file");
            }
        }
    }

    public static File of(String filePath, boolean createAtOnce) throws Exception {
        return new ExportFile(filePath, createAtOnce).getFile();
    }

    public void write(String text) throws IOException {
        this.fileWriter.write(text);
    }

    public void start() throws Exception {
        File parent = this.path.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new Exception("Cannot create the target file");
        }
        this.fileWriter = new FileWriter(this.path, StandardCharsets.UTF_8);
    }

    public void finish() throws IOException {
        this.fileWriter.close();
    }

    public File getFile() {
        return this.path;
    }
}
