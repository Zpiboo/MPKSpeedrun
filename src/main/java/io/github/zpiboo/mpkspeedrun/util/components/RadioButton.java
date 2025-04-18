package io.github.zpiboo.mpkspeedrun.util.components;

import io.github.kurrycat.mpkmod.gui.components.CheckButton;
import io.github.kurrycat.mpkmod.util.Vector2D;

public class RadioButton extends CheckButton {
    private final RadioButtonGroup group;

    public RadioButton(Vector2D pos, RadioButtonGroup group, boolean checked, CheckButtonCallback callback) {
        super(pos, checked, null);

        this.group = group;
        group.addButton(this);

        checkButtonCallback = isChecked -> {
            if (isChecked)
                getGroup().selectButton(this);
            else
                getGroup().unselectButton();
            callback.apply(isChecked);
        };
    }
    public RadioButton(Vector2D pos, RadioButtonGroup group) {
        this(pos, group, false, isChecked -> {});
    }

    public RadioButtonGroup getGroup() {
        return group;
    }
}
