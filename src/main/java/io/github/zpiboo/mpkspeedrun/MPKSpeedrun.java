package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.Main;
import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.events.EventAPI;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.modules.MPKModule;
import io.github.kurrycat.mpkmod.util.ClassUtil;
import io.github.zpiboo.mpkspeedrun.parkourmaps.Map;
import io.github.zpiboo.mpkspeedrun.parkourmaps.gui.PkMapsGUIScreen;
import io.github.zpiboo.mpkspeedrun.util.FileUtil;

import java.lang.reflect.Field;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MPKSpeedrun implements MPKModule {
    public static final String MODULE_NAME = "mpkspeedrun";
    public static final Logger LOGGER = LogManager.getLogger(MODULE_NAME);

    public void init() {
        try {
            Field classesField = ClassUtil.class.getDeclaredField("classes");
            classesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Set<Class<?>> classes = (Set<Class<?>>) classesField.get(null);

            classes.add(Speedrunner.class);
            classes.add(Map.class);

            Main.infoTree = InfoString.createInfoTree();
        } catch (ReflectiveOperationException e) { e.printStackTrace(); }

        FileUtil.init();

        API.registerGUIScreen("maps_gui", new PkMapsGUIScreen());
    }

    public void loaded() {
        EventAPI.addListener(EventAPI.EventListener.onTickEnd(Speedrunner::onTickEnd));
    }
}