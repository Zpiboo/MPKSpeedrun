package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.InputConstants;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Keyboard;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.events.OnTickEndEvent;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.util.List;

public class SpeedrunLabels {
    @InfoString.AccessInstance
    public static final SpeedrunLabels instance = new SpeedrunLabels();

    private int groundtime = 0;
    private int runTicks = 0;

    private Vector2D inputVector;
    private boolean isMoving = false;

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

        int inputX = 0, inputY = 0;
        List<Integer> buttons = Keyboard.getPressedButtons();
        if (buttons.contains(InputConstants.KEY_W)) inputY = 1;
        if (buttons.contains(InputConstants.KEY_A)) inputX = -1;
        if (buttons.contains(InputConstants.KEY_S)) inputY -= 1;
        if (buttons.contains(InputConstants.KEY_D)) inputX += 1;
        instance.inputVector = new Vector2D(inputX, inputY);

        boolean wasMoving = instance.isMoving;
        instance.isMoving = !instance.inputVector.equals(Vector2D.ZERO);

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
