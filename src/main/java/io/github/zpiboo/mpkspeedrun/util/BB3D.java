package io.github.zpiboo.mpkspeedrun.util;

import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector3D;

public class BB3D {
    public static boolean contains(BoundingBox3D bb, Vector3D vec) {
        return
                bb.minX() <= vec.getX() && vec.getX() <= bb.maxX() &&
                bb.minY() <= vec.getY() && vec.getY() <= bb.maxY() &&
                bb.minZ() <= vec.getZ() && vec.getZ() <= bb.maxZ();
    }

    public static boolean intersect(BoundingBox3D bb1, BoundingBox3D bb2) {
        return
                bb1.maxX() >= bb2.minX() &&
                bb1.minX() <= bb2.maxX() &&
                bb1.maxY() >= bb2.minY() &&
                bb1.minY() <= bb2.maxY() &&
                bb1.maxZ() >= bb2.minZ() &&
                bb1.minZ() <= bb2.maxZ();
    }
}
