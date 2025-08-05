package io.github.zpiboo.mpkspeedrun.compatibility.forge_1_8_9.world;

import io.github.kurrycat.mpkmod.util.Vector3D;
import io.github.zpiboo.mpkspeedrun.api.world.IBlock;
import io.github.zpiboo.mpkspeedrun.api.world.IWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class WorldImpl implements IWorld {
    @Override
    public IBlock getBlockAt(Vector3D pos) {
        return new BlockImpl(
                Minecraft.getMinecraft().theWorld.getBlockState(
                        new BlockPos(
                                pos.floor().getXI(),
                                pos.floor().getYI(),
                                pos.floor().getZI()
                        )
                ).getBlock().getLocalizedName()
        );
    }
}
