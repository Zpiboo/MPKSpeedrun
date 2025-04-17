package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.InputConstants;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Keyboard;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.events.OnTickEndEvent;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.Vector2D;
import io.github.zpiboo.mpkspeedrun.parkourmaps.Map;

import java.util.List;

public class Speedrunner {
    @InfoString.AccessInstance
    public static final Speedrunner instance = new Speedrunner();

    private int groundtime = 0;
    private int runTicks = 0;

    private Vector2D inputVector;
    private boolean isMoving = false;

    private static Map currentMap = null;
    private boolean isTimed = false;
    private int timer = 0;


    @InfoString.Getter
    public int getGroundtime() {
        return groundtime;
    }

    @InfoString.Getter
    public int getRunTicks() {
        return runTicks;
    }

    @InfoString.Getter
    public String getTimer() {
        final int seconds = this.timer / 20;
        final int milliseconds = (this.timer % 20)*50;

        String zeroes = "";
        if (milliseconds == 0) zeroes = "00";
        else if (milliseconds == 50) zeroes = "0";

        return seconds + "." + zeroes + milliseconds + "s";
    }


    @InfoString.Getter
    public Map getCurrentMap() {
        return currentMap;
    }
    public void setCurrentMap(Map map) {
        currentMap = map;
    }

    private boolean isTimed() {
        return isTimed;
    }
    private void setTimed(boolean isTimed) {
        this.isTimed = isTimed;
    }

    public int getTimeInTicks() {
        return timer;
    }
    private void incrementTimer() {
        timer++;
    }
    private void resetTimer() {
        timer = 0;
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


        Map pkMap = instance.getCurrentMap();
        if (pkMap == null) return;

        if (instance.isTimed()) {
            instance.incrementTimer();

            boolean shouldFinishMap = pkMap.getFinish().shouldTrigger(currentPlayer);
            if (shouldFinishMap) {
                instance.setTimed(false);
            }
        }

        boolean shouldStartMap = pkMap.getStart().shouldTrigger(currentPlayer);
        if (shouldStartMap) {
            instance.resetTimer();
            instance.setTimed(true);
        }
    }
}
