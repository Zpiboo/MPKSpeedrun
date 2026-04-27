package io.github.zpiboo.mpkspeedrun.util;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileUtil {
    private static final String CONFIG_FOLDER = "config/mpk";
    private static final Map<String, File> configDirs = new HashMap<>();

    public static void registerConfigDir(String name) {
        assertCorrectDirName(name);
        File dir = Paths.get(CONFIG_FOLDER, name).toFile();

        ensureDirExists(dir);
        configDirs.put(name, dir);
    }

    private static void ensureDirExists(File dir) {
        // noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
    }

    public static File getConfigDir(String name) {
        assertCorrectDirName(name);
        return configDirs.get(name);
    }

    private static void assertCorrectDirName(String name) {
        if (name.contains("/"))
            throw new IllegalArgumentException("Directory names cannot contain a '/' ('" + name + "').");
    }
}
