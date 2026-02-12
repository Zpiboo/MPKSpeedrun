package io.github.zpiboo.mpkspeedrun.compatibility.fabric_1_21_11.world;

import io.github.kurrycat.mpkmod.util.Vector3D;
import io.github.zpiboo.mpkspeedrun.api.world.IBlock;
import io.github.zpiboo.mpkspeedrun.api.world.IWorld;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static io.github.zpiboo.mpkspeedrun.compatibility.fabric_1_21_11.Constants.MC;

public class WorldImpl implements IWorld {
    @Override
    public IBlock getBlockAt(Vector3D pos) {
        ClientLevel world = MC.level;
        if (world == null) return null;

        BlockState blockState = world.getBlockState(
                new BlockPos(
                        pos.floor().getXI(),
                        pos.floor().getYI(),
                        pos.floor().getZI()
                )
        );
        return new BlockImpl(blockState.getBlock().getName().getString());
    }
}
