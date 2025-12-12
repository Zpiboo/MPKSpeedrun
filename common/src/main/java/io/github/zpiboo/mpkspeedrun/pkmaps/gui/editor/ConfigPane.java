package io.github.zpiboo.mpkspeedrun.pkmaps.gui.editor;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Renderer2D;
import io.github.kurrycat.mpkmod.gui.components.Anchor;
import io.github.kurrycat.mpkmod.gui.components.Button;
import io.github.kurrycat.mpkmod.gui.components.InputField;
import io.github.kurrycat.mpkmod.gui.components.Pane;
import io.github.kurrycat.mpkmod.util.Mouse;
import io.github.kurrycat.mpkmod.util.Vector2D;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.PkMap;
import io.github.zpiboo.mpkspeedrun.pkmaps.gui.screen.PkMapsGUIScreen;
import io.github.zpiboo.mpkspeedrun.pkmaps.io.PkMapIO;

import java.awt.*;

public class ConfigPane extends Pane<PkMapsGUIScreen> {
    private final PkMap map;
    public final InputField nameField;

    private final TriggerZoneEditor startEditor;
    private final TriggerZoneEditor finishEditor;

    private final Button cancelButton;

    public ConfigPane(PkMap map, Vector2D pos, Vector2D size) {
        super(pos, size);
        this.map = map;
        backgroundColor = new Color(50, 50, 50);

        double heightUpToHere = 12;
        nameField = new InputField(map.getName(), new Vector2D(0, heightUpToHere), 0.95D)
                .setName("Map Name: ");
        addChild(nameField, PERCENT.SIZE_X, Anchor.TOP_CENTER);

        heightUpToHere += nameField.getDisplayedSize().getY() + 10;
        startEditor = new TriggerZoneEditor(map.getStart(), "Start Box:", new Vector2D(0, heightUpToHere), 0.95);
        addChild(startEditor, PERCENT.SIZE_X, Anchor.TOP_CENTER);

        heightUpToHere += startEditor.getDisplayedSize().getY() + 10;
        finishEditor = new TriggerZoneEditor(map.getFinish(), "Finish Box:", new Vector2D(0, heightUpToHere), 0.95D);
        addChild(finishEditor, PERCENT.SIZE_X, Anchor.TOP_CENTER);

        heightUpToHere += finishEditor.getDisplayedSize().getY();
        cancelButton = new Button("Cancel", new Vector2D(0.025D, heightUpToHere), new Vector2D(60, 20));
        cancelButton.setButtonCallback(mouseButton -> closeWithoutSaving());
        addChild(cancelButton, PERCENT.POS_X, Anchor.TOP_RIGHT);

        heightUpToHere += cancelButton.getDisplayedSize().getY() + 5;
        size.setY(heightUpToHere);
        setSize(size);
    }

    private void save() {
        map.setName(nameField.content);
        startEditor.save();
        finishEditor.save();

        PkMapIO.save(map);
        ((PkMapsGUIScreen) parent).updateMapList();
    }

    @Override
    public void render(Vector2D mousePos) {
        super.render(mousePos);
        Renderer2D.drawHollowRect(getDisplayedPos(), getDisplayedSize(), 1, Color.BLACK);
    }

    @Override
    public void close() {
        save();
        super.close();
    }

    public void closeWithoutSaving() {
        super.close();
    }
}
