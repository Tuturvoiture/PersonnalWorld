package fr.galsaxx.util;

import net.minecraft.nbt.NbtCompound;

public interface ReturnPositionSaver {
    NbtCompound getReturnPosition();
    void setReturnPosition(NbtCompound nbt);
}
