package fr.atlasworld.generator.file;

import java.io.File;

public class FileManager {
    public static File getWorkingDirectory() {
        return new File(System.getProperty("user.dir"));
    }
    public static File getWorkingDirectoryFile(String filename) {
        return new File(filename);
    }
    public static void deleteDirectory(File root) {
        File[] files = root.listFiles();
        if (files == null || files.length < 1) {
            root.delete();
        }

        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            }

            if (file.isDirectory()) {
                deleteDirectory(file);
            }
        }
    }
}
