package fr.galsaxx.mixin;

import fr.galsaxx.util.ReturnPositionSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ReturnPositionSaver {
    @Unique
    private NbtCompound personnalworld$returnPosition = new NbtCompound();

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void saveReturnPosition(NbtCompound nbt, CallbackInfo ci) {
        nbt.put("pw_returnPosition", personnalworld$returnPosition);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void loadReturnPosition(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("pw_returnPosition")) {
            personnalworld$returnPosition = nbt.getCompound("pw_returnPosition");
        }
    }

    @Override
    public NbtCompound getReturnPosition() {
        return personnalworld$returnPosition;
    }

    @Override
    public void setReturnPosition(NbtCompound nbt) {
        this.personnalworld$returnPosition = nbt;
    }
}
