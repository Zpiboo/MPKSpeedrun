package io.github.zpiboo.mpkspeedrun.util.api_compat;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector3D;

public class PlayerUtil {
    public static Vector3D getLandingPos(Player player) {
        return new Vector3D(
                player.getLastPos().getX(),
                player.getPos().getY(),
                player.getLastPos().getZ()
        );
    }
    public static BoundingBox3D getLandingBB(Player player) {
        return getBBForPos(getLandingPos(player));
    }

    public static BoundingBox3D getBBForPos(Vector3D pos) {
        return new BoundingBox3D(
                new Vector3D(
                        pos.getX() - 0.3F,
                        pos.getY(),
                        pos.getZ() - 0.3F
                ),
                new Vector3D(
                        pos.getX() + 0.3F,
                        pos.getY() + 1.8F,
                        pos.getZ() + 0.3F
                )
        );
    }
}
