package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.events.OnTickStartEvent;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.FormatDecimals;
import io.github.kurrycat.mpkmod.util.MathUtil;
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
        return currentMap;
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
        if (pkMap != null) {
            if (timer.isEnabled()) {
                boolean shouldFinishMap = pkMap.getFinish().tick(currPlayer);
                if (shouldFinishMap) {
                    timer.setEnabled(false);
                    timer.setSubtick(timer.getSubtick() + getCurrentMap().getFinish().getSubtick());
                } else {
                    timer.increment();
                }
            }

            boolean shouldStartMap = pkMap.getStart().tick(currPlayer);
            if (shouldStartMap) {
                timer.setTimeInTicks(getCurrentMap().getStartTime());
                timer.setSubtick(-getCurrentMap().getStart().getSubtick());
                timer.setEnabled(true);
            }
        }

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

    @InfoString.DataClass
    public static class Timer implements FormatDecimals {
        private int timeInTicks = 0;
        private double subtick = 0.0D;
        private final SubtickTimerFormat subtickTimer = new SubtickTimerFormat();
        private boolean enabled = false;

        @InfoString.Getter
        public int getTimeInTicks() {
            return timeInTicks;
        }
        public void setTimeInTicks(int timeInTicks) {
            this.timeInTicks = timeInTicks;
        }

        @InfoString.Getter
        public double getSubtick() {
            return subtick;
        }
        public void setSubtick(double subtick) {
            this.subtick = subtick;
        }

        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void reset() {
            setTimeInTicks(0);
            setSubtick(0.0D);
        }
        public void increment() {
            setTimeInTicks(getTimeInTicks() + 1);
        }

        @Override
        public String formatDecimals(int decimals, boolean keepZeros) {
            return MathUtil.formatDecimals((double) getTimeInTicks() / 20, decimals, keepZeros) + "s";
        }

        @InfoString.Getter
        public SubtickTimerFormat getWithSubtick() {
            return subtickTimer;
        }

        public class SubtickTimerFormat implements FormatDecimals {
            @Override
            public String formatDecimals(int decimals, boolean keepZeros) {
                return MathUtil.formatDecimals(
                        (double) getTimeInTicks() / 20 + subtick * 0.05D,
                        decimals,
                        keepZeros
                ) + "s";
            }
        }
    }
}
