package io.github.zpiboo.mpkspeedrun.pkmaps.core;

import io.github.kurrycat.mpkmod.gui.components.Anchor;
import io.github.kurrycat.mpkmod.gui.components.ComponentHolder;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.Vector2D;
import io.github.zpiboo.mpkspeedrun.MPKSpeedrun;
import io.github.zpiboo.mpkspeedrun.pkmaps.gui.editor.ConfigPane;
import io.github.zpiboo.mpkspeedrun.pkmaps.gui.screen.PkMapsGUIScreen;
import io.github.zpiboo.mpkspeedrun.pkmaps.io.PkMapIO;
import org.json.JSONObject;

import java.util.UUID;

@InfoString.DataClass
public class PkMap implements Comparable<PkMap> {
    public static final String DEFAULT_NAME = "New Map";

    private String fileName;
    private final UUID uuid;
    private String name;

    private StartZone start;
    private FinishZone finish;

    private int startTime = 1;

    public PkMap(String name, StartZone start, FinishZone finish, UUID uuid) {
        if (uuid == null)
            this.uuid = UUID.randomUUID();
        else
            this.uuid = uuid;

        setName(name);
        setStart(start);
        setFinish(finish);
    }

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UUID getUUID() {
        return uuid;
    }

    @InfoString.Getter
    public String getName() { return name; }

    public void setName(String name) {
        this.name = name == null
                ? DEFAULT_NAME
                : name;
    }

    @InfoString.Getter
    public TriggerZone getStart() {
        return start;
    }
    public void setStart(StartZone start) {
        this.start = start;
    }

    @InfoString.Getter
    public TriggerZone getFinish() {
        return finish;
    }
    public void setFinish(FinishZone finish) {
        this.finish = finish;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("uuid", uuid)
                .put("name", name)
                .put("start", start != null ? start.toJson() : new JSONObject())
                .put("finish", finish != null ? finish.toJson() : new JSONObject())
                .put("start_time", startTime);
    }
    public static PkMap fromJson(JSONObject mapJson) {
        String name = mapJson.optString("name", DEFAULT_NAME);

        UUID uuid;
        try {
            uuid = UUID.fromString(mapJson.optString("uuid"));
        } catch (IllegalArgumentException | NullPointerException e) {
            uuid = UUID.randomUUID();
            MPKSpeedrun.LOGGER.warn("UUID is either absent or malformatted in parkour map '" + name + "', generated a new one - " + e.getMessage(), e);
        }

        StartZone start = StartZone.fromJson( mapJson.optJSONObject("start") );
        FinishZone finish = FinishZone.fromJson( mapJson.optJSONObject("finish") );
        int startTime = mapJson.optInt("start_time", 1);

        final PkMap loadedMap = new PkMap(name, start, finish, uuid);
        loadedMap.setStartTime(startTime);

        return loadedMap;
    }

    public int getStartTime() {
        return startTime;
    }
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    @Override
    public int compareTo(PkMap other) {
        int nameCmp = this.getName().compareTo( other.getName() );
        return nameCmp == 0
                ? this.uuid.compareTo(other.uuid)
                : nameCmp;
    }

    public boolean equals(PkMap other) {
        return this.uuid.equals(other.uuid);
    }

    private ConfigPane createConfigPane(ComponentHolder parent) {
        ConfigPane newPane = new ConfigPane(this, Vector2D.ZERO, new Vector2D(3 / 7D, 0));
        parent.passPositionTo(newPane, ComponentHolder.PERCENT.X, Anchor.CENTER);

        return newPane;
    }

    public void openConfigPane(PkMapsGUIScreen parentScreen) {
        parentScreen.openPane(createConfigPane(parentScreen));
    }
}
