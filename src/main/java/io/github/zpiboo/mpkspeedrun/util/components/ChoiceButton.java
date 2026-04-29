package io.github.zpiboo.mpkspeedrun.util.components;

import io.github.kurrycat.mpkmod.gui.components.Button;
import io.github.kurrycat.mpkmod.util.Mouse;
import io.github.kurrycat.mpkmod.util.Vector2D;
import io.github.zpiboo.mpkspeedrun.util.misc.ChoiceEnum;

public class ChoiceButton<T extends Enum<T> & ChoiceEnum<T>> extends Button {
    private T current;

    public ChoiceButton(Class<T> choiceEnum, Vector2D pos, Vector2D size) {
        super("", pos, size);
        setButtonCallback(this::handleClick);

        current = choiceEnum.getEnumConstants()[0];
        updateText();
    }

    private void handleClick(Mouse.Button mouseButton) {
        switch (mouseButton) {
            case LEFT: current = current.getNext(); break;
            case RIGHT: current = current.getPrevious(); break;
        }
        updateText();
    }

    private void updateText() {
        setText(current.toString());
    }

    public T getCurrent() {
        return current;
    }
    public void setCurrent(T newValue) {
        current = newValue;
        updateText();
    }
}
