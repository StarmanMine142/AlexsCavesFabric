package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.fabric.MultipartEntityLevel;
import com.github.alexmodguy.alexscaves.server.entity.util.ACMultipartEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {

    protected ClientLevelMixin(WritableLevelData data, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionType, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(data, dimension, registryAccess, dimensionType, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void ac_getSkyColor_timeOfDay(Vec3 position, float partialTick, CallbackInfoReturnable<Vec3> cir) {
        if (AlexsCaves.CLIENT_CONFIG.biomeSkyOverrides) {
            if (ClientProxy.acSkyOverrideAmount > 0.0F) {
                Vec3 prevVec3 = cir.getReturnValue();
                Vec3 sampledVec3 = ClientProxy.acSkyOverrideColor;
                sampledVec3 = ClientProxy.processSkyColor(sampledVec3, partialTick);
                cir.setReturnValue(prevVec3.add(sampledVec3.subtract(prevVec3).scale(ClientProxy.acSkyOverrideAmount)));
            }
        }
    }

    @Inject(method = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyDarken(F)F",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void ac_getSkyDarken_timeOfDay(float partialTick, CallbackInfoReturnable<Float> cir) {
        if (AlexsCaves.CLIENT_CONFIG.biomeSkyOverrides) {
            float skyDarken = cir.getReturnValue();
            if (ClientProxy.acSkyOverrideAmount > 0.0F) {
                cir.setReturnValue(Math.max(skyDarken, ClientProxy.acSkyOverrideAmount));
            }
        }
    }

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
}