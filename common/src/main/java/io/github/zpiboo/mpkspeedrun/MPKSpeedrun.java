package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.Main;
import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Minecraft;
import io.github.kurrycat.mpkmod.events.EventAPI;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.modules.MPKModule;
import io.github.kurrycat.mpkmod.util.ClassUtil;
import io.github.zpiboo.mpkspeedrun.parkourmaps.PkMap;
import io.github.zpiboo.mpkspeedrun.parkourmaps.gui.PkMapsGUIScreen;
import io.github.zpiboo.mpkspeedrun.util.FileUtil;
import io.github.zpiboo.mpkspeedrun.util.Proxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

public class MPKSpeedrun implements MPKModule {
    public static final String MODULE_NAME = "mpkspeedrun";
    public static final Logger LOGGER = LogManager.getLogger(MODULE_NAME);

    public void init() {
        addClassesToClassesTxt(new Class[] { Speedrunner.class, PkMap.class });
        FileUtil.init();

        API.registerGUIScreen("maps_gui", new PkMapsGUIScreen());
    }

    public void loaded() {
        Proxy.init(Minecraft.getMcVersion());
        EventAPI.addListener(EventAPI.EventListener.onTickEnd(Speedrunner::onTickEnd));
    }

    public void addClassesToClassesTxt(Class<?>[] classes) {
        try {
            Field classesField = ClassUtil.class.getDeclaredField("classes");
            classesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            final Set<Class<?>> classesTxt = (Set<Class<?>>) classesField.get(null);
            classesTxt.addAll(Arrays.asList(classes));

            Main.infoTree = InfoString.createInfoTree();
        } catch (ReflectiveOperationException e) { e.printStackTrace(); }
    }
}