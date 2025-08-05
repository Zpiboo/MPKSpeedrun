package io.github.zpiboo.mpkspeedrun.api.world;

import io.github.kurrycat.mpkmod.util.Vector3D;

public interface IWorld {
    IBlock getBlockAt(Vector3D pos);
}