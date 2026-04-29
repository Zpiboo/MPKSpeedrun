package io.github.zpiboo.mpkspeedrun.pkmaps.core;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import org.json.JSONObject;

public class FinishZone extends TriggerZone {
    private long lastLandTick = -1;

    @Override
    public boolean tick(Player p) {
        if (p.landTick)
            lastLandTick = API.tickTime;


        return super.tick(p);
    }

    @Override
    protected void onTrigger(Player p) {
        super.onTrigger(p);

        setTickIndicator(
                p.isOnGround()
                        ? (int) (API.tickTime - lastLandTick)
                        : -p.getAirtime()
        );
    }

    public static FinishZone fromJson(JSONObject boxJson) {
        FinishZone newZone = new FinishZone();
        newZone.applyJson(boxJson);

        return newZone;
    }
}