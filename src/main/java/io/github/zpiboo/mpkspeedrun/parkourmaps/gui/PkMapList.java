package io.github.zpiboo.mpkspeedrun.parkourmaps.gui;

import java.util.ArrayList;
import java.util.List;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.FontRenderer;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Renderer2D;
import io.github.kurrycat.mpkmod.gui.components.*;
import io.github.kurrycat.mpkmod.gui.components.Button;
import io.github.kurrycat.mpkmod.gui.components.Component;
import io.github.kurrycat.mpkmod.gui.interfaces.MouseInputListener;
import io.github.kurrycat.mpkmod.util.*;
import io.github.zpiboo.mpkspeedrun.MPKSpeedrun;
import io.github.zpiboo.mpkspeedrun.Speedrunner;
import io.github.zpiboo.mpkspeedrun.parkourmaps.Map;
import io.github.zpiboo.mpkspeedrun.parkourmaps.TriggerZone;
import io.github.zpiboo.mpkspeedrun.parkourmaps.TriggerZone.TriggerMode;
import io.github.zpiboo.mpkspeedrun.util.components.RadioButton;
import io.github.zpiboo.mpkspeedrun.util.components.RadioButtonGroup;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;

public class PkMapList extends ScrollableList<PkMapList.PkMapItem> {
    public List<Map> maps;
    private final RadioButtonGroup radioGroup;

    public Button addMap;

    public PkMapList(Vector2D pos, Vector2D size, List<Map> maps) {
        this.maps = maps;

        radioGroup = new RadioButtonGroup();

        setPos(pos);
        setSize(size);
        setTitle("Parkour Maps");

        addMap = new Button("Add Map", new Vector2D(0, 0), new Vector2D(60, 20), mouseButton -> {
            if (mouseButton != Mouse.Button.LEFT) return;
            Map newMap = new Map(
                Map.getDefaultName(),
                new TriggerZone(),
                new TriggerZone()
            );
            maps.add(newMap);
            newMap.save();
            items.add(new PkMapItem(this, newMap));
        });
        bottomCover.setHeight(24, false);
        bottomCover.backgroundColor = null;
        bottomCover.addChild(addMap, PERCENT.NONE, Anchor.CENTER);

        updateList();
    }

    public void updateList() {
        items.clear();
        for (Map map : maps) {
            items.add(new PkMapItem(this, map));
        }
    }

    @Override
    public void render(Vector2D mouse) {
        super.render(mouse);
        components.forEach(c -> c.render(mouse));
    }

    public class PkMapItem extends ScrollableListItem<PkMapItem> {
        private final Map map;
        public boolean collapsed = true;

        public InputField nameField;
        public RadioButton selectedBtn;
        public Button collapseBtn;
        public Button deleteBtn;
        public List<InputField> startFields = new ArrayList<>();
        public List<InputField> finishFields = new ArrayList<>();
        public Button startModeBtn;
        public Button finishModeBtn;

        public PkMapItem(ScrollableList<PkMapItem> parent, Map map) {
            super(parent);
            this.map = map;

            nameField = new InputField(map.getName(), new Vector2D(21, 5), 170)
                .setName("Name: ")
                .setOnContentChange(c -> {
                    String newName = c.getContent();
                    if (!Files.exists(Map.getFilePath(newName))) {
                        try {
                            Files.move(map.getFilePath(), Map.getFilePath(newName));
                        } catch (IOException e) {
                            MPKSpeedrun.LOGGER.error("Failed to rename map file to {}: {} - {}", newName, map.getFilePath(), e.getMessage(), e);
                        }
                        map.setName(newName);
                    }
                });
            addChild(nameField);

            selectedBtn = new RadioButton(Vector2D.ZERO, radioGroup, false, checked -> {
                if (checked)
                    Speedrunner.instance.setCurrentMap(map);
                else
                    Speedrunner.instance.setCurrentMap(null);
            });
            addChild(selectedBtn);
            selectedBtn.setPos(new Vector2D(5, 5));

            TriggerZone mapStart = map.getStart();
            TriggerZone mapFinish = map.getFinish();
            BoundingBox3D startZone = mapStart.getZone();
            BoundingBox3D finishZone = mapFinish.getZone();
            for (FieldName fieldName : FieldName.values()) {
                InputField startField = createInputField(startZone, fieldName);
                startFields.add(startField);
                setupField(startField);
                addChild(startField);

                InputField finishField = createInputField(finishZone, fieldName);
                finishFields.add(finishField);
                setupField(finishField);
                addChild(finishField);
            }
            final Vector2D modeBtnSize = new Vector2D(
                5 + startFields.get(0).getDisplayedSize().getX()
                  + startFields.get(3).getDisplayedSize().getX(),
                11
            );
            startModeBtn = new Button(mapStart.getMode().toString(), Vector2D.ZERO, modeBtnSize, mouseButton -> {
                if (mouseButton != Mouse.Button.LEFT) return;
                TriggerMode newStartMode = mapStart.getMode().getNext();
                mapStart.setMode(newStartMode);
                startModeBtn.setText(newStartMode.toString());
            });
            addChild(startModeBtn);
            startModeBtn.setPos(new Vector2D(16, 90));

            finishModeBtn = new Button(mapFinish.getMode().toString(), Vector2D.ZERO, modeBtnSize, mouseButton -> {
                if (mouseButton != Mouse.Button.LEFT) return;
                TriggerMode newFinishMode = mapFinish.getMode().getNext();
                mapFinish.setMode(newFinishMode);
                finishModeBtn.setText(newFinishMode.toString());
            });
            addChild(finishModeBtn, PERCENT.NONE, Anchor.TOP_LEFT, Anchor.TOP_CENTER);
            finishModeBtn.setPos(new Vector2D(0, 90));

            collapseBtn = new Button("v", Vector2D.ZERO, new Vector2D(11, 11), mouseButton -> {
                if (mouseButton != Mouse.Button.LEFT) return;
                collapsed = !collapsed;
                collapseBtn.setText(collapsed ? "v" : "^");
                collapseBtn.textOffset = collapsed ? Vector2D.ZERO : new Vector2D(0, 3);
            });
            addChild(collapseBtn, PERCENT.NONE, Anchor.TOP_RIGHT);
            collapseBtn.setPos(new Vector2D(21, 5));

            deleteBtn = new Button("x", Vector2D.ZERO, new Vector2D(11, 11), mouseButton -> {
                if (mouseButton != Mouse.Button.LEFT) return;
                maps.remove(map);
                try {
                    Files.deleteIfExists(map.getFilePath());
                } catch (IOException e) {
                    MPKSpeedrun.LOGGER.error("Failed to delete map file: {} - {}", map.getFilePath(), e.getMessage(), e);
                }
                items.remove(this);
            });
            addChild(deleteBtn, PERCENT.NONE, Anchor.TOP_RIGHT);
            deleteBtn.setPos(new Vector2D(5, 5));
        }

        @Override public int getHeight() { return collapsed ? 21 : 106; }

        public void render(int index, Vector2D pos, Vector2D size, Vector2D mouse) {
            Renderer2D.drawRectWithEdge(pos, size, 1, new Color(31, 31, 31, 150), new Color(255, 255, 255, 95));

            selectedBtn.render(mouse);

            if (collapsed)
                FontRenderer.drawLeftCenteredString(
                    map.getName(),
                    nameField.getDisplayedPos().add(0, 6),
                    Color.WHITE,
                    false
                );

            collapseBtn.render(mouse);
            deleteBtn.render(mouse);

            if (collapsed) return;

            nameField.render(mouse);

            final double halfSize = getDisplayedSize().getX() / 2;
            FontRenderer.drawString("Start Box:", pos.add(16, 26), Color.WHITE, false);
            renderFields(startFields, new Vector2D(16, 42), mouse);
            FontRenderer.drawString("Finish Box:", pos.add(halfSize, 26), Color.WHITE, false);
            renderFields(finishFields, new Vector2D(halfSize, 42), mouse);

            startModeBtn.render(mouse);
            finishModeBtn.render(mouse);
        }

        @Override
        public boolean handleMouseInput(Mouse.State state, Vector2D mousePos, Mouse.Button button) {
            final ArrayList<Component> components = new ArrayList<>();
            components.add(selectedBtn);
            components.add(collapseBtn);
            components.add(deleteBtn);
            if (!collapsed) {
                components.add(nameField);
                components.addAll(startFields);
                components.addAll(finishFields);
                components.add(startModeBtn);
                components.add(finishModeBtn);
            }

            return ItrUtil.orMapAll(
                ItrUtil.getAllOfType(MouseInputListener.class, components),
                ele -> ele.handleMouseInput(state, mousePos, button)
            );
        }

        private InputField createInputField(BoundingBox3D zone, FieldName fieldName) {
            return new InputField(getFieldValue(zone, fieldName), Vector2D.ZERO, 0, true)
                .setName(fieldName + ": ")
                .setOnContentChange(c -> {
                    if (c.getNumber() != null) setFieldValue(zone, fieldName, c.getNumber());
                });
        }

        private void setupField(InputField field) {
            field.setWidth(FontRenderer.getStringSize(field.name).getX() + 60);
        }
        private void renderFields(List<InputField> fields, Vector2D offset, Vector2D mouse) {
            double xOffset = fields.get(0).getDisplayedSize().getX() + 5;

            for (int i = 0; i < fields.size(); i++) {
                InputField field = fields.get(i);
                field.setPos(offset.add(
                    xOffset*(i / 3),
                    16*(i % 3)
                ));
                field.render(mouse);
            }
        }

        private String getFieldValue(BoundingBox3D bb, FieldName fieldName) {
            switch (fieldName) {
                case MIN_X: return String.valueOf(bb.getMin().getX());
                case MIN_Y: return String.valueOf(bb.getMin().getY());
                case MIN_Z: return String.valueOf(bb.getMin().getZ());
                case MAX_X: return String.valueOf(bb.getMax().getX());
                case MAX_Y: return String.valueOf(bb.getMax().getY());
                case MAX_Z: return String.valueOf(bb.getMax().getZ());
                default: throw new IllegalArgumentException("Invalid field name: " + fieldName);
            }
        }

        private void setFieldValue(BoundingBox3D bb, FieldName fieldName, double value) {
            switch (fieldName) {
                case MIN_X: bb.setMinX(value); break;
                case MIN_Y: bb.setMinY(value); break;
                case MIN_Z: bb.setMinZ(value); break;
                case MAX_X: bb.setMaxX(value); break;
                case MAX_Y: bb.setMaxY(value); break;
                case MAX_Z: bb.setMaxZ(value); break;
                default: throw new IllegalArgumentException("Invalid field name: " + fieldName);
            }
        }
    }

    private enum FieldName {
        MIN_X("minX"), MIN_Y("minY"), MIN_Z("minZ"),
        MAX_X("maxX"), MAX_Y("maxY"), MAX_Z("maxZ");

        private final String strName;
        FieldName(String strName) {
            this.strName = strName;
        }
        @Override
        public String toString() {
            return strName;
        }
    }
}