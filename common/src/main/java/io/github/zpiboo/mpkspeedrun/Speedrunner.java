package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.KeyBinding;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.events.OnTickEndEvent;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.FormatDecimals;
import io.github.kurrycat.mpkmod.util.MathUtil;
import io.github.zpiboo.mpkspeedrun.parkourmaps.PkMap;

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
        return currentMap;
    }
    public void setCurrentMap(PkMap map) {
        currentMap = map;
        timer.setTimeInTicks(0);
        timer.setEnabled(false);
    }


    public void onTickEnd(@SuppressWarnings("unused") OnTickEndEvent evt) {
        final Player currentPlayer = Player.getLatest();
        final Player previousPlayer = Player.getBeforeLatest();

        if (currentPlayer == null || previousPlayer == null) return;

        int inputX = 0, inputY = 0;
        if (KeyBinding.getByName("key.forward").isKeyDown()) inputY = 1;
        if (KeyBinding.getByName("key.left").isKeyDown()) inputX = -1;
        if (KeyBinding.getByName("key.back").isKeyDown()) inputY -= 1;
        if (KeyBinding.getByName("key.right").isKeyDown()) inputX += 1;

        final boolean wasMoving = isMoving;
        isMoving = inputX != 0 || inputY != 0;

        if (currentPlayer.isOnGround()) {
            groundtime = previousPlayer.isOnGround()
                    ? groundtime + 1
                    : 0;

            if (isMoving)
                if (previousPlayer.isOnGround())
                    if (wasMoving)
                        runTicks += 1;
                    else
                        runTicks = 1;
                else
                    runTicks = 0;
        }


        final PkMap pkMap = getCurrentMap();
        if (pkMap == null) return;

        if (timer.isEnabled()) {
            boolean shouldFinishMap = pkMap.getFinish().tick(currentPlayer);
            if (shouldFinishMap)
                timer.setEnabled(false);
            else
                timer.increment();
        }

        boolean shouldStartMap = pkMap.getStart().tick(currentPlayer);
        if (shouldStartMap) {
            timer.reset();
            timer.setEnabled(true);
        }
    }

    @InfoString.DataClass
    public class Timer implements FormatDecimals {
        private int timeInTicks = 0;
        private boolean enabled = false;

        @InfoString.Getter
        public int getTimeInTicks() {
            return timeInTicks;
        }
        public void setTimeInTicks(int timeInTicks) {
            this.timeInTicks = timeInTicks;
        }

        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void reset() {
            setTimeInTicks(getCurrentMap().getStartTime());
        }
        public void increment() {
            setTimeInTicks(getTimeInTicks() + 1);
        }

        @Override
        public String formatDecimals(int decimals, boolean keepZeros) {
            return MathUtil.formatDecimals((double) getTimeInTicks() / 20, decimals, keepZeros) + "s";
        }
    }
}
