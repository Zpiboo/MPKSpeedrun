package io.github.zpiboo.mpkspeedrun.parkourmaps.gui;

import io.github.kurrycat.mpkmod.gui.ComponentScreen;
import io.github.kurrycat.mpkmod.gui.components.Anchor;
import io.github.kurrycat.mpkmod.gui.components.Button;
import io.github.kurrycat.mpkmod.util.Vector2D;
import io.github.zpiboo.mpkspeedrun.parkourmaps.PkMap;
import io.github.zpiboo.mpkspeedrun.util.FileUtil;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

public class PkMapsGUIScreen extends ComponentScreen {
    private final Set<PkMap> maps = new TreeSet<>();
    private PkMapList mapList;

    @Override public boolean resetOnOpen() { return false; }
    @Override public boolean shouldCreateKeyBind() { return true; }

    @Override
    public void onGuiInit() {
        super.onGuiInit();

        loadMaps();
        mapList = new PkMapList(
                new Vector2D(0, 0.05),
                new Vector2D(0.6, 0.9),
                maps
        );
        addChild(mapList, PERCENT.ALL, Anchor.TOP_CENTER);
        mapList.topCover.addChild(
                new Button(
                        "x",
                        new Vector2D(5, 1),
                        new Vector2D(11, 11),
                        mouseButton -> close()
                ),
                PERCENT.NONE, Anchor.CENTER_RIGHT
        );
    }
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        for (PkMap pkMap : mapList.maps)
            pkMap.save();
        mapList.updateList();
    }

    public void loadMaps() {
        maps.clear();

        File[] files = FileUtil.MAP_FOLDER.listFiles((dir, filename) -> filename.endsWith(".json"));
        if (files == null) return;

        for (File mapFile : files) {
            PkMap pkMap = PkMap.load(mapFile.getName().substring(0, mapFile.getName().length() - 5));
            if (pkMap != null) maps.add(pkMap);
        }
    }
}