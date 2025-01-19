package io.github.zpiboo.mpkspeedrun.parkourmaps;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector3D;
import io.github.zpiboo.mpkspeedrun.util.BB3D;

public class TriggerZone {
    private BoundingBox3D zone;
    private TriggerMode mode;

    public enum TriggerMode {
        POS_ENTER,
        BOX_ENTER,
        POS_EXIT,
        BOX_EXIT
    }

    public TriggerZone(BoundingBox3D zone, TriggerMode mode) {
        this.zone = zone;
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
}
