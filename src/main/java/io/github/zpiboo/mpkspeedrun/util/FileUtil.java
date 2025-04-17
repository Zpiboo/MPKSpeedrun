package io.github.zpiboo.mpkspeedrun.util;

import java.io.File;

public class FileUtil {
    public static final String MAP_FOLDER_PATH = "config/mpk/maps";
    public static File MAP_FOLDER;

    public static void init() {
        MAP_FOLDER = new File(MAP_FOLDER_PATH);
        MAP_FOLDER.mkdir();
    }
}
