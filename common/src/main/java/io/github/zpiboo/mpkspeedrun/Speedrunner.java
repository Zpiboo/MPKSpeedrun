package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.KeyBinding;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.events.OnTickEndEvent;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.zpiboo.mpkspeedrun.parkourmaps.PkMap;

public class Speedrunner {
    @InfoString.AccessInstance
    public static final Speedrunner instance = new Speedrunner();

    private int groundtime = 0;
    private int runTicks = 0;

    private boolean isMoving = false;

    private static PkMap currentMap = null;
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
        final int seconds = getTimeInTicks() / 20;
        final int milliseconds = (getTimeInTicks() % 20)*50;

        String zeroes = "";
        if (milliseconds == 0) zeroes = "00";
        else if (milliseconds == 50) zeroes = "0";

        return seconds + "." + zeroes + milliseconds + "s";
    }


    @InfoString.Getter
    public PkMap getCurrentMap() {
        return currentMap;
    }
    public void setCurrentMap(PkMap map) {
        currentMap = map;
        timer = 0;
        setTimed(false);
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
        timer = getCurrentMap().getStartTime();
    }


    public static void onTickEnd(@SuppressWarnings("unused") OnTickEndEvent evt) {
        final Player currentPlayer = Player.getLatest();
        final Player previousPlayer = Player.getBeforeLatest();

        if (currentPlayer == null || previousPlayer == null) return;

        int inputX = 0, inputY = 0;
        if (KeyBinding.getByName("key.forward").isKeyDown()) inputY = 1;
        if (KeyBinding.getByName("key.left").isKeyDown()) inputX = -1;
        if (KeyBinding.getByName("key.back").isKeyDown()) inputY -= 1;
        if (KeyBinding.getByName("key.right").isKeyDown()) inputX += 1;

        final boolean wasMoving = instance.isMoving;
        instance.isMoving = inputX != 0 || inputY != 0;

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


        final PkMap pkMap = instance.getCurrentMap();
        if (pkMap == null) return;

        if (instance.isTimed()) {
            boolean shouldFinishMap = pkMap.getFinish().tick(currentPlayer);
            if (shouldFinishMap)
                instance.setTimed(false);
            else
                instance.incrementTimer();
        }

        boolean shouldStartMap = pkMap.getStart().tick(currentPlayer);
        if (shouldStartMap) {
            instance.resetTimer();
            instance.setTimed(true);
        }
    }
}
