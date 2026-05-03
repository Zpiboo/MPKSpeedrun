package io.github.zpiboo.mpkspeedrun.util.api_compat;

import io.github.kurrycat.mpkmod.util.BoundingBox2D;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector3D;

public class BoundingBox3DUtil {
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

    public static double xzSlabMethod(Vector3D start, Vector3D end, BoundingBox3D box) {
        // Start pos is above or below
        if (box.minX() < start.getX() && start.getX() < box.maxX() &&
                box.minZ() < start.getZ() && start.getZ() < box.maxZ() &&
                (start.getY() < box.minY() || box.maxY() < start.getY())) {
            return 0.0;
        }

        double[] startArr = new double[2];
        double[] endArr = new double[2];
        double[] direction = new double[2];
        double[] boxMin = new double[2];
        double[] boxMax = new double[2];

        startArr[0] = start.getX();
        startArr[1] = start.getZ();
        endArr[0] = end.getX();
        endArr[1] = end.getZ();

        boxMin[0] = box.minX();
        boxMin[1] = box.minZ();
        boxMax[0] = box.maxX();
        boxMax[1] = box.maxZ();

        for (int i = 0; i < 2; i++) {
            direction[i] = endArr[i] - startArr[i];
        }

        double tMin = 0.0;
        double tMax = 1.0;

        for (int i = 0; i < 2; i++) {
            double boxMinCoord = boxMin[i];
            double boxMaxCoord = boxMax[i];

            if (Math.abs(direction[i]) == 0) {
                // Line is parallel to edge
                if (startArr[i] < boxMinCoord || startArr[i] > boxMaxCoord) {
                    return 1.0;
                }
            } else {
                double t1 = (boxMinCoord - startArr[i]) / direction[i];
                double t2 = (boxMaxCoord - startArr[i]) / direction[i];

                if (t1 > t2) {
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }

                tMin = Math.max(tMin, t1);
                tMax = Math.min(tMax, t2);

                if (tMin > tMax) { return 1.0; }
            }
        }

        if (tMax < 0 || tMin > 1) {
            return 1.0;
        }

        return Math.max(tMin, 0.0);
    }
}
