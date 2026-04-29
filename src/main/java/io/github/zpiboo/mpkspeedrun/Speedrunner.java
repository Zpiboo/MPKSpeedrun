package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.events.OnTickStartEvent;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.zpiboo.mpkspeedrun.pkmaps.core.PkMap;
import io.github.zpiboo.mpkspeedrun.util.misc.KeyBindings;

public class Speedrunner {
    @InfoString.AccessInstance
    public static final Speedrunner instance = new Speedrunner();

    private int groundtime = 0;
    private int runTicks = 0;

    private boolean isMoving = false;

    private PkMap currentMap = null;
    private final Timer timer = new Timer();


    @InfoString.Getter
    public int getGroundtime() {
        return groundtime;
    }

    @InfoString.Getter
    public int getRunTicks() {
        return runTicks;
    }

    @InfoString.Getter
    public Timer getTimer() {
        return timer;
    }


    @InfoString.Getter
    public PkMap getCurrentMap() {
        return currentMap != null
                ? currentMap
                : PkMap.NONE;
    }
    public void setCurrentMap(PkMap map) {
        currentMap = map;
        timer.reset();
        timer.setEnabled(false);
    }


    public void onTickStart(@SuppressWarnings("unused") OnTickStartEvent evt) {
        Player currPlayer = Player.getLatest();
        if (currPlayer == null) return;

        final PkMap pkMap = getCurrentMap();
        if (pkMap != null) pkMap.tick(currPlayer, this);

        Player prevPlayer = currPlayer.getPrevious();
        if (prevPlayer == null) return;

        int inputX = 0, inputY = 0;
        if (KeyBindings.KEY_FORWARD.isKeyDown()) inputY = 1;
        if (KeyBindings.KEY_LEFT.isKeyDown()) inputX = 1;
        if (KeyBindings.KEY_BACK.isKeyDown()) inputY -= 1;
        if (KeyBindings.KEY_RIGHT.isKeyDown()) inputX -= 1;

        boolean wasMoving = isMoving;
        isMoving = inputX != 0 || inputY != 0;

        boolean isOnGround = currPlayer.isOnGround();
        boolean wasOnGround = prevPlayer.isOnGround();

        if (isOnGround) {
            if (wasOnGround)
                groundtime++;
            else
                groundtime = 0;

            if (isMoving) {
                if (wasMoving)
                    runTicks++;
                if (!wasOnGround || !wasMoving)
                    runTicks = 0;
            } else if (wasOnGround && wasMoving) {
                runTicks++;
            }
        }
    }
}
