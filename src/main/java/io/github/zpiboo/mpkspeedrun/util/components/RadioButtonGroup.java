package io.github.zpiboo.mpkspeedrun.util.components;

import java.util.ArrayList;
import java.util.List;

public class RadioButtonGroup {
    private List<RadioButton> buttons = new ArrayList<>();
    private RadioButton selected;

    public List<RadioButton> getButtons() {
        return buttons;
    }
    public void addButton(RadioButton button) {
        buttons.add(button);
    }

    public RadioButton getSelected() {
        return selected;
    }
    public void selectButton(RadioButton button) {
        for (RadioButton btn : buttons) {
            btn.setChecked(false);
        }
        button.setChecked(true);
        selected = button;
    }
    public void unselectButton() {
        selected.setChecked(false);
        selected = null;
    }
}
