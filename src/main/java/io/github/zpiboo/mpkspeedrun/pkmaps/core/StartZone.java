package io.github.zpiboo.mpkspeedrun.pkmaps.core;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.zpiboo.mpkspeedrun.Speedrunner;
import org.json.JSONObject;

public class StartZone extends TriggerZone {
    private TriggerState lastTriggerState = TriggerState.NONE;
    private long lastTriggerTime = -1;

    @Override
    public boolean tick(Player player) {
        boolean shouldTrigger = super.tick(player);

        if (lastTriggerState == TriggerState.GROUNDED) {
            if (!player.isOnGround()) {
                lastTriggerState = TriggerState.NONE;
            } else {
                setTickIndicator((int) (API.tickTime - lastTriggerTime));  // positive
            }
        } else if (lastTriggerState == TriggerState.AIRBORNE) {
            if (player.isOnGround()) {
                lastTriggerState = TriggerState.NONE;
            }
            setTickIndicator((int) (lastTriggerTime - API.tickTime));  // negative
        }

        return shouldTrigger;
    }

    @Override
    protected void onTrigger(Player player) {
        super.onTrigger(player);

        lastTriggerState = player.isOnGround()
                ? TriggerState.GROUNDED
                : TriggerState.AIRBORNE;

        lastTriggerTime = API.tickTime;
    }

    public static StartZone fromJson(JSONObject boxJson) {
        StartZone newZone = new StartZone();
        newZone.applyJson(boxJson);

        return newZone;
    }
}