package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.multipart.MultipartEntityLevel;
import com.github.alexmodguy.alexscaves.server.entity.util.ACMultipartEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class ServerLevelMultipartMixin {

    @Inject(method = "getEntity", at = @At("RETURN"), cancellable = true)
    private void alexscaves$getEntity(int id, CallbackInfoReturnable<Entity> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }
        ACMultipartEntity<?> part = ((MultipartEntityLevel) this).alexscaves$getPartEntityMap().get(id);
        if (part != null) {
            cir.setReturnValue(part);
        }
    }

    @Inject(method = "getEntityOrPart", at = @At("RETURN"), cancellable = true)
    private void alexscaves$getEntityOrPart(int id, CallbackInfoReturnable<Entity> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }
        ACMultipartEntity<?> part = ((MultipartEntityLevel) this).alexscaves$getPartEntityMap().get(id);
        if (part != null) {
            cir.setReturnValue(part);
        }
    }
}