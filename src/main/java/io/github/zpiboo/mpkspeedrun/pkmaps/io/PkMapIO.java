package io.github.zpiboo.mpkspeedrun.pkmaps.io;

import io.github.zpiboo.mpkspeedrun.MPKSpeedrun;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.PkMap;
import io.github.zpiboo.mpkspeedrun.util.FileUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

public class PkMapIO {
    public static final File MAP_FOLDER = FileUtil.getConfigDir("maps");

    public static void save(PkMap map) {
        String oldFileName = map.getFileName();
        Path oldPath = null;

        String newFileName = makeFileName(map.getName(), map.getUUID());
        Path newPath = getFilePath(newFileName);

        boolean hasOldFile = oldFileName != null;
        boolean isRename = hasOldFile && !Objects.equals(oldFileName, newFileName);
        if (hasOldFile) oldPath = getFilePath(oldFileName);

        Path tmpPath = null;
        try {
            tmpPath = Files.createTempFile(newPath.getParent(), newFileName, ".tmp");

            Files.write(tmpPath, map.toJson().toString(2).getBytes(StandardCharsets.UTF_8));

            try {
                Files.move(tmpPath, newPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                throw new IOException("Failed to move temporary file to location: " + newPath, e);
            }

            if (isRename) {
                try {
                    Files.deleteIfExists(oldPath);
                } catch (IOException e) {
                    MPKSpeedrun.LOGGER.error("Failed to delete old file: " + oldPath + " - " + e.getMessage(), e);
                }
            }

            map.setFileName(newFileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save map: " + map.getName() + " (" + map.getUUID() + ")", e);
        } finally {
            if (tmpPath != null && Files.exists(tmpPath)) {
                try {
                    Files.delete(tmpPath);
                } catch (IOException e) {
                    System.err.println("Failed to delete temporary file: " + tmpPath);
                }
            }
        }
    }

    public static PkMap load(File mapFile) {
        try {
            PkMap loadedMap = PkMap.fromJson(new JSONObject(
                    new String(Files.readAllBytes(mapFile.toPath()), StandardCharsets.UTF_8)
            ));

            String fileName = mapFile.getName();
            if (fileName.endsWith(".json"))
                fileName = fileName.substring(0, fileName.length() - 5);

            loadedMap.setFileName(fileName);
            return loadedMap;
        } catch (IOException e) {
            MPKSpeedrun.LOGGER.error("Failed to read map file: " + mapFile.getPath() + " - " + e.getMessage(), e);
        } catch (Exception e) {
            MPKSpeedrun.LOGGER.error("Invalid JSON format in map file: " + mapFile.getPath() + " - " + e.getMessage(), e);
        }

        return null;
    }

    private static String makeFileName(String mapName, UUID uuid) {
        String sanitizedName = mapName.toLowerCase()
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", "_");

        if (sanitizedName.length() > 50)
            sanitizedName = sanitizedName.substring(0, 50);

        return sanitizedName + "__" + uuid.toString().substring(0, 8);
    }

    public static Path getFilePath(PkMap map) {
        return getFilePath(map.getFileName());
    }

    public static Path getFilePath(String fileName) {
        return Paths.get(PkMapIO.MAP_FOLDER.toString(), fileName + ".json");
    }
}
