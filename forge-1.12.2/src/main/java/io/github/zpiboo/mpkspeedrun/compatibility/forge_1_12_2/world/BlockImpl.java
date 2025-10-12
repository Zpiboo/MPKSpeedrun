package io.github.zpiboo.mpkspeedrun.compatibility.forge_1_12_2.world;

import io.github.zpiboo.mpkspeedrun.api.world.IBlock;

public class BlockImpl implements IBlock {
    private final String id;

    public BlockImpl(String id) {
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
    }
}
