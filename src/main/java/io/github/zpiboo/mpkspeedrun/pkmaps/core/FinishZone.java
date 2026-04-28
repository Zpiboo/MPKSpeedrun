package io.github.zpiboo.mpkspeedrun.pkmaps.core;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import org.json.JSONObject;

public class FinishZone extends TriggerZone {
    private long lastLandTick = -1;

    @Override
    public boolean tick(Player player) {
        if (player.landTick)
            lastLandTick = API.tickTime;


        return super.tick(player);
    }

    @Override
    protected void onTrigger(Player player) {
        super.onTrigger(player);

        setTickIndicator(
                player.isOnGround()
                        ? (int) (API.tickTime - lastLandTick)
                        : -player.getAirtime()
        );
    }

    public static FinishZone fromJson(JSONObject boxJson) {
        FinishZone newZone = new FinishZone();
        newZone.applyJson(boxJson);

        return newZone;
    }
}