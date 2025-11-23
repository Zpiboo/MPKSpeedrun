package io.github.zpiboo.mpkspeedrun.pkmaps.gui.screen;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.FontRenderer;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Renderer2D;
import io.github.kurrycat.mpkmod.gui.components.Anchor;
import io.github.kurrycat.mpkmod.gui.components.Button;
import io.github.kurrycat.mpkmod.gui.components.ScrollableList;
import io.github.kurrycat.mpkmod.gui.components.ScrollableListItem;
import io.github.kurrycat.mpkmod.util.Mouse;
import io.github.kurrycat.mpkmod.util.Vector2D;
import io.github.zpiboo.mpkspeedrun.MPKSpeedrun;
import io.github.zpiboo.mpkspeedrun.Speedrunner;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.PkMap;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.TriggerZone;
import io.github.zpiboo.mpkspeedrun.pkmaps.io.PkMapIO;
import io.github.zpiboo.mpkspeedrun.util.components.RadioButton;
import io.github.zpiboo.mpkspeedrun.util.components.RadioButtonGroup;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class PkMapList extends ScrollableList<PkMapList.PkMapItem> {
    public final Set<PkMap> maps;
    private final RadioButtonGroup radioGroup;

    private final Button addMap;

    public PkMapList(Vector2D pos, Vector2D size, Set<PkMap> maps) {
        this.maps = maps;

        radioGroup = new RadioButtonGroup();

        setPos(pos);
        setSize(size);
        setTitle("Parkour Maps");

        addMap = new Button("Add Map", new Vector2D(0, 0), new Vector2D(60, 20), mouseButton -> {
            if (mouseButton != Mouse.Button.LEFT) return;
            PkMap newMap = new PkMap(
                PkMap.DEFAULT_NAME,
                new TriggerZone(),
                new TriggerZone(),
                null
            );
            maps.add(newMap);
            PkMapIO.save(newMap);
            updateComponents();
        });
        bottomCover.setHeight(24, false);
        bottomCover.backgroundColor = null;
        bottomCover.addChild(addMap, PERCENT.NONE, Anchor.CENTER);

        updateComponents();
    }

    public void updateComponents() {
        Set<PkMap> mapsTmp = new TreeSet<>(maps);
        maps.clear();
        for (PkMap map : mapsTmp)
            maps.add(map);

        items.clear();
        for (PkMap map : maps)
            items.add(new PkMapItem(this, map));
    }

    public class PkMapItem extends ScrollableListItem<PkMapItem> {
        private final PkMap map;

        public final RadioButton selectedBtn;
        public Button configPaneBtn;

        public final Button deleteBtn;

        public PkMapItem(ScrollableList<PkMapItem> parent, PkMap map) {
            super(parent);
            this.map = map;

            final PkMap currentMap = Speedrunner.instance.getCurrentMap();
            boolean isCurrentMap = currentMap != null && Objects.equals(currentMap.getUUID(), map.getUUID());
            if (isCurrentMap)
                Speedrunner.instance.setCurrentMap(map);
            selectedBtn = new RadioButton(Vector2D.ZERO, radioGroup, isCurrentMap, checked -> {
                if (checked)
                    Speedrunner.instance.setCurrentMap(map);
                else
                    Speedrunner.instance.setCurrentMap(null);
            });
            addChild(selectedBtn);
            selectedBtn.setPos(new Vector2D(5, 5));

            configPaneBtn = new Button("Config", Vector2D.ZERO, new Vector2D(50, 11), mouseButton -> {
                ((PkMapsGUIScreen) PkMapList.this.parent).openPane(map.createConfigPane(PkMapList.this.parent));
            });
            addChild(configPaneBtn, PERCENT.NONE, Anchor.CENTER_LEFT);

            deleteBtn = new Button("x", Vector2D.ZERO, new Vector2D(11, 11), mouseButton -> {
                if (mouseButton != Mouse.Button.LEFT) return;
                maps.remove(map);
                try {
                    Files.deleteIfExists(PkMapIO.getFilePath(map));
                } catch (IOException e) {
                    MPKSpeedrun.LOGGER.error("Failed to delete map file: " + PkMapIO.getFilePath(map) + " - " + e.getMessage(), e);
                }
                items.remove(this);
            });
            addChild(deleteBtn, PERCENT.NONE, Anchor.CENTER_RIGHT);
            deleteBtn.setPos(new Vector2D(5, 0));
        }

        @Override
        public int getHeight() {
            return 21;
        }

        public void render(int index, Vector2D pos, Vector2D size, Vector2D mouse) {
            Renderer2D.drawRectWithEdge(pos, size, 1, new Color(31, 31, 31, 150), new Color(255, 255, 255, 95));

            selectedBtn.render(mouse);

            FontRenderer.drawLeftCenteredString(
                    map.getName(),
                    getDisplayedPos().add(21, 11),  // nameField.getDisplayedPos().add(0, 6),
                    Color.WHITE,
                    false
            );

            configPaneBtn.setPos(new Vector2D(FontRenderer.getStringSize(map.getName()).getX() + 26, 0));
            configPaneBtn.render(mouse);

            deleteBtn.render(mouse);
        }
    }
}