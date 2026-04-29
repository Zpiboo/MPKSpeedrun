package io.github.zpiboo.mpkspeedrun.util.api_compat;

import io.github.kurrycat.mpkmod.util.Vector3D;

public class Vector3DUtil {
    public static double xzOffset(Vector3D offset) {
        if (offset.getX() > 0) {
            if (offset.getZ() > 0)  // both are positive
                return Math.min(offset.getX(), offset.getZ());

            return offset.getZ();  // only z is negative
        }
        if (offset.getZ() > 0) {
            return offset.getX();  // only x is negative
        }

        return offset.lengthXZ();  // both are negative
    }
}
