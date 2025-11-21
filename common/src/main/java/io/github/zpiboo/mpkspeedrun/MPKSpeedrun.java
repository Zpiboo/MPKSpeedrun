package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.Main;
import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Minecraft;
import io.github.kurrycat.mpkmod.events.EventAPI;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.modules.MPKModule;
import io.github.kurrycat.mpkmod.util.ClassUtil;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.PkMap;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.TriggerZone;
import io.github.zpiboo.mpkspeedrun.pkmaps.gui.screen.PkMapsGUIScreen;
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
        addClassesToClassesTxt(new Class[] {
                Speedrunner.class,
                Speedrunner.Timer.class,
                PkMap.class,
                TriggerZone.class
        });

        FileUtil.registerConfigDir("maps");

        API.registerGUIScreen("maps_gui", new PkMapsGUIScreen());
    }

    public void loaded() {
        if (!Proxy.init(Minecraft.getMcVersion()))
            LOGGER.error(
                    "Failed initializing the version compatibility proxy. " +
                    "Version specific features might not work as expected, if at all."
            );
        EventAPI.addListener(EventAPI.EventListener.onTickEnd(Speedrunner.instance::onTickEnd));
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