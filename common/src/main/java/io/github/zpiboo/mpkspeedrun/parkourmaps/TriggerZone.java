package io.github.zpiboo.mpkspeedrun.parkourmaps;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector3D;
import io.github.zpiboo.mpkspeedrun.MPKSpeedrun;
import io.github.zpiboo.mpkspeedrun.util.BB3D;
import org.json.JSONObject;

import java.util.Arrays;

@InfoString.DataClass
public class TriggerZone {
    private BoundingBox3D box;
    private TriggerMode mode;

    public boolean shouldTrigger = false;
    private int lastAirtime = 0;
    private double lastSubtick = 0.0D;

    @SuppressWarnings("unused") public static final TriggerZone ZERO = new TriggerZone();

    public enum TriggerMode {
        POS_ENTER,
        BOX_ENTER,
        POS_EXIT,
        BOX_EXIT;

        public TriggerMode getNext() {
            return values()[(Arrays.asList(values()).indexOf(this) + 1) % values().length];
        }
    }

    public TriggerZone(BoundingBox3D box, TriggerMode mode) {
        this.box = new BoundingBox3D(box.getMin(), box.getMax());
        this.mode = mode;
    }
    public TriggerZone() {
        this(BoundingBox3D.ZERO, TriggerMode.POS_ENTER);
    }

    @InfoString.Getter
    public BoundingBox3D getBox() {
        return box;
    }
    @SuppressWarnings("unused")
    public void setBox(BoundingBox3D box) {
        this.box = box;
    }

    @InfoString.Getter
    public TriggerMode getMode() {
        return mode;
    }
    public void setMode(TriggerMode mode) {
        this.mode = mode;
    }

    @InfoString.Getter
    public int getAirtime() {
        return lastAirtime;
    }
    public void setAirtime(int airtime) {
        lastAirtime = airtime;
    }

    public double getSubtick() {
        return lastSubtick;
    }
    public void setSubtick(double subtick) {
        lastSubtick = subtick;
    }

    private double calculateSubtick(Player player) {
        switch (mode) {
            case POS_ENTER:
                return BB3D.slabMethod(
                        player.getLastPos(),
                        player.getPos(),
                        getBox()
                );
            case POS_EXIT:
                return 1.0D - BB3D.slabMethod(
                        player.getPos(),
                        player.getLastPos(),
                        getBox()
                );

            default: return 0.0D;
        }
    }

    private boolean shouldTrigger(Player player) {
        final Vector3D currPos = player.getPos();
        final Vector3D lastPos = player.getLastPos();
        final BoundingBox3D currBb = player.getBoundingBox();
        final BoundingBox3D lastBb = player.getLastBoundingBox();

        switch (mode) {
            case POS_ENTER:
                return BB3D.contains(box, currPos) && !BB3D.contains(box, lastPos);

            case BOX_ENTER:
                return BB3D.intersect(box, currBb) && !BB3D.intersect(box, lastBb);

            case POS_EXIT:
                return BB3D.contains(box, lastPos) && !BB3D.contains(box, currPos);

            case BOX_EXIT:
                return BB3D.intersect(box, lastBb) && !BB3D.intersect(box, currBb);

            default: return false;
        }
    }

    public boolean tick(Player player) {
        shouldTrigger = shouldTrigger(player);

        if (shouldTrigger) {
            setAirtime(player.getAirtime());
            setSubtick(calculateSubtick(player));
        }

        return shouldTrigger;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("mode", mode.toString())

                .put("minx", box.minX())
                .put("miny", box.minY())
                .put("minz", box.minZ())

                .put("maxx", box.maxX())
                .put("maxy", box.maxY())
                .put("maxz", box.maxZ());
    }
    public static TriggerZone fromJson(JSONObject boxJson) {
        String modeString = boxJson.optString("mode", "POS_ENTER");
        TriggerMode mode;
        try {
            mode = TriggerMode.valueOf(modeString);
        } catch (IllegalArgumentException e) {
            mode = TriggerMode.POS_ENTER;
            MPKSpeedrun.LOGGER.warn("Trigger mode not found: {}", modeString);
        }

        double minX = boxJson.optDouble("minx", 0);
        double minY = boxJson.optDouble("miny", 0);
        double minZ = boxJson.optDouble("minz", 0);
        double maxX = boxJson.optDouble("maxx", 0);
        double maxY = boxJson.optDouble("maxy", 0);
        double maxZ = boxJson.optDouble("maxz", 0);

        BoundingBox3D box = new BoundingBox3D(
                new Vector3D(minX, minY, minZ),
                new Vector3D(maxX, maxY, maxZ)
        );

        return new TriggerZone(box, mode);
    }
}
