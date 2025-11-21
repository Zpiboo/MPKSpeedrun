package io.github.zpiboo.mpkspeedrun.pkmaps.gui.editor;

import io.github.kurrycat.mpkmod.gui.components.Anchor;
import io.github.kurrycat.mpkmod.gui.components.Div;
import io.github.kurrycat.mpkmod.gui.components.InputField;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.MathUtil;
import io.github.kurrycat.mpkmod.util.Vector2D;
import io.github.kurrycat.mpkmod.util.Vector3D;
import io.github.zpiboo.mpkspeedrun.util.misc.Axis;

import java.util.EnumMap;

public class BB3DEditor extends Div {
    private final double ROW_HEIGHT = 12;

    private final BoundingBox3D bb;

    private final EnumMap<Axis, InputField> minFields = new EnumMap<>(Axis.class);
    private final EnumMap<Axis, InputField> maxFields = new EnumMap<>(Axis.class);

    public BB3DEditor(BoundingBox3D bb, Vector2D pos, double width) {
        this.bb = bb;

        for (Axis axis : Axis.values()) {
            int idx = axis.ordinal();

            String minLabel = "Min " + axis + ": ";
            String minValue = String.valueOf(
                    getVectorCoordByAxis(bb.getMin(), axis)
            );

            InputField minField = new InputField(minValue, new Vector2D(0, ROW_HEIGHT*idx), 0.48D, true).setName(minLabel);
            minFields.put(axis, minField);
            addChild(minField, PERCENT.X, Anchor.TOP_LEFT);


            String maxLabel = "Max " + axis + ": ";
            String maxValue = String.valueOf(
                    getVectorCoordByAxis(bb.getMax(), axis)
            );

            InputField maxField = new InputField(maxValue, new Vector2D(0, ROW_HEIGHT*idx), 0.48D, true).setName(maxLabel);
            maxFields.put(axis, maxField);
            addChild(maxField, PERCENT.X, Anchor.TOP_RIGHT);
        }

        setPos(pos);
        setSize(new Vector2D(width, 3*ROW_HEIGHT));
    }

    private static Double getVectorCoordByAxis(Vector3D vector3D, Axis axis) {
        switch (axis) {
            case X: return vector3D.getX();
            case Y: return vector3D.getY();
            case Z: return vector3D.getZ();
        }
        throw new IllegalStateException("Unexpected axis: " + axis);
    }
    private static void setVectorCoordByAxis(Vector3D vector3D, Axis axis, double value) {
        switch (axis) {
            case X: vector3D.setX(value); break;
            case Y: vector3D.setY(value); break;
            case Z: vector3D.setZ(value); break;
        }
    }
    private static double getFieldValue(InputField field) {
        return MathUtil.parseDouble(field.content, 0.0D);
    }

    public void save() {
        for (Axis axis : Axis.values()) {
            setVectorCoordByAxis(
                    bb.getMin(), axis,
                    getFieldValue(minFields.get(axis))
            );
            setVectorCoordByAxis(
                    bb.getMax(), axis,
                    getFieldValue(maxFields.get(axis))
            );
        }
    }
}
