package io.github.zpiboo.mpkspeedrun.parkourmaps;

import java.util.Arrays;

import org.json.JSONObject;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector3D;
import io.github.zpiboo.mpkspeedrun.MPKSpeedrun;
import io.github.zpiboo.mpkspeedrun.util.BB3D;

public class TriggerZone {
    private BoundingBox3D zone;
    private TriggerMode mode;

    public static TriggerZone ZERO = new TriggerZone();

    public enum TriggerMode {
        POS_ENTER,
        BOX_ENTER,
        POS_EXIT,
        BOX_EXIT;

        public TriggerMode getNext() {
            return values()[(Arrays.asList(values()).indexOf(this) + 1) % values().length];
        }
    }

    public TriggerZone(BoundingBox3D zone, TriggerMode mode) {
        this.zone = new BoundingBox3D(zone.getMin(), zone.getMax());
        this.mode = mode;
    }
    public TriggerZone() {
        this(BoundingBox3D.ZERO, TriggerMode.POS_ENTER);
    }

    public BoundingBox3D getZone() {
        return zone;
    }
    public void setZone(BoundingBox3D zone) {
        this.zone = zone;
    }

    public TriggerMode getMode() {
        return mode;
    }
    public void setMode(TriggerMode mode) {
        this.mode = mode;
    }

    public boolean shouldTrigger(Player player) {
        Vector3D currPos = player.getPos();
        Vector3D lastPos = player.getLastPos();
        BoundingBox3D currBb = player.getBoundingBox();
        BoundingBox3D lastBb = player.getLastBoundingBox();

        switch (mode) {
            case POS_ENTER:
                return BB3D.contains(zone, currPos) && !BB3D.contains(zone, lastPos);

            case BOX_ENTER:
                return BB3D.intersect(zone, currBb) && !BB3D.intersect(zone, lastBb);

            case POS_EXIT:
                return BB3D.contains(zone, lastPos) && !BB3D.contains(zone, currPos);

            case BOX_EXIT:
                return BB3D.intersect(zone, lastBb) && !BB3D.intersect(zone, currBb);

            default: return false;
        }
    }

    public JSONObject toJson() {
        JSONObject zoneJson = new JSONObject()
            .put("mode", mode.toString())

            .put("minx", zone.minX())
            .put("miny", zone.minY())
            .put("minz", zone.minZ())

            .put("maxx", zone.maxX())
            .put("maxy", zone.maxY())
            .put("maxz", zone.maxZ());

        return zoneJson;
    }
    public static TriggerZone fromJson(JSONObject zoneJson) {
        String modeString = zoneJson.optString("mode", "POS_ENTER");
        TriggerMode mode;
        try {
            mode = TriggerMode.valueOf(modeString);
        } catch (IllegalArgumentException e) {
            mode = TriggerMode.POS_ENTER;
            MPKSpeedrun.LOGGER.warn("Trigger mode not found: " + modeString);
        }

        double minX = zoneJson.optDouble("minx", 0);
        double minY = zoneJson.optDouble("miny", 0);
        double minZ = zoneJson.optDouble("minz", 0);
        double maxX = zoneJson.optDouble("maxx", 0);
        double maxY = zoneJson.optDouble("maxy", 0);
        double maxZ = zoneJson.optDouble("maxz", 0);

        BoundingBox3D zone = new BoundingBox3D(
                new Vector3D(minX, minY, minZ),
                new Vector3D(maxX, maxY, maxZ)
        );

        return new TriggerZone(zone, mode);
    }
}
