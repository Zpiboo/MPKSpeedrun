package io.github.zpiboo.mpkspeedrun.compatibility.fabric_1_20_4.world;

import io.github.kurrycat.mpkmod.util.Vector3D;
import io.github.zpiboo.mpkspeedrun.api.world.IBlock;
import io.github.zpiboo.mpkspeedrun.api.world.IWorld;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

import static io.github.zpiboo.mpkspeedrun.compatibility.fabric_1_20_4.Constants.MC;

public class WorldImpl implements IWorld {
    @Override
    public IBlock getBlockAt(Vector3D pos) {
        ClientWorld world = MC.world;
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
