package io.github.zpiboo.mpkspeedrun;

import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.FormatDecimals;
import io.github.kurrycat.mpkmod.util.MathUtil;

@InfoString.DataClass
public class Timer implements FormatDecimals {
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
        return MathUtil.formatDecimals((double) getTimeInTicks() / 20, decimals, keepZeros);
    }

    @InfoString.Getter
    public SubtickTimerFormat getWithSubtick() {
        return subtickTimer;
    }

    public class SubtickTimerFormat implements FormatDecimals {
        @Override
        public String formatDecimals(int decimals, boolean keepZeros) {
            return MathUtil.formatDecimals(
                    (getTimeInTicks() + subtick) / 20,
                    decimals,
                    keepZeros
            );
        }
    }
}