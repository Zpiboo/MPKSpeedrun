package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.events.OnTickEndEvent;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;

public class SpeedrunLabels {
    @InfoString.AccessInstance
    public static final SpeedrunLabels instance = new SpeedrunLabels();

    public static void onTickEnd(OnTickEndEvent evt) {}
}
