package io.github.zpiboo.mpkspeedrun.parkourmaps;

import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.zpiboo.mpkspeedrun.MPKSpeedrun;
import io.github.zpiboo.mpkspeedrun.util.FileUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@InfoString.DataClass
public class PkMap implements Comparable<PkMap> {
    private String name;

    private TriggerZone start;
    private TriggerZone finish;

    private int startTime = 1;

    public PkMap(String name, TriggerZone start, TriggerZone finish) {
        setName(name);
        setStart(start);
        setFinish(finish);
    }

    @InfoString.Getter
    public BoundingBox3D getStartBox() { return start != null ? start.getZone() : null; }
    @InfoString.Getter
    public BoundingBox3D getFinishBox() { return finish != null ? finish.getZone() : null; }

    @InfoString.Getter
    public String getName() { return name; }

    public void setName(String name) {
        this.name = name == null
                ? getDefaultName()
                : name;
    }

    public static String getDefaultName() {
        File[] files = FileUtil.MAP_FOLDER.listFiles((dir, filename) -> filename.endsWith(".json"));
        int num = files == null ? 1 : files.length + 1;
        return "Map " + num;
    }

    public TriggerZone getStart() {
        return start;
    }
    public void setStart(TriggerZone start) {
        this.start = start;
    }

    public TriggerZone getFinish() {
        return finish;
    }
    public void setFinish(TriggerZone finish) {
        this.finish = finish;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("start", start != null ? start.toJson() : new JSONObject())
                .put("finish", finish != null ? finish.toJson() : new JSONObject())
                .put("start_time", startTime);
    }
    public static PkMap fromJson(JSONObject mapJson) {
        String name = getDefaultName();

        TriggerZone start = TriggerZone.fromJson( mapJson.optJSONObject("start") );
        TriggerZone finish = TriggerZone.fromJson( mapJson.optJSONObject("finish") );
        int startTime = mapJson.optInt("start_time", 1);

        final PkMap loadedMap = new PkMap(name, start, finish);
        loadedMap.setStartTime(startTime);
        return loadedMap;
    }

    public Path getFilePath() {
        return Paths.get(FileUtil.MAP_FOLDER_PATH, name + ".json");
    }
    public static Path getFilePath(String mapName) {
        return Paths.get(FileUtil.MAP_FOLDER_PATH, mapName + ".json");
    }

    public void save() {
        Path filePath = getFilePath();
        try {
            Files.write(filePath, toJson().toString(2).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            MPKSpeedrun.LOGGER.error("Failed to create file: {} - {}", filePath, e.getMessage(), e);
        }
    }
    public static PkMap load(String mapName) {
        Path filePath = getFilePath(mapName);

        if (!Files.exists(filePath)) {
            MPKSpeedrun.LOGGER.warn("Couldn't find map file: {}", filePath);
            return null;
        }

        try {
            final PkMap pkMap = fromJson(new JSONObject(
                new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8)
            ));
            pkMap.setName(mapName);

            return pkMap;
        } catch (IOException e) {
            MPKSpeedrun.LOGGER.error("Failed to read map file: {} - {}", filePath, e.getMessage(), e);
        } catch (Exception e) {
            MPKSpeedrun.LOGGER.error("Invalid JSON format in map file: {} - {}", filePath, e.getMessage(), e);
        }

        return null;
    }

    public int getStartTime() {
        return startTime;
    }
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    @Override
    public int compareTo(PkMap other) {
        return this.getName().compareTo( other.getName() );
    }
}
