package io.github.zpiboo.mpkspeedrun.compatibility.fabric_1_20_6.world;

import io.github.zpiboo.mpkspeedrun.api.world.IBlock;

public class BlockImpl implements IBlock {
    private final String id;

    public BlockImpl(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }
}
