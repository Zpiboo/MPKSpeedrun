package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.InputConstants;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Keyboard;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.events.OnTickEndEvent;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;

import java.util.List;

public class Speedrunner {
    @InfoString.AccessInstance
    public static final Speedrunner instance = new Speedrunner();

    public int groundtime = 0;
    public int runTicks = 0;

    public boolean isMoving = false;

    @InfoString.Getter
    public int getGroundtime() {
        return groundtime;
    }
    @InfoString.Getter
    public int getRunTicks() {
        return runTicks;
    }

    public static void onTickEnd(OnTickEndEvent evt) {
        Player currentPlayer = Player.getLatest();
        Player previousPlayer = Player.getBeforeLatest();

        if (currentPlayer == null || previousPlayer == null) return;

        List<Integer> buttons = Keyboard.getPressedButtons();

        boolean wasMoving = instance.isMoving;
        instance.isMoving =
                buttons.contains(InputConstants.KEY_W) ||
                buttons.contains(InputConstants.KEY_A) ||
                buttons.contains(InputConstants.KEY_S) ||
                buttons.contains(InputConstants.KEY_D);

        if (currentPlayer.isOnGround()) {
            instance.groundtime = previousPlayer.isOnGround()
                    ? instance.groundtime + 1
                    : 0;

            if (instance.isMoving)
                if (previousPlayer.isOnGround())
                    if (wasMoving)
                        instance.runTicks += 1;
                    else
                        instance.runTicks = 1;
                else
                    instance.runTicks = 0;
        }
    }
}
