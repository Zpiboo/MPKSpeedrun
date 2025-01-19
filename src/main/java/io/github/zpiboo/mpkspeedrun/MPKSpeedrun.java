package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.events.EventAPI;
import io.github.kurrycat.mpkmod.modules.MPKModule;
import io.github.zpiboo.mpkspeedrun.util.InfoString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MPKSpeedrun implements MPKModule {
    public static final String MODULE_NAME = "mpkspeedrun";
    public static final Logger LOGGER = LogManager.getLogger(MODULE_NAME);

    public void init() {
        InfoString.loadInfoVars(Speedrunner.class);
    }

    public void loaded() {
        EventAPI.addListener(EventAPI.EventListener.onTickEnd(Speedrunner::onTickEnd));
    }
}